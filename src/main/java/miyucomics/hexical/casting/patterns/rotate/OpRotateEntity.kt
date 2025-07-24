package miyucomics.hexical.casting.patterns.rotate

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getDouble
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.LivingEntity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth

class OpRotateEntity : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val target = args.getEntity(0, argc) as LivingEntity
		val yaw = args.getDouble(1, argc) * Mth.DEG_TO_RAD
		val pitch = args.getDouble(2, argc) * Mth.DEG_TO_RAD
		var cost = MediaConstants.DUST_UNIT / 2
		if (target is Player)
			cost = MediaConstants.CRYSTAL_UNIT
		if (target == env.castingEntity)
			cost = 0
		return SpellAction.Result(Spell(target, yaw.toFloat(), pitch.toFloat()), cost, listOf(ParticleSpray.burst(target.eyePosition, 1.0)))
	}

	private data class Spell(val target: LivingEntity, val yaw: Float, val pitch: Float) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			target.xRotO = target.xRot
			target.yRotO = target.yRot
			target.yBodyRot += yaw
			target.yRot += yaw
			target.yHeadRot += yaw
			target.xRot += pitch
			if (target is ServerPlayer)
				target.connection.teleport(target.x, target.y, target.z, target.yRot, target.xRot)
		}
	}
}