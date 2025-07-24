package miyucomics.hexical.items

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import miyucomics.hexical.casting.environments.ArchLampCastEnv
import miyucomics.hexical.interfaces.GenieLamp
import miyucomics.hexical.interfaces.PlayerEntityMinterface
import miyucomics.hexical.registry.HexicalItems
import miyucomics.hexical.registry.HexicalSounds
import com.mojang.blaze3d.vertex.*
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level
import net.minecraft.client.renderer.item.ItemProperties

class ArchLampItem : ItemPackagedHex(Properties().stacksTo(1).rarity(Rarity.EPIC)), GenieLamp {
	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = user.getItemInHand(hand)
		if (!hasHex(stack))
			return InteractionResultHolder.fail(stack)

		val stackNbt = stack.orCreateTag
		if (!stackNbt.contains("active"))
			stackNbt.putBoolean("active", false)

		if (world.isClientSide) {
			world.playLocalSound(user.x, user.y, user.z, if (stackNbt.getBoolean("active")) HexicalSounds.LAMP_DEACTIVATE.get() else HexicalSounds.LAMP_ACTIVATE.get(), SoundSource.MASTER, 1f, 1f, true)
			return InteractionResultHolder.success(stack)
		}

		if (stackNbt.getBoolean("active")) {
			val vm = CastingVM(CastingImage(), ArchLampCastEnv(user as ServerPlayer, hand, true, stack))
			vm.queueExecuteAndWrapIotas((stack.item as ArchLampItem).getHex(stack, world as ServerLevel)!!, world)
			stackNbt.putBoolean("active", false)
			return InteractionResultHolder.success(stack)
		}

		stackNbt.putBoolean("active", true)

		val state = (user as PlayerEntityMinterface).getArchLampState()
		state.position = user.eyePosition
		state.rotation = user.lookAngle
		state.velocity = user.deltaMovement
		state.storage = IotaType.serialize(NullIota())
		state.time = world.gameTime

		return InteractionResultHolder.success(stack)
	}

	override fun inventoryTick(stack: ItemStack, world: Level, user: Entity, slot: Int, selected: Boolean) {
		if (world.isClientSide) return
		if (getMedia(stack) == 0L) return
		if (user !is ServerPlayer) return
		if (!stack.orCreateTag.getBoolean("active")) return
		if (user.gameMode.gameModeForPlayer == GameType.SPECTATOR) return

		if ((user as PlayerEntityMinterface).getArchLampCastedThisTick()) {
			for (itemSlot in user.inventory.items)
				if (itemSlot.item == HexicalItems.ARCH_LAMP_ITEM)
					itemSlot.orCreateTag.putBoolean("active", false)
			user.cooldowns.addCooldown(this, 100)
			return
		}

		val vm = CastingVM(CastingImage(), ArchLampCastEnv(user as ServerPlayer, InteractionHand.MAIN_HAND, false, stack))
		vm.queueExecuteAndWrapIotas((stack.item as ArchLampItem).getHex(stack, world as ServerLevel)!!, world)
		(user as PlayerEntityMinterface).archLampCasted()
	}

	override fun canDrawMediaFromInventory(stack: ItemStack) = false
	override fun canRecharge(stack: ItemStack) = false
	override fun breakAfterDepletion() = false
	override fun cooldown() = 0

	companion object {
		fun registerModelPredicate() {
			ItemProperties.register(HexicalItems.ARCH_LAMP_ITEM.get(), ResourceLocation("active")) { stack, _, _, _ ->
				if (stack.tag?.getBoolean("active") == true)
					1.0f
				else
					0.0f
			}
		}
	}
}

fun hasActiveArchLamp(player: ServerPlayer): Boolean {
	for (stack in player.inventory.items)
		if (stack.item == HexicalItems.ARCH_LAMP_ITEM && stack.orCreateTag.getBoolean("active"))
			return true
	for (stack in player.inventory.offhand)
		if (stack.item == HexicalItems.ARCH_LAMP_ITEM && stack.orCreateTag.getBoolean("active"))
			return true
	return false
}