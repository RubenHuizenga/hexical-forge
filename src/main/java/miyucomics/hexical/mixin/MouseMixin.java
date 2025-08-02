package miyucomics.hexical.mixin;

import at.petrak.hexcasting.common.lib.HexSounds;
import io.netty.buffer.Unpooled;
import kotlin.Pair;
import miyucomics.hexical.features.charms.CharmUtilities;
import miyucomics.hexical.features.charms.ServerCharmedUseReceiver;
import miyucomics.hexical.features.curios.CurioItem;
import miyucomics.hexical.features.charms.ServerCharmedUseReceiver.CharmedItemUsePacket;
import miyucomics.hexical.misc.HexicalNetworking;
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

		for (Pair<InteractionHand, ItemStack> pair : CharmUtilities.getUseableCharmedItems(minecraft.player)) {
			if (!CharmUtilities.shouldIntercept(pair.getSecond(), buttonPressed, minecraft.player.isShiftKeyDown()))
				continue;

			if (!(pair.getSecond().getItem() instanceof CurioItem)) {
				minecraft.player.swing(pair.getFirst());
				minecraft.player.clientLevel.playSound(null, minecraft.player.getX(), minecraft.player.getY(), minecraft.player.getZ(), HexSounds.CAST_HERMES, SoundSource.MASTER, 0.25f, 1f);
			}

			CharmedItemUsePacket packet = new CharmedItemUsePacket(buttonPressed, pair.getFirst());
			HexicalNetworking.sendToServer(packet);
			ci.cancel();
			return;
		}
	}
}