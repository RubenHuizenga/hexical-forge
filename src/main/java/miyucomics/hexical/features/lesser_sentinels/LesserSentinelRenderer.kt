package miyucomics.hexical.features.lesser_sentinels

import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.mojang.blaze3d.systems.RenderSystem
import miyucomics.hexical.misc.ClientStorage
import miyucomics.hexical.misc.RenderUtils
import miyucomics.hexical.misc.InitHook
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.math.Axis
import net.minecraft.world.phys.Vec2
import kotlin.math.cos
import kotlin.math.sin

object LesserSentinelRenderer : InitHook() {
	override fun init() {
		MinecraftForge.EVENT_BUS.register(::initWorldLastRender)
	}

	fun initWorldLastRender(event: RenderLevelStageEvent) {
		ClientStorage.lesserSentinels.forEach { pos ->
			val matrices = event.poseStack
			val camera = event.camera
			val camPos = camera.position

			matrices.pushPose()
			matrices.translate(pos.x - camPos.x, pos.y - camPos.y, pos.z - camPos.z)

			matrices.mulPose(Axis.YP.rotationDegrees(-camera.yRot))
			matrices.mulPose(Axis.XP.rotationDegrees(camera.xRot))

			val tessellator = Tesselator.getInstance()
			val bufferBuilder = tessellator.builder

			RenderSystem.disableDepthTest()
			RenderSystem.enableBlend()
			RenderSystem.defaultBlendFunc()
			RenderSystem.disableCull()
			RenderSystem.setShader(GameRenderer::getPositionColorShader)

			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)

			val points = mutableListOf<Vec2>()
			for (i in 0..6) {
				val angle = (i % 6) * (Math.PI / 3)
				points.add(Vec2(cos(angle).toFloat(), sin(angle).toFloat()).scale(0.25f))
			}

			val pigment = IXplatAbstractions.INSTANCE.getPigment(Minecraft.getInstance().player!!).colorProvider
			fun makeVertex(offset: Vec2) = bufferBuilder.vertex(matrices.last().pose(), offset.x, offset.y, 0f)
				.color(pigment.getColor(ClientStorage.ticks.toFloat(), pos.add(offset.x.toDouble() * 2, offset.y.toDouble() * 2, 0.0)))
				.endVertex()
			RenderUtils.quadifyLines(::makeVertex, 0.05f, points)

			tessellator.end()

			RenderSystem.enableCull()
			RenderSystem.disableBlend()
			RenderSystem.enableDepthTest()

			matrices.popPose()
		}
	}
}