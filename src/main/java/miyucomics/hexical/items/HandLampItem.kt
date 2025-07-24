package miyucomics.hexical.items

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import miyucomics.hexical.casting.environments.HandLampCastEnv
import miyucomics.hexical.interfaces.GenieLamp
import miyucomics.hexical.registry.HexicalSounds
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

class HandLampItem : ItemPackagedHex(Properties().stacksTo(1).rarity(Rarity.RARE)), GenieLamp {
	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = user.getItemInHand(hand)
		if (!hasHex(stack)) return InteractionResultHolder.fail(stack)
		val stackNbt = stack.orCreateTag
		world.playLocalSound(user.x, user.y, user.z, HexicalSounds.LAMP_ACTIVATE.get(), SoundSource.MASTER, 1f, 1f, true)
		if (!world.isClientSide) {
			stackNbt.putCompound("position", user.eyePosition.serializeToNBT())
			stackNbt.putCompound("rotation", user.lookAngle.serializeToNBT())
			stackNbt.putCompound("velocity", user.deltaMovement.serializeToNBT())
			stackNbt.putCompound("storage", IotaType.serialize(NullIota()))
			stackNbt.putLong("start_time", world.gameTime)
		}
		user.startUsingItem(hand)
		return InteractionResultHolder.success(stack)
	}

	override fun onUseTick(world: Level, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
		if (world.isClientSide) return
		if (getMedia(stack) == 0L) return
		val vm = CastingVM(CastingImage(), HandLampCastEnv(user as ServerPlayer, InteractionHand.MAIN_HAND, false, stack))
		vm.queueExecuteAndWrapIotas((stack.item as HandLampItem).getHex(stack, world as ServerLevel)!!, world)
	}

	override fun releaseUsing(stack: ItemStack, world: Level, user: LivingEntity, remainingUseTicks: Int) {
		if (!world.isClientSide) {
			val vm = CastingVM(CastingImage(), HandLampCastEnv(user as ServerPlayer, InteractionHand.MAIN_HAND, true, stack))
			vm.queueExecuteAndWrapIotas((stack.item as HandLampItem).getHex(stack, world as ServerLevel)!!, world)
		}
		world.playLocalSound(user.x, user.y, user.z, HexicalSounds.LAMP_DEACTIVATE.get(), SoundSource.MASTER, 1f, 1f, true)
	}

	override fun getUseDuration(stack: ItemStack) = Int.MAX_VALUE
	override fun canDrawMediaFromInventory(stack: ItemStack) = false
	override fun getUseAnimation(stack: ItemStack) = UseAnim.BOW
	override fun canRecharge(stack: ItemStack?) = false
	override fun breakAfterDepletion() = false
	override fun cooldown() = 0
}