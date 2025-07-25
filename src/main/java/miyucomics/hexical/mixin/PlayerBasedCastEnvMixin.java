package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import miyucomics.hexical.features.media_log.MediaLogField;
import miyucomics.hexical.features.media_log.MediaLogFieldKt;
import miyucomics.hexical.features.periwinkle.WooleyedEffect;
import miyucomics.hexical.inits.HexicalItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerBasedCastEnv.class, remap = false)
public class PlayerBasedCastEnvMixin {
	@Shadow @Final protected ServerPlayer caster;

	@WrapMethod(method = "canOvercast")
	private boolean canOvercast(Operation<Boolean> original) {
		if (this.caster.getItemBySlot(EquipmentSlot.HEAD).is(HexicalItems.LEI.get()) || this.caster.hasEffect(WooleyedEffect.INSTANCE))
			return false;
		return original.call();
	}

	@Inject(method = "sendMishapMsgToPlayer(Lat/petrak/hexcasting/api/casting/eval/sideeffects/OperatorSideEffect$DoMishap;)V", at = @At("HEAD"))
	private void captureMishap(OperatorSideEffect.DoMishap mishap, CallbackInfo ci) {
		Component message = mishap.getMishap().errorMessageWithName((CastingEnvironment) (Object) this, mishap.getErrorCtx());
		if (message != null && MediaLogField.isEnvCompatible((CastingEnvironment) (Object) this))
			MediaLogFieldKt.getMediaLog(caster).saveMishap(message);
	}
}