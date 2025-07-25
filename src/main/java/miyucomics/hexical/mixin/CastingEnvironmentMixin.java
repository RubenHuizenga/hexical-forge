package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import miyucomics.hexical.features.periwinkle.WooleyedEffect;
import miyucomics.hexical.inits.HexicalItems;
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
		if (this.getCastingEntity() instanceof Player player && player.getInventory().armor.get(3).isOf(HexicalItems.LEI))
			return true;
		MobEffectInstance wooleye = this.getCastingEntity().getStatusEffect(WooleyedEffect.INSTANCE);
		if (wooleye == null)
			return original.call();
		return wooleye.getAmplifier() < 1;
	}
}