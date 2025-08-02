package miyucomics.hexical.features.media_log

import at.petrak.hexcasting.client.render.*
import miyucomics.hexical.misc.ClientStorage
import miyucomics.hexical.misc.ClientStorage.ticks
import miyucomics.hexical.misc.InitHook
import net.minecraftforge.event.TickEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.client.event.RenderGuiEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.util.FastColor
import net.minecraft.util.Mth
import kotlin.math.max
import kotlin.math.min

object MediaLogRenderer : InitHook() {
	const val FADE_IN_DURATION: Int = 40
	var fadingInLog = false
	var fadingInLogStart = 0
	var fadingInLogTweener = 0

	override fun init() {
		MinecraftForge.EVENT_BUS.addListener(::initTickEvent)
		MinecraftForge.EVENT_BUS.addListener(::initHudRenderEvent)
	}

	fun initTickEvent(event: TickEvent.ClientTickEvent) {
		if (event.phase == TickEvent.Phase.END) {
			fadingInLogTweener = if (fadingInLog) min(ticks - fadingInLogStart, FADE_IN_DURATION) else max(fadingInLogTweener - 5, 0)
		}
	}

	fun initHudRenderEvent(event: RenderGuiEvent.Post) {
		if (fadingInLogTweener == 0) return
		val context = event.guiGraphics
		val progress = (fadingInLogTweener + event.partialTick) / FADE_IN_DURATION.toFloat()

		val backgroundColor = FastColor.ARGB32.color((progress * 100).toInt(), 0, 0, 0)
		context.fillGradient(0, 0, context.guiWidth(), context.guiHeight(), backgroundColor, backgroundColor)

		context.pose().pushPose()
		context.pose().translate(context.guiWidth() / 2f, context.guiHeight() / 2f, 0f)

		for (phase in phases) {
			val localProgress = (progress - phase.start) / phase.duration
			if (localProgress in 0f..1f)
				phase.render(context, localProgress)
		}

		context.pose().popPose()
	}

	fun drawMishapText(context: GuiGraphics, alpha: Float) {
		val mishapText = ClientStorage.mediaLog.mishap
		context.drawCenteredString(Minecraft.getInstance().font, mishapText, 0, -context.guiHeight() / 2 + 10, FastColor.ARGB32.color((alpha * 255).toInt(), 255, 255, 255))
	}

	fun drawMediaLogPattern(matrices: PoseStack, index: Int, alpha: Float) {
		matrices.pushPose()
		matrices.translate((index - 8).toFloat() * 30f - 12.5f, -12.5f, 0f)
		matrices.scale(25f, 25f, 25f)

		val color = FastColor.ARGB32.color((alpha * 255).toInt(), 255, 255, 255)
		val patternlike = HexPatternLike.of(ClientStorage.mediaLog.patterns.buffer()[index])
		val patternSettings = WorldlyPatternRenderHelpers.WORLDLY_SETTINGS_WOBBLY
		val staticPoints = HexPatternPoints.getStaticPoints(patternlike, patternSettings, 0.0)
		val nonzappyLines = patternlike.nonZappyPoints
		val zappyPattern = makeZappy(nonzappyLines, findDupIndices(nonzappyLines), patternSettings.hops, patternSettings.variance, patternSettings.speed, patternSettings.flowIrregular, patternSettings.readabilityOffset, patternSettings.lastSegmentProp, 0.0)
		drawLineSeq(matrices.last().pose(), staticPoints.scaleVecs(zappyPattern), 0.05f, color, color, VCDrawHelper.getHelper(null, matrices, 0.001f))

		matrices.popPose()
	}

	fun drawStackItem(context: GuiGraphics, index: Int, alpha: Float) {
		if (index >= ClientStorage.mediaLog.stack.buffer().size || alpha == 0f)
			return
		context.pose().pushPose()
		val iotas = ClientStorage.mediaLog.stack.buffer()
		context.drawCenteredString(Minecraft.getInstance().font, iotas[index], 17, 16 * (4 - index), FastColor.ARGB32.color((alpha * 255).toInt(), 255, 255, 255))
		context.pose().popPose()
	}

	private val phases = listOf(
		Phase(0.0f, 0.2f) { ctx, t ->
			drawMishapText(ctx, t)
		},
		Phase(0.2f, 0.5f) { ctx, t ->
			val progress = Mth.clamp(t, 0f, 1f)
			val visible = (progress * 16).toInt()
			val alpha = (progress * 16) % 1
			for (i in 0 until visible)
				drawMediaLogPattern(ctx.pose(), i, 1f)
			if (visible < 15)
				drawMediaLogPattern(ctx.pose(), visible, alpha)
		},
		Phase(0.7f, 0.3f) { ctx, t ->
			val progress = Mth.clamp(t, 0f, 1f)
			val visible = (progress * 8).toInt()
			val alpha = (progress * 8) % 1
			for (i in 0 until visible)
				drawStackItem(ctx, i, 1f)
			if (visible < 7)
				drawStackItem(ctx, visible, alpha)
		}
	)

	private data class Phase(
		val start: Float,
		val duration: Float,
		val render: (GuiGraphics, Float) -> Unit
	)
}

