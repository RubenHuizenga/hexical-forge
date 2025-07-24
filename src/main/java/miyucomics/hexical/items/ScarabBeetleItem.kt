package miyucomics.hexical.items

import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.utils.containsTag
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.remove
import at.petrak.hexcasting.api.utils.styledWith
import miyucomics.hexical.casting.frames.ScarabFrame
import miyucomics.hexical.registry.HexicalItems
import miyucomics.hexical.registry.HexicalSounds
import com.mojang.blaze3d.vertex.*
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.sounds.SoundSource
import net.minecraft.network.chat.Component
import net.minecraft.util.*
import net.minecraft.world.level.Level
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.InteractionHand
import net.minecraft.ChatFormatting
import net.minecraft.resources.ResourceLocation
import net.minecraft.client.renderer.item.ItemProperties
import com.ibm.icu.impl.number.Properties

class ScarabBeetleItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), IotaHolderItem {
	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = user.getItemInHand(hand)
		val nbt = stack.orCreateTag
		nbt.putBoolean("active", !nbt.getBoolean("active"))
		if (world.isClientSide)
			world.playLocalSound(user.x, user.y, user.z, HexicalSounds.SCARAB_CHIRPS.get(), SoundSource.MASTER, 1f, 1f, true)
		return InteractionResultHolder.success(stack)
	}

	override fun appendHoverText(stack: ItemStack, world: Level?, tooltip: MutableList<Component>, context: TooltipFlag) {
		val nbt = stack.tag ?: return
		if (!nbt.contains("program"))
			return
		val program = IotaType.getDisplay(nbt.getCompound("program"))
		tooltip.add(Component.translatable("hexical.scarab.program", program).styledWith(ChatFormatting.GRAY))
		super.appendHoverText(stack, world, tooltip, context)
	}

	override fun readIotaTag(stack: ItemStack) = null
	override fun canWrite(stack: ItemStack, iota: Iota?) = iota == null || writeable(stack)
	override fun writeable(stack: ItemStack) = !stack.containsTag("program")
	override fun writeDatum(stack: ItemStack, iota: Iota?) {
		if (iota == null) {
			stack.remove("program")
			return
		}
		stack.putCompound("program", IotaType.serialize(iota))
	}

	companion object {
		fun registerModelPredicate() {
			ItemProperties.register(HexicalItems.SCARAB_BEETLE_ITEM.get(), ResourceLocation("active")) { stack, _, _, _ ->
				if (stack.tag?.getBoolean("active") == true)
					1.0f
				else
					0.0f
			}
		}

		fun wouldBeRecursive(pattern: String, continuation: SpellContinuation): Boolean {
			var cont = continuation
			while (cont is SpellContinuation.NotDone) {
				if (cont.frame is ScarabFrame && (cont.frame as ScarabFrame).signature == pattern)
					return true
				cont = cont.next
			}
			return false
		}
	}
}