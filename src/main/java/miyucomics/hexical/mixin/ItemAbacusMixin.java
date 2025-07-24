package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.storage.ItemAbacus;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemAbacus.class)
public class ItemAbacusMixin {
	@Inject(method = "canWrite", at = @At("HEAD"), cancellable = true, remap = false)
	public void makeWriteable(ItemStack stack, Iota datum, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(datum instanceof DoubleIota);
	}

	@Inject(method = "writeDatum", at = @At("HEAD"), remap = false)
	public void makeWriteable(ItemStack stack, Iota datum, CallbackInfo ci) {
		if (datum instanceof DoubleIota doub)
			NBTHelper.putDouble(stack, ItemAbacus.TAG_VALUE, doub.getDouble());
	}
}