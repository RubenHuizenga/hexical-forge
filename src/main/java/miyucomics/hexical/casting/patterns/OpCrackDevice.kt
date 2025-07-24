package miyucomics.hexical.casting.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import miyucomics.hexical.casting.mishaps.IllegalJailbreakMishap
import net.minecraft.world.item.ItemStack

class OpCrackDevice : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val heldData = env.getHeldItemToOperateOn { it.item is ItemPackagedHex }
		if (heldData == null)
			throw MishapBadOffhandItem.of(null, "casting_device")
		val stack = heldData.stack
		if ((heldData.stack.item as ItemPackagedHex).hasHex(stack))
			throw IllegalJailbreakMishap()
		return SpellAction.Result(Spell(heldData.stack), MediaConstants.CRYSTAL_UNIT, listOf())
	}

	private data class Spell(val stack: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			stack.orCreateTag.putBoolean("cracked", true)
		}
	}
}