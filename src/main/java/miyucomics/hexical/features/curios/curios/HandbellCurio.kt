package miyucomics.hexical.features.curios.curios

import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.misc.HexicalNetworking
import miyucomics.hexical.misc.HexicalNetworking.AbstractPacket
import miyucomics.hexical.misc.HexicalNetworking.SimpleChannelWrapper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.level.Level
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag
import net.minecraft.world.phys.Vec3
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkDirection
import java.util.function.Supplier
import java.util.UUID
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess
import dev.kosmx.playerAnim.api.layered.ModifierLayer

object HandbellCurio : CurioItem() {
	fun initNetworking(wrapper: SimpleChannelWrapper) {
		wrapper.channel.messageBuilder<HandbellCurioPacket>(HandbellCurioPacket::class.java, wrapper.nextId())
            .encoder(HandbellCurioPacket::encode)
            .decoder(HandbellCurioPacket::decode)
            .consumerMainThread(HandbellCurioPacket::handle)
            .add()
	}

	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (world.isClientSide)
			return InteractionResultHolder.pass(user.getItemInHand(hand))
		playSound(world as ServerLevel, user as ServerPlayer)
		return InteractionResultHolder.pass(user.getItemInHand(hand))
	}

	override fun postCharmCast(user: ServerPlayer, item: ItemStack, hand: InteractionHand, world: ServerLevel, stack: List<Iota>) {
		playSound(world, user)
	}

	private fun playSound(world: ServerLevel, user: ServerPlayer) {
		HexicalNetworking.sendToPlayer(user, HandbellCurioPacket(user.uuid))
		world.playSound(null, user.x, user.y, user.z, HexicalSounds.HANDBELL_CHIMES.get(), SoundSource.MASTER, 1f, 0.8f + HexicalMain.RANDOM.nextFloat() * 0.3f)
	}

	class HandbellCurioPacket(val uuid: UUID) : AbstractPacket<HandbellCurioPacket>() {
     	override val handler: PacketHandler<HandbellCurioPacket> = Companion

		companion object : PacketHandler<HandbellCurioPacket> {
			override fun encode(packet: HandbellCurioPacket, buffer: FriendlyByteBuf) {
				buffer.writeUUID(packet.uuid)
			}

			override fun decode(buffer: FriendlyByteBuf): HandbellCurioPacket {
				return HandbellCurioPacket(buffer.readUUID())
			}
		
			override fun handle(packet: HandbellCurioPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					if (ctx.get().direction == NetworkDirection.PLAY_TO_CLIENT) {
						val client = Minecraft.getInstance()

						val player = client.level!!.getPlayerByUUID(packet.uuid)
						if (player != null) {
							val handbellAnimation = (PlayerAnimationAccess.getPlayerAssociatedData(player as AbstractClientPlayer).get(HandbellCurioItemModel.clientReceiver) as ModifierLayer<HandbellCurioPlayerModel>).animation
							handbellAnimation!!.shakingBellTimer = 10
						}
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}
}