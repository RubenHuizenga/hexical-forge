package miyucomics.hexical.mixin;

import miyucomics.hexical.inits.HexicalBlocks;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CatSitOnBlockGoal.class)
class CatSitOnBlockGoalMixin {
	@Inject(method = "isValidTarget(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z", at = @At("RETURN"), cancellable = true)
	void sits(LevelReader worldView, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue())
			return;
		if (!worldView.isEmptyBlock(blockPos.above())) {
			cir.setReturnValue(false);
			return;
		}
		if (worldView.getBlockState(blockPos).is(HexicalBlocks.SENTINEL_BED_BLOCK.get()))
			cir.setReturnValue(true);
	}
}