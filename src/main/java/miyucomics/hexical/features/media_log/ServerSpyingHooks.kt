package miyucomics.hexical.features.media_log

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import net.minecraft.nbt.CompoundTag
import miyucomics.hexical.misc.InitHook

object ServerSpyingHooks : InitHook() {
	override fun init() {
		CastingEnvironment.addCreateEventListener { env: CastingEnvironment, _: CompoundTag ->
			if (MediaLogField.isEnvCompatible(env))
				env.addExtension(MediaLogComponent(env as PlayerBasedCastEnv))
		}
	}
}