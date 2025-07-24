package miyucomics.hexical.entities.specklikes

import at.petrak.hexcasting.api.HexAPI.modLoc
import miyucomics.hexical.utils.RenderUtils
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.RenderType
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.resources.ResourceLocation
import com.mojang.math.Axis
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class SpeckRenderer(ctx: EntityRendererProvider.Context) : EntityRenderer<SpeckEntity>(ctx) {
	override fun getTextureLocation(entity: SpeckEntity): ResourceLocation? = null
	override fun shouldRender(entity: SpeckEntity, frustum: Frustum, x: Double, y: Double, z: Double) = true
	override fun render(entity: SpeckEntity, yaw: Float, tickDelta: Float, matrices: PoseStack, vertexConsumers: MultiBufferSource, light: Int) {
		matrices.pushPose()
		matrices.translate(0.0, 0.25, 0.0)
		if (entity.clientIsText)
			matrices.translate(0.0, 0.125, 0.0)
		matrices.mulPose(Axis.YP.rotationDegrees(-entity.yRot))
		matrices.mulPose(Axis.XP.rotationDegrees(entity.xRot))
		matrices.mulPose(Axis.ZP.rotationDegrees(entity.clientRoll))
		matrices.scale(entity.clientSize, entity.clientSize, entity.clientSize)

		if (entity.clientIsText) {
			matrices.scale(0.025f, -0.025f, 0.025f)
			val top = matrices.last()
			val xOffset = -font.width(entity.clientText) / 2f
			font.drawInBatch(entity.clientText, xOffset, 0f, -0x1, false, top.pose(), vertexConsumers, Font.DisplayMode.NORMAL, 0x00000000, light)
		} else {
			val top = matrices.last()
			val buffer = vertexConsumers.getBuffer(renderLayer)
			fun makeVertex(pos: Vec2) = buffer.vertex(top.pose(), pos.x, pos.y, 0f)
				.color(entity.clientPigment.colorProvider.getColor(0f, Vec3(pos.x.toDouble(), pos.y.toDouble(), 0.0).scale(2.0).add(entity.position())))
				.uv(0f, 0f)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(LightTexture.FULL_BRIGHT)
				.normal(top.normal(), 0f, 1f, 0f)
				.endVertex()

			RenderUtils.quadifyLines(::makeVertex, entity.clientThickness * 0.05f / entity.clientSize, entity.clientVerts)
		}

		matrices.popPose()
	}

	companion object {
		private val renderLayer = RenderType.entityCutoutNoCull(modLoc("textures/entity/white.png"))
	}
}