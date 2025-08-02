package miyucomics.hexical.misc

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.shaders.ServerShaderManager
import miyucomics.hexical.features.peripherals.ServerPeripheralReceiver
import miyucomics.hexical.features.evocation.ServerEvocationManager
import miyucomics.hexical.features.lesser_sentinels.ServerLesserSentinelPusher
import miyucomics.hexical.features.confetti.ConfettiHelper
import miyucomics.hexical.features.charms.ServerCharmedUseReceiver
import miyucomics.hexical.features.media_log.MediaLogField
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkDirection
import java.util.function.Supplier

object HexicalNetworking : InitHook() {
	private const val PROTOCOL_VERSION = "1"
    private lateinit var channelWrapper: SimpleChannelWrapper

	override fun init() {
		channelWrapper = SimpleChannelWrapper(NetworkRegistry.newSimpleChannel(
			HexicalMain.id("main"),
			{ PROTOCOL_VERSION },
			{ it == PROTOCOL_VERSION },
			{ it == PROTOCOL_VERSION }
		))

		ServerShaderManager.initNetworking(channelWrapper)
        ServerPeripheralReceiver.initNetworking(channelWrapper)
        ServerEvocationManager.initNetworking(channelWrapper)
        ConfettiHelper.initNetworking(channelWrapper)
        ServerLesserSentinelPusher.initNetworking(channelWrapper)
        ServerCharmedUseReceiver.initNetworking(channelWrapper)
        MediaLogField.initNetworking(channelWrapper)
	}

    abstract class AbstractPacket<Self: AbstractPacket<Self>> {
        abstract val handler: PacketHandler<Self>

        interface PacketHandler<Self> {
            fun encode(packet: Self, buffer: FriendlyByteBuf)

            fun decode(buffer: FriendlyByteBuf): Self
            
            fun handle(packet: Self, ctx: Supplier<NetworkEvent.Context>)
        }
    }

    class SimpleChannelWrapper(val channel: SimpleChannel) {
        private var id: Int = 0

        fun nextId(): Int {
            return id++;
        }
    }

	@JvmStatic
	fun sendToServer(packet: AbstractPacket<*>) {
        channelWrapper.channel.sendToServer(packet)
    }

    fun sendToPlayer(player: ServerPlayer, packet: AbstractPacket<*>) {
        channelWrapper.channel.send(PacketDistributor.PLAYER.with { player }, packet)
    }

    fun sendToAllPlayers(packet: AbstractPacket<*>) {
        channelWrapper.channel.send(PacketDistributor.ALL.noArg(), packet)
    }
}