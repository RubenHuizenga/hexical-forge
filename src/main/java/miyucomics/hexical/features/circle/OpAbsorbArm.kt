package miyucomics.hexical.features.circle

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapNoSpellCircle
import miyucomics.hexical.features.pedestal.PedestalBlockEntity
import miyucomics.hexical.features.circle.OutsideCircleMishap
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

object OpAbsorbArm : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env !is CircleCastEnv)
			throw MishapNoSpellCircle()

		val circle = env.impetus ?: throw MishapNoSpellCircle()
		val bounds = circle.executionState!!.bounds

		val pedestal = args.getBlockPos(0, argc)
		if (!bounds.contains(Vec3.atCenterOf(pedestal)))
			throw OutsideCircleMishap()
		if (env.world.getBlockEntity(pedestal) !is PedestalBlockEntity)
			throw MishapBadBlock.of(pedestal, "pedestal")

		return SpellAction.Result(Spell(pedestal), 0, listOf(ParticleSpray.burst(Vec3.atCenterOf(pedestal), 1.0)))
	}

	private data class Spell(val pedestal: BlockPos) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {}
		override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
			val newImage = (env as CircleCastEnv).circleState().currentImage.copy()
			newImage.userData.putIntArray("impetus_hand", listOf(pedestal.x, pedestal.y, pedestal.z))
			return newImage
		}
	}
}