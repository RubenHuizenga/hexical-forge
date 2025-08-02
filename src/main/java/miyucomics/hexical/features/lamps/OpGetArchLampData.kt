package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import miyucomics.hexical.features.lamps.NeedsArchLampMishap
import miyucomics.hexical.features.player.PlayerEntityMinterface
import miyucomics.hexical.features.lamps.hasActiveArchLamp
import net.minecraft.server.level.ServerPlayer

class OpGetArchLampData(private val process: (CastingEnvironment, ArchLampField) -> List<Iota>) : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster !is ServerPlayer)
			return listOf(NullIota())
		if (!hasActiveArchLamp(caster))
			throw NeedsArchLampMishap()
		return process(env, caster.getArchLampField())
	}
}