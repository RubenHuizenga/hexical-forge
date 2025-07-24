package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.common.casting.actions.rw.OpRead;
import miyucomics.hexical.registry.HexicalItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = OpRead.class, remap = false)
public class OpReadMixin {
	@Inject(method = "execute", at = @At("HEAD"), cancellable = true)
	private void readCompass(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<List<Iota>> cir) {
		CastingEnvironment.HeldItemInfo data = env.getHeldItemToOperateOn(item -> item.is(HexicalItems.CONJURED_COMPASS_ITEM.get()));
		if (data == null)
			return;
		ItemStack stack = data.stack();
		CompoundTag nbt = stack.getOrCreateTag();
		if (nbt.getString("dimension").equals(env.getWorld().dimensionTypeId().location().toString())) {
			LivingEntity caster = env.getCastingEntity();
			if (caster != null)
				cir.setReturnValue(List.of(new Vec3Iota(new Vec3(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")).subtract(caster.getEyePosition()).normalize())));
			else
				cir.setReturnValue(List.of(new NullIota()));
		} else
			cir.setReturnValue(List.of(new NullIota()));
	}
}
