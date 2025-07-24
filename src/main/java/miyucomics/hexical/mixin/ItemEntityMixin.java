package miyucomics.hexical.mixin;

import miyucomics.hexical.registry.HexicalItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Shadow public abstract ItemStack getItem();

	@Shadow private int pickupDelay;

	@Inject(method = "playerTouch", at = @At("HEAD"))
	void deactivateDroppedLamp(CallbackInfo ci) {
		if (!((Entity) (Object) this).level().isClientSide && this.getItem().getItem() == HexicalItems.ARCH_LAMP_ITEM.get() && this.getItem().getTag() != null && this.pickupDelay == 0)
			this.getItem().getTag().putBoolean("active", false);
	}
}