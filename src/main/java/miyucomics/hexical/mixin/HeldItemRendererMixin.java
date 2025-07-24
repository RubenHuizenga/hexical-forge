package miyucomics.hexical.mixin;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import miyucomics.hexical.data.EvokeState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.FastColor;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemInHandRenderer.class)
public class HeldItemRendererMixin {
	@Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "HEAD"))
	void spawnEvokingParticles(LivingEntity entity, ItemStack itemStack, ItemDisplayContext modelTransformationMode, boolean bl, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
		if (entity instanceof LocalPlayer player && EvokeState.isEvoking(entity.getUUID())) {
			ClientLevel world = (ClientLevel) entity.level();
			Vector3f offset = matrixStack.last().pose().transformPosition(new Vector3f(0, 0, 0));
			Vector3f position = offset.add((float) entity.getX(), (float) entity.getY(), (float) entity.getZ());
			int color = IXplatAbstractions.INSTANCE.getPigment(player).getColorProvider().getColor(world.getGameTime() * 10, player.position());
			float r = FastColor.ARGB32.red(color) / 255f;
			float g = FastColor.ARGB32.green(color) / 255f;
			float b = FastColor.ARGB32.blue(color) / 255f;
			world.addParticle(ParticleTypes.ENTITY_EFFECT, position.x, position.y, position.z, r, g, b);
		}
	}
}