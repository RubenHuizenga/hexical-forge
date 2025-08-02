package miyucomics.hexical.features.peripherals

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.evocation.ServerEvocationManager
import miyucomics.hexical.misc.HexicalNetworking
import miyucomics.hexical.misc.HexicalNetworking.AbstractPacket
import miyucomics.hexical.misc.HexicalNetworking.SimpleChannelWrapper
import miyucomics.hexical.misc.InitHook
import net.minecraft.resources.ResourceLocation
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkDirection
import java.util.function.Supplier


object ServerPeripheralReceiver : InitHook() {
	override fun init() {

	}

	fun initNetworking(wrapper: SimpleChannelWrapper) {
		wrapper.channel.messageBuilder<KeyPressPacket>(KeyPressPacket::class.java, wrapper.nextId())
            .encoder(KeyPressPacket::encode)
            .decoder(KeyPressPacket::decode)
            .consumerMainThread(KeyPressPacket::handle)
            .add()
		
        wrapper.channel.messageBuilder<ScrollPacket>(ScrollPacket::class.java, wrapper.nextId())
            .encoder(ScrollPacket::encode)
            .decoder(ScrollPacket::decode)
            .consumerMainThread(ScrollPacket::handle)
            .add()
	}


	class KeyPressPacket(val key: String, val pressed: Boolean) : AbstractPacket<KeyPressPacket>() {
		override val handler: PacketHandler<KeyPressPacket> = Companion

		companion object : PacketHandler<KeyPressPacket> {
			override fun encode(packet: KeyPressPacket, buffer: FriendlyByteBuf) {
				buffer.writeUtf(packet.key)
				buffer.writeBoolean(packet.pressed)
			}

			override fun decode(buffer: FriendlyByteBuf): KeyPressPacket {
				return KeyPressPacket(buffer.readUtf(), buffer.readBoolean())
			}
		
			override fun handle(packet: KeyPressPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					val player = ctx.get().sender ?: return@enqueueWork

					if (packet.pressed) {
						player.serverKeybindActive()[packet.key] = true
						player.serverKeybindDuration()[packet.key] = 0
						if (packet.key == "key.hexical.telepathy")
							player.serverScroll = 0
						if (packet.key == "key.hexical.evoke")
							ServerEvocationManager.startEvocation(player, player.server)
					} else {
						player.serverKeybindActive()[packet.key] = false
						player.serverKeybindDuration()[packet.key] = 0
						if (packet.key == "key.hexical.evoke")
							ServerEvocationManager.endEvocation(player, player.server)
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}

	class ScrollPacket(val delta: Int) : AbstractPacket<ScrollPacket>() {
		override val handler: PacketHandler<ScrollPacket> = Companion

		companion object : PacketHandler<ScrollPacket> {
			override fun encode(packet: ScrollPacket, buffer: FriendlyByteBuf) {
				buffer.writeInt(packet.delta)
			}

			override fun decode(buffer: FriendlyByteBuf): ScrollPacket {
				return ScrollPacket(buffer.readInt())
			}
			
			override fun handle(packet: ScrollPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					val player = ctx.get().sender ?: return@enqueueWork
					player.serverScroll = player.serverScroll + packet.delta
				}
				ctx.get().packetHandled = true
			}
		}
	}
}