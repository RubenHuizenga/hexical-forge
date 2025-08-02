package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.mediaBarColor
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import miyucomics.hexical.features.charms.CharmUtilities.CHARMED_COLOR
import miyucomics.hexical.features.charms.CharmUtilities.getMaxMedia
import miyucomics.hexical.features.charms.CharmUtilities.getMedia
import miyucomics.hexical.features.charms.CharmUtilities.isStackCharmed
import miyucomics.hexical.misc.RenderUtils
import miyucomics.hexical.misc.InitHook
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor

object CharmedItemTooltip : InitHook() {
	override fun init() {
		MinecraftForge.EVENT_BUS.register(::initItemTooltipCallbackCurio)
	}

	fun initItemTooltipCallbackCurio(event: ItemTooltipEvent) {
		if (!isStackCharmed(event.itemStack))
			return
		val media = getMedia(event.itemStack)
		val maxMedia = getMaxMedia(event.itemStack)
		event.toolTip.add(Component.translatable("hexical.charmed").withStyle { style -> style.withColor(CHARMED_COLOR) })
		event.toolTip.add(Component.translatable("hexcasting.tooltip.media_amount.advanced",
			Component.literal(RenderUtils.DUST_AMOUNT.format((media / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { style -> style.withColor(ItemMediaHolder.HEX_COLOR) },
			Component.translatable("hexcasting.tooltip.media", RenderUtils.DUST_AMOUNT.format((maxMedia / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { style -> style.withColor(ItemMediaHolder.HEX_COLOR) },
			Component.literal(RenderUtils.PERCENTAGE.format((100f * media / maxMedia).toDouble()) + "%").withStyle { style -> style.withColor(TextColor.fromRgb(mediaBarColor(media, maxMedia))) }
		))
	}
}