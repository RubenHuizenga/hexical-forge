package miyucomics.hexical.client

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.client.render.*
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.resources.ResourceLocation

class AnimatedPatternTooltipComponent(tooltip: AnimatedPatternTooltip) : ClientTooltipComponent {
	private val color: Int = tooltip.color
	private val state: Int = tooltip.state
	private val pattern: HexPattern = tooltip.pattern

	override fun renderImage(font: Font, mouseX: Int, mouseY: Int, graphics: GuiGraphics) {
		val matrices = graphics.pose()

		matrices.pushPose()
		matrices.translate(mouseX.toFloat(), mouseY.toFloat(), 500f)
		RenderSystem.enableBlend()

		if (state != 2)
			graphics.blit(if (state == 1) ANCIENT_BG else PRISTINE_BG, 0, 0, RENDER_SIZE.toInt(), RENDER_SIZE.toInt(), 0f, 0f, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE)
		matrices.translate(0f, 0f, 100f)
		matrices.scale(RENDER_SIZE, RENDER_SIZE, 1f)

		val patternlike = HexPatternLike.of(pattern)
		val patternSettings = WorldlyPatternRenderHelpers.WORLDLY_SETTINGS_WOBBLY
		val oldShader = RenderSystem.getShader()
		val staticPoints = HexPatternPoints.getStaticPoints(patternlike, patternSettings, 0.0)
		val nonzappyLines = patternlike.nonZappyPoints
		val zappyPattern = makeZappy(nonzappyLines, findDupIndices(nonzappyLines), patternSettings.hops, patternSettings.variance, patternSettings.speed, patternSettings.flowIrregular, patternSettings.readabilityOffset, patternSettings.lastSegmentProp, 0.0)
		drawLineSeq(matrices.last().pose(), staticPoints.scaleVecs(zappyPattern), patternSettings.getInnerWidth(staticPoints.finalScale).toFloat(), color, color, VCDrawHelper.getHelper(null, matrices, 0.001f))
		RenderSystem.setShader { oldShader }

		matrices.popPose()
	}

	override fun getWidth(renderer: Font) = RENDER_SIZE.toInt()
	override fun getHeight() = RENDER_SIZE.toInt()

	companion object {
		val ANCIENT_BG: ResourceLocation = HexAPI.modLoc("textures/gui/scroll_ancient.png")
		val PRISTINE_BG: ResourceLocation = HexAPI.modLoc("textures/gui/scroll.png")

		private const val RENDER_SIZE = 128f
		private const val TEXTURE_SIZE = 48

		fun tryConvert(data: TooltipComponent): ClientTooltipComponent? {
			if (data is AnimatedPatternTooltip)
				return AnimatedPatternTooltipComponent(data)
			return null
		}
	}
}