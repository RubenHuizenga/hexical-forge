package miyucomics.hexical.screens

import miyucomics.hexical.client.ClientStorage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting

class StackWidget(private val x: Int, private val y: Int, width: Int, height: Int) : SpacerElement(x, y, width, height), Renderable {
	override fun render(drawContext: GuiGraphics, mouseX: Int, mouseY: Int, f: Float) {
		val renderer = Minecraft.getInstance().font
		drawContext.drawString(renderer, Component.translatable("hexical.ledger.stack").withStyle(ChatFormatting.BOLD), x, y, 0xffffff)
		var i = 1
		ClientStorage.ledger.stack.buffer().reversed().withIndex().forEach { text ->
			renderer.split(text.value, width).forEach {
				drawContext.drawString(renderer, it, x, y + i * 16, 0xffffff)
				i++
			}
		}
	}
}