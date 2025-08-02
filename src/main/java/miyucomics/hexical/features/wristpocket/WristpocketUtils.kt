package miyucomics.hexical.features.wristpocket

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import miyucomics.hexical.features.player.PlayerEntityMinterface
import net.minecraft.world.item.ItemStack
import net.minecraft.world.entity.player.Player

object WristpocketUtils {
	fun getWristpocketStack(env: CastingEnvironment): ItemStack? {
		return when (env) {
			is PlayerBasedCastEnv -> (env.castingEntity as Player).wristpocket
			else -> null
		}
	}

	fun setWristpocketStack(env: CastingEnvironment, stack: ItemStack) {
		when (env) {
			is PlayerBasedCastEnv -> (env.castingEntity as Player).wristpocket = stack
		}
	}
}