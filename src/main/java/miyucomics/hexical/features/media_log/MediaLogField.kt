package miyucomics.hexical.features.media_log

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import miyucomics.hexpose.utils.RingBuffer
import miyucomics.hexical.misc.HexicalNetworking
import miyucomics.hexical.misc.HexicalNetworking.AbstractPacket
import miyucomics.hexical.misc.HexicalNetworking.SimpleChannelWrapper
import miyucomics.hexical.misc.ClientStorage
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkDirection
import net.minecraft.server.MinecraftServer
import net.minecraft.sounds.SoundSource
import net.minecraft.client.Minecraft
import java.util.function.Supplier
import net.minecraft.world.entity.player.Player
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import javax.print.attribute.standard.Media

class MediaLogField : PlayerField {
	var patterns: RingBuffer<HexPattern> = RingBuffer(32)
	var stack: RingBuffer<Component> = RingBuffer(8)
	var mishap: Component = Component.empty()
	var active = true

	fun saveMishap(text: Component) {
		mishap = text
	}

	fun pushPattern(pattern: HexPattern) {
		patterns.add(pattern)
	}

	fun saveStack(iotas: List<Iota>) {
		stack.clear()
		iotas.forEach { iota -> stack.add(iota.display()) }
	}

	override fun readNbt(compound: CompoundTag) {
		if (!compound.contains("media_log"))
			return
		fromNbt(compound.getCompound("media_log"))
	}

	override fun writeNbt(compound: CompoundTag) {
		compound.putCompound("media_log", toNbt())
	}

	fun fromNbt(mediaLog: CompoundTag) {
		mediaLog.getList("patterns", CompoundTag.TAG_COMPOUND.toInt()).forEach { pattern -> patterns.add(HexPattern.fromNBT(pattern as CompoundTag)) }
		mediaLog.getList("stack", CompoundTag.TAG_STRING.toInt()).forEach { iota -> stack.add(Component.Serializer.fromJson((iota as StringTag).getAsString())!!) }
		this.mishap = Component.Serializer.fromJson(mediaLog.getString("mishap")) ?: Component.empty()
	}

	fun toNbt(): CompoundTag {
		return CompoundTag().also { compound ->
			compound.putList("patterns", ListTag().also { patterns.buffer().forEach { pattern -> it.add(pattern.serializeToNBT()) } })
			compound.putList("stack", ListTag().also { stack.buffer().forEach { iota -> it.add(StringTag.valueOf(Component.Serializer.toJson(iota))) } })
			compound.putString("mishap", Component.Serializer.toJson(mishap))
		}
	}
	
	fun toPacket(): CompoundTag {
		return this.toNbt()
	}

	companion object {
		fun initNetworking(wrapper: SimpleChannelWrapper) {
			wrapper.channel.messageBuilder<MediaLogPacket>(MediaLogPacket::class.java, wrapper.nextId())
				.encoder(MediaLogPacket::encode)
				.decoder(MediaLogPacket::decode)
				.consumerMainThread(MediaLogPacket::handle)
				.add()
		}

		@JvmStatic
		fun isEnvCompatible(env: CastingEnvironment) = env is StaffCastEnv || env is PackagedItemCastEnv
	}

	class MediaLogPacket(
		val nbt: CompoundTag,
	) : AbstractPacket<MediaLogPacket>() {
		override val handler: PacketHandler<MediaLogPacket> = Companion

		companion object : PacketHandler<MediaLogPacket>{
			override fun encode(packet: MediaLogPacket, buffer: FriendlyByteBuf) {
				buffer.writeNbt(packet.nbt)
			}

			override fun decode(buffer: FriendlyByteBuf): MediaLogPacket {
				return MediaLogPacket(buffer.readNbt()!!)
			}

			override fun handle(packet: MediaLogPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					ClientStorage.mediaLog = MediaLogField().also { it.fromNbt(packet.nbt) }
				}
				ctx.get().packetHandled = true
			}
		}
	}
}

fun Player.getMediaLog() = this.getHexicalPlayerManager().get(MediaLogField::class)
fun ServerPlayer.syncMediaLog() { HexicalNetworking.sendToPlayer(this, MediaLogField.MediaLogPacket(this.getMediaLog().toPacket())) }