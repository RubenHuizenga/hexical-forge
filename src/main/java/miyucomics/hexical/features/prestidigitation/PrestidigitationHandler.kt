package miyucomics.hexical.features.prestidigitation

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import net.minecraft.world.entity.Entity
import net.minecraft.core.BlockPos

interface PrestidigitationHandler {
	fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean = false
	fun tryHandleEntity(env: CastingEnvironment, entity: Entity): Boolean = false
}