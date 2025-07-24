package miyucomics.hexical.screens

import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.TAU
import com.mojang.blaze3d.systems.RenderSystem
import miyucomics.hexical.utils.RenderUtils
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.layouts.SpacerElement
import com.mojang.blaze3d.vertex.*
import net.minecraft.world.phys.Vec2
import kotlin.math.cos
import kotlin.math.sin
import net.minecraft.client.renderer.GameRenderer

class DisplayWidget(pattern: HexPattern?, private val x: Int, private val y: Int) : SpacerElement(x, y, PATTERN_SIZE, PATTERN_SIZE), Renderable {
	private var points: List<Vec2>? = listOf()

	companion object {
		const val PATTERN_SIZE = 14
	}

	init {
		setPattern(pattern)
	}

	fun setPattern(pattern: HexPattern?) {
		if (pattern == null) {
			this.points = null
			return
		}
		this.points = RenderUtils.getNormalizedStrokes(pattern).map { Vec2(it.x, -it.y).scale(PATTERN_SIZE.toFloat()) }
	}

	override fun render(drawContext: GuiGraphics, mouseX: Int, mouseY: Int, f: Float) {
		val matrices = drawContext.pose().last()
		val buffer = Tesselator.getInstance().builder
		RenderSystem.setShader(GameRenderer::getPositionColorShader)

		if (points != null) {
			buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)
			fun makeVertex(pos: Vec2) = buffer.vertex(matrices.pose(), pos.x + x + PATTERN_SIZE / 2, pos.y + y + PATTERN_SIZE / 2, 0f).color(1f, 1f, 1f, 1f).endVertex()
			RenderUtils.quadifyLines(::makeVertex, 0.5f, points!!)
		} else {
			buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR)
			for (i in 0..6) {
				val theta = -i.toFloat() / 6f * TAU.toFloat()
				buffer.vertex(matrices.pose(), cos(theta) + x + PATTERN_SIZE / 2, sin(theta) + y + PATTERN_SIZE / 2, 0f).color(0.42f, 0.44f, 0.53f, 1f).endVertex()
			}
		}

		BufferUploader.drawWithShader(buffer.end())
	}
}