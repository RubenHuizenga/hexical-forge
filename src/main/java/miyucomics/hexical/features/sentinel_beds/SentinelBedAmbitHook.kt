package miyucomics.hexical.features.sentinel_beds

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import net.minecraft.nbt.CompoundTag
import miyucomics.hexical.misc.InitHook

object SentinelBedAmbitHook : InitHook() {
	override fun init() {
		CastingEnvironment.addCreateEventListener { env: CastingEnvironment, _: CompoundTag ->
			env.addExtension(SentinelBedComponent(env))
		}
	}
}