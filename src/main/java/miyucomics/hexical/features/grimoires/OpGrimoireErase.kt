package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import miyucomics.hexical.features.grimoires.OpGrimoireIndex.populateGrimoireMetadata
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.world.item.ItemStack

object OpGrimoireErase : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val itemInfo = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.GRIMOIRE_ITEM.get()) }
		if (itemInfo == null)
			throw MishapBadOffhandItem.of(null, "grimoire")
		val stack = itemInfo.stack
		OpGrimoireIndex.populateGrimoireMetadata(stack)
		val pattern = args.getPattern(0, argc)
		return SpellAction.Result(Spell(stack, pattern), 0, listOf())
	}

	private data class Spell(val stack: ItemStack, val key: HexPattern) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (!stack.orCreateTag.contains("expansions"))
				return
			stack.orCreateTag.getCompound("expansions").remove(key.anglesSignature())
			stack.orCreateTag.getCompound("metadata").remove(key.anglesSignature())
		}
	}
}