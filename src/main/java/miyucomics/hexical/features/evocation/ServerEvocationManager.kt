package miyucomics.hexical.features.evocation

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.misc.CastingUtils
import miyucomics.hexical.misc.HexicalNetworking
import miyucomics.hexical.misc.HexicalNetworking.AbstractPacket
import miyucomics.hexical.misc.HexicalNetworking.SimpleChannelWrapper
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkDirection
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.resources.ResourceLocation
import net.minecraft.client.Minecraft
import java.util.function.Supplier
import java.util.UUID

object ServerEvocationManager {
	const val EVOKE_DURATION: Int = 20

	fun initNetworking(wrapper: SimpleChannelWrapper) {
		wrapper.channel.messageBuilder<EvokeStatePacket>(EvokeStatePacket::class.java, wrapper.nextId())
            .encoder(EvokeStatePacket::encode)
            .decoder(EvokeStatePacket::decode)
            .consumerMainThread(EvokeStatePacket::handle)
            .add()
	}

	fun startEvocation(player: ServerPlayer, server: MinecraftServer) {
		if (!CastingUtils.isEnlightened(player))
			return
		player.evocationActive = true
		player.evocationDuration = EVOKE_DURATION
		player.level().playSound(null, player.x, player.y, player.z, HexicalSounds.EVOKING_MURMUR.get(), SoundSource.PLAYERS, 1f, 1f)
		for (receiver in server.playerList.players)
			HexicalNetworking.sendToPlayer(receiver, EvokeStatePacket(player.getUUID(), true))
	}

	fun endEvocation(player: ServerPlayer, server: MinecraftServer) {
		if (!CastingUtils.isEnlightened(player))
			return
		player.evocationActive = false
		for (receiver in server.playerList.players)
			HexicalNetworking.sendToPlayer(receiver, EvokeStatePacket(player.getUUID(), false))
	}

	class EvokeStatePacket(val uuid: UUID, val start: Boolean) : AbstractPacket<EvokeStatePacket>() {
		override val handler: PacketHandler<EvokeStatePacket> = Companion

		companion object : PacketHandler<EvokeStatePacket> {
			override fun encode(packet: EvokeStatePacket, buffer: FriendlyByteBuf) {
				buffer.writeUUID(packet.uuid)
				buffer.writeBoolean(packet.start)
			}

			override fun decode(buffer: FriendlyByteBuf): EvokeStatePacket {
				return EvokeStatePacket(buffer.readUUID(), buffer.readBoolean())
			}
		
			override fun handle(packet: EvokeStatePacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					if (ctx.get().direction == NetworkDirection.PLAY_TO_CLIENT) {
						val client = Minecraft.getInstance()
						val player = client.level!!.getPlayerByUUID(packet.uuid)
						if (player != null) {
							player.evocationActive = packet.start
						}
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}
}