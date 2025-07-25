package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import miyucomics.hexical.casting.environments.HandLampCastEnv
import miyucomics.hexical.casting.mishaps.NoHandLampMishap
import net.minecraft.nbt.CompoundTag

class OpGetHandLampData(private val process: (CastingEnvironment, CompoundTag) -> List<Iota>) : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env !is HandLampCastEnv)
			throw NeedsHandLampMishap()
		val nbt = env.castingEntity!!.useItem.tag ?: return listOf(NullIota())
		return process(env, nbt)
	}
}