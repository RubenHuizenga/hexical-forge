package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import miyucomics.hexical.registry.HexicalPotions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CastingEnvironment.class, remap = false)
public abstract class CastingEnvironmentMixin {
	@Shadow public abstract @Nullable LivingEntity getCastingEntity();

	@WrapMethod(method = "isEnlightened")
	private boolean canOvercast(Operation<Boolean> original) {
		if (this.getCastingEntity() == null)
			return original.call();
		MobEffectInstance wooleye = this.getCastingEntity().getEffect(HexicalPotions.WOOLEYED_EFFECT.get());
		if (wooleye == null)
			return original.call();
		System.out.println(wooleye.getAmplifier());
		return wooleye.getAmplifier() < 1;
	}
}