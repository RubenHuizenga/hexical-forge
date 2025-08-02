package miyucomics.hexical.features.specklikes

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import miyucomics.hexical.features.pigments.getPigment
import miyucomics.hexical.features.specklikes.Specklike
import net.minecraft.commands.arguments.EntityAnchorArgument

class OpSpecklikeProperty(private val mode: Int) : ConstMediaAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val specklike = args.getEntity(0, argc)
		if (specklike !is Specklike)
			throw MishapBadEntity.of(specklike, "specklike")
		when (mode) {
			0 -> {
				val position = args.getVec3(1, argc)
				env.assertVecInRange(position)
				specklike.setPos(position.subtract(0.0, specklike.eyeHeight.toDouble(), 0.0))
			}
			1 -> specklike.lookAt(EntityAnchorArgument.Anchor.FEET, specklike.position().add(args.getVec3(1, argc)))
			2 -> specklike.setRoll(args.getDoubleBetween(1, 0.0, 1.0, argc).toFloat() * 360)
			3 -> specklike.setSize(args.getPositiveDoubleUnderInclusive(1, 10.0, argc).toFloat())
			4 -> specklike.setThickness(args.getPositiveDoubleUnderInclusive(1, 10.0, argc).toFloat())
			5 -> specklike.setLifespan(args.getInt(1, argc))
			6 -> specklike.setPigment(args.getPigment(1, argc))
		}
		return listOf()
	}
}