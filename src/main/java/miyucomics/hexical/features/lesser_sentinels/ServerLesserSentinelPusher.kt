package miyucomics.hexical.features.lesser_sentinels

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.HexicalNetworking
import miyucomics.hexical.misc.HexicalNetworking.AbstractPacket
import miyucomics.hexical.misc.HexicalNetworking.SimpleChannelWrapper
import miyucomics.hexical.misc.ClientStorage
import miyucomics.hexical.misc.InitHook
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.common.MinecraftForge
import net.minecraft.server.level.ServerPlayer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkDirection
import java.util.function.Supplier

object ServerLesserSentinelPusher : InitHook() {
	override fun init() {
		MinecraftForge.EVENT_BUS.register(::initPlayerJoin)
		MinecraftForge.EVENT_BUS.register(::initPlayerChangeWorld)
	}

	fun initNetworking(wrapper: SimpleChannelWrapper) {
		wrapper.channel.messageBuilder<LesserSentinelPacket>(LesserSentinelPacket::class.java, wrapper.nextId())
            .encoder(LesserSentinelPacket::encode)
            .decoder(LesserSentinelPacket::decode)
            .consumerMainThread(LesserSentinelPacket::handle)
            .add()
	}

	fun initPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
		if (event.entity is ServerPlayer) {
			(event.entity as ServerPlayer).syncLesserSentinels()
		}
	}

	fun initPlayerChangeWorld(event: PlayerEvent.PlayerChangedDimensionEvent) {
		if (event.entity is ServerPlayer) {
			(event.entity as ServerPlayer).syncLesserSentinels()
		}
	}

	class LesserSentinelPacket(val size: Int, val positions: MutableList<Vec3>) : AbstractPacket<LesserSentinelPacket>() {
     	override val handler: PacketHandler<LesserSentinelPacket> = Companion

		companion object : PacketHandler<LesserSentinelPacket> {
			override fun encode(packet: LesserSentinelPacket, buffer: FriendlyByteBuf) {
				buffer.writeInt(packet.size)
				packet.positions.forEach { pos ->
					buffer.writeDouble(pos.x)
					buffer.writeDouble(pos.y)
					buffer.writeDouble(pos.z)
				}
			}

			override fun decode(buffer: FriendlyByteBuf): LesserSentinelPacket {
				val count = buffer.readInt()
				val list = mutableListOf<Vec3>()

				repeat(count) {
					val x = buffer.readDouble()
					val y = buffer.readDouble()
					val z = buffer.readDouble()
					list.add(Vec3(x, y, z))
				}

				return LesserSentinelPacket(count, list)
			}
		
			override fun handle(packet: LesserSentinelPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					if (ctx.get().direction == NetworkDirection.PLAY_TO_CLIENT) {
						ClientStorage.lesserSentinels.clear()
						ClientStorage.lesserSentinels.addAll(packet.positions)
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}
}