package miyucomics.hexical.casting.patterns.telepathy

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import miyucomics.hexical.data.KeybindData
import net.minecraft.server.level.ServerPlayer

class OpGetKeybind(private val key: String) : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster !is ServerPlayer)
			return listOf(NullIota())
		if (!KeybindData.active.containsKey(caster.uuid))
			return (-1).asActionResult
		if (!KeybindData.active[caster.uuid]!!.getOrDefault(key, false))
			return (-1).asActionResult
		return KeybindData.duration[caster.uuid]!!.getOrDefault(key, 0.0).asActionResult
	}
}