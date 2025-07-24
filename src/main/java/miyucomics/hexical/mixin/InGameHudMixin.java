package miyucomics.hexical.mixin;

import miyucomics.hexical.HexicalMain;
import miyucomics.hexical.registry.HexicalPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
	@Unique
	private static final ResourceLocation HEARTS = HexicalMain.id("textures/gui/amethyst_hearts.png");

	@Inject(method = "renderHeart", at = @At("HEAD"), cancellable = true)
	private void amethystHearts(GuiGraphics guiGraphics, Gui.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart, CallbackInfo ci) {
		Player player = Minecraft.getInstance().player;
		if (player == null)
			return;
		if (!player.hasEffect(HexicalPotions.WOOLEYED_EFFECT.get()))
			return;
		if (type == Gui.HeartType.NORMAL) {
			guiGraphics.blit(HEARTS, x, y, halfHeart ? 9 : 0, v, 9, 9);
			ci.cancel();
		} else if (type == Gui.HeartType.CONTAINER) {
			guiGraphics.blit(HEARTS, x, y, 18, v, 9, 9);
			ci.cancel();
		}
	}
}