package miyucomics.hexical.mixin;

import miyucomics.hexical.features.curios.curios.FluteCurioItemModel;
import miyucomics.hexical.features.curios.curios.HandbellCurioItemModel;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelLoaderMixin {
	@Shadow protected abstract void loadTopLevel(ModelResourceLocation modelIdentifier);

	@Inject(method = "<init>", at = @At("TAIL"))
	private void injectModel(BlockColors blockColors, ProfilerFiller profiler, Map map, Map map2, CallbackInfo ci) {
		loadTopLevel(FluteCurioItemModel.heldFluteModel);
		loadTopLevel(HandbellCurioItemModel.heldHandbellModel);
	}
}