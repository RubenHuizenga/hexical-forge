package miyucomics.hexical.features.shaders

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.HexicalNetworking
import miyucomics.hexical.misc.HexicalNetworking.AbstractPacket
import miyucomics.hexical.misc.HexicalNetworking.SimpleChannelWrapper
import miyucomics.hexical.misc.InitHook
import net.minecraft.server.level.ServerPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.simple.SimpleChannel
import java.util.function.Supplier
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.common.MinecraftForge

object ServerShaderManager : InitHook() {
    val SHADER_CHANNEL: ResourceLocation = HexicalMain.id("shader")

    fun setShader(shader: ResourceLocation?) {
        HexicalNetworking.sendToServer(ShaderPacket(shader))
    }

    override fun init() {
       MinecraftForge.EVENT_BUS.register(::initPlayerRespawn)
    }

    fun initPlayerRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        if(!event.isEndConquered)
            setShader(null)
    }

    fun initNetworking(wrapper: SimpleChannelWrapper) {
        wrapper.channel.messageBuilder<ShaderPacket>(ShaderPacket::class.java, wrapper.nextId())
            .encoder(ShaderPacket::encode)
            .decoder(ShaderPacket::decode)
            .consumerMainThread(ShaderPacket::handle)
            .add()
    }

    class ShaderPacket(val shader: ResourceLocation?) : AbstractPacket<ShaderPacket>() {
        override val handler: PacketHandler<ShaderPacket> = Companion

		companion object : PacketHandler<ShaderPacket> {
			override fun encode(packet: ShaderPacket, buffer: FriendlyByteBuf) {
				buffer.writeUtf(packet.shader?.toString() ?: "null")
			}

			override fun decode(buffer: FriendlyByteBuf): ShaderPacket {
				val str = buffer.readUtf()
				return ShaderPacket(if (str == "null") null else ResourceLocation(str))
			}
		
			override fun handle(packet: ShaderPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					if (ctx.get().direction == NetworkDirection.PLAY_TO_CLIENT) {
						ShaderRenderer.setEffect(packet.shader)
					}
				}
				ctx.get().packetHandled = true
			}
        }
	}
}