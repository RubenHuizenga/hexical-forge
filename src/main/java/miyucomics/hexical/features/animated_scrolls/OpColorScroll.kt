package miyucomics.hexical.features.animated_scrolls

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import miyucomics.hexical.features.animated_scrolls.AnimatedScrollEntity
import net.minecraft.util.FastColor
import net.minecraft.world.phys.Vec3
import kotlin.math.max
import kotlin.math.min

object OpColorScroll : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val scroll = args.getEntity(0, argc)
		env.assertEntityInRange(scroll)
		if (scroll !is AnimatedScrollEntity)
			throw MishapBadEntity.of(scroll, "animated_scroll")
		val color = args.getVec3(1, argc)
		return SpellAction.Result(Spell(scroll, color), 0, listOf())
	}

	private data class Spell(val scroll: AnimatedScrollEntity, val color: Vec3) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			scroll.setColor(
				FastColor.ARGB32.color(
					255,
					(max(min(color.x, 1.0), 0.0) * 255).toInt(),
					(max(min(color.y, 1.0), 0.0) * 255).toInt(),
					(max(min(color.z, 1.0), 0.0) * 255).toInt()
				)
			)
		}
	}
}