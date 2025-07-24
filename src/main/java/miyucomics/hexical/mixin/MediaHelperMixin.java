package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import miyucomics.hexical.interfaces.PlayerEntityMinterface;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MediaHelper.class)
public class MediaHelperMixin {
	@Inject(method = "scanPlayerForMediaStuff", at = @At("RETURN"), remap = false)
	private static void useWristpocketPhial(ServerPlayer player, CallbackInfoReturnable<List<ADMediaHolder>> cir) {
		ItemStack wristpocket = ((PlayerEntityMinterface) player).getWristpocket();
		if (wristpocket.is(HexItems.BATTERY))
			cir.getReturnValue().add(0, IXplatAbstractions.INSTANCE.findMediaHolder(wristpocket));
	}
}