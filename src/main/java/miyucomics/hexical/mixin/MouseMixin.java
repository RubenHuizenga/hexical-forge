package miyucomics.hexical.mixin;

import at.petrak.hexcasting.common.lib.HexSounds;
import io.netty.buffer.Unpooled;
import kotlin.Pair;
import miyucomics.hexical.registry.HexicalNetworking;
import miyucomics.hexical.registry.HexicalNetworking.CharmedItemUsePacket;
import miyucomics.hexical.utils.CharmedItemUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = MouseHandler.class)
public class MouseMixin {
	@Shadow @Final private Minecraft minecraft;

	@Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
	private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
		if (minecraft.screen != null || minecraft.getOverlay() != null) return;
		if (minecraft.player == null || minecraft.player.isSpectator()) return;
		if (action != GLFW.GLFW_PRESS) return;

		int buttonPressed = switch (button) {
			case GLFW.GLFW_MOUSE_BUTTON_1 -> 0; // left
			case GLFW.GLFW_MOUSE_BUTTON_2 -> 1; // right
			case GLFW.GLFW_MOUSE_BUTTON_3 -> 2; // middle
			case GLFW.GLFW_MOUSE_BUTTON_4 -> 3;
			case GLFW.GLFW_MOUSE_BUTTON_5 -> 4;
			case GLFW.GLFW_MOUSE_BUTTON_6 -> 5;
			case GLFW.GLFW_MOUSE_BUTTON_7 -> 6;
			case GLFW.GLFW_MOUSE_BUTTON_8 -> 7;
			default -> -1;
		};

		if (buttonPressed == -1)
			return;

		for (Pair<InteractionHand, ItemStack> pair : CharmedItemUtilities.getUseableCharmedItems(minecraft.player)) {
			if (!CharmedItemUtilities.shouldIntercept(pair.getSecond(), buttonPressed, minecraft.player.isShiftKeyDown()))
				continue;

			minecraft.player.swing(pair.getFirst());
			minecraft.player.level().playSound(minecraft.player, minecraft.player.getX(), minecraft.player.getY(), minecraft.player.getZ(), HexSounds.CAST_HERMES, SoundSource.PLAYERS, 1f, 1f);
			CharmedItemUsePacket packet = new CharmedItemUsePacket(buttonPressed, pair.getFirst());
			HexicalNetworking.sendToServer(packet);
			ci.cancel();
			return;
		}
	}
}