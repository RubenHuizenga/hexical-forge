package miyucomics.hexical.features.dyes

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.features.dyes.getTrueDye
import net.minecraft.world.phys.Vec3

object OpTranslateDye : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val dye = args.getTrueDye(0, argc).textureDiffuseColors
		return Vec3(dye[0].toDouble(), dye[1].toDouble(), dye[2].toDouble()).asActionResult
	}
}