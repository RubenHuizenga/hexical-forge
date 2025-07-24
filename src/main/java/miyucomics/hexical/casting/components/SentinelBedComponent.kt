package miyucomics.hexical.casting.components

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent.IsVecInRange
import miyucomics.hexical.registry.HexicalBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

class SentinelBedComponent(val env: CastingEnvironment) : IsVecInRange {
	override fun getKey() = SentinelBedKey()
	class SentinelBedKey : CastingEnvironmentComponent.Key<IsVecInRange>

	override fun onIsVecInRange(vec: Vec3?, current: Boolean): Boolean {
		return current || env.world.getBlockState(BlockPos.containing(vec!!)).`is`(HexicalBlocks.SENTINEL_BED_BLOCK.get())
	}
}