package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.misc.HexicalNetworking
import miyucomics.hexical.misc.HexicalNetworking.AbstractPacket
import miyucomics.hexical.misc.HexicalNetworking.SimpleChannelWrapper
import net.minecraft.world.InteractionHand
import net.minecraft.resources.ResourceLocation
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkDirection
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.client.Minecraft
import java.util.function.Supplier

object ServerCharmedUseReceiver : InitHook() {
	@JvmField
	val CHARMED_ITEM_USE_CHANNEL: ResourceLocation = HexicalMain.id("charmed_item")

	fun initNetworking(wrapper: SimpleChannelWrapper) {
		wrapper.channel.messageBuilder<CharmedItemUsePacket>(CharmedItemUsePacket::class.java, wrapper.nextId())
            .encoder(CharmedItemUsePacket::encode)
            .decoder(CharmedItemUsePacket::decode)
            .consumerMainThread(CharmedItemUsePacket::handle)
            .add()
	}

	override fun init() {
		// Done in handler of the CharmedItemUsePacket
		// ServerPlayNetworking.registerGlobalReceiver(CHARMED_ITEM_USE_CHANNEL) { server, player, _, buf, _ ->
		// 	val inputMethod = buf.readInt()
		// 	val hand = enumValues<InteractionHand>()[buf.readInt()]
		// 	val stack = player.getItemInHand(hand)
		// 	server.execute {
		// 		val vm = CastingVM(CastingImage().copy(stack = inputMethod.asActionResult), CharmCastEnv(player, hand, stack))
		// 		vm.queueExecuteAndWrapIotas(CharmUtilities.getHex(stack, player.serverWorld), player.serverWorld)
		// 		if (stack.item is CurioItem)
		// 			(stack.item as CurioItem).postCharmCast(player, stack, hand, player.serverWorld, vm.image.stack)
		// 	}
		// }
	}

	class CharmedItemUsePacket(
		val inputMethod: Int,
		val hand: InteractionHand
	) : AbstractPacket<CharmedItemUsePacket>() {
		override val handler: PacketHandler<CharmedItemUsePacket> = Companion

		companion object : PacketHandler<CharmedItemUsePacket>{
			override fun encode(packet: CharmedItemUsePacket, buffer: FriendlyByteBuf) {
				buffer.writeInt(packet.inputMethod)
				buffer.writeEnum(packet.hand)
			}

			override fun decode(buffer: FriendlyByteBuf): CharmedItemUsePacket {
				return CharmedItemUsePacket(
					buffer.readInt(),
					buffer.readEnum(InteractionHand::class.java)
				)
			}

			override fun handle(packet: CharmedItemUsePacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					val player = ctx.get().sender!!

					val stack = player.getItemInHand(packet.hand)
					player.server.execute {
						val vm = CastingVM(CastingImage().copy(stack = packet.inputMethod.asActionResult), CharmCastEnv(player, packet.hand, stack))
						vm.queueExecuteAndWrapIotas(CharmUtilities.getHex(stack, player.serverLevel()), player.serverLevel())
						if (stack.item is CurioItem)
							(stack.item as CurioItem).postCharmCast(player, stack, packet.hand, player.serverLevel(), vm.image.stack)
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}
}