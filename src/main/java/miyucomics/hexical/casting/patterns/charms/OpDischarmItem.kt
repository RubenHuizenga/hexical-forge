package miyucomics.hexical.casting.patterns.charms

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.utils.CharmedItemUtilities
import net.minecraft.world.item.ItemStack

class OpDischarmItem : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val item = args.getItemEntity(0, argc)
		env.assertEntityInRange(item)
		if (!CharmedItemUtilities.isStackCharmed(item.item))
			throw MishapBadEntity.of(item, "charmed_item")
		return SpellAction.Result(Spell(item.item), MediaConstants.DUST_UNIT, listOf(ParticleSpray.burst(item.position(), 1.0)))
	}

	private data class Spell(val stack: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			CharmedItemUtilities.removeCharm(stack)
		}
	}
}