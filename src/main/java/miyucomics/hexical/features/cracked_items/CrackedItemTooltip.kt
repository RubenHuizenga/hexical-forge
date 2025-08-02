package miyucomics.hexical.features.cracked_items

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import miyucomics.hexical.features.charms.CharmUtilities
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.misc.InitHook
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting

object CrackedItemTooltip : InitHook() {
	override fun init() {
		MinecraftForge.EVENT_BUS.register(::initItemTooltipCallback)
		MinecraftForge.EVENT_BUS.register(::initItemTooltipCallbackCurio)
	}

	fun initItemTooltipCallback(event: ItemTooltipEvent) {
		val nbt = event.itemStack.tag ?: return
			if (event.itemStack.item !is ItemPackagedHex || !nbt.getBoolean("cracked"))
				return
			if (nbt.contains(ItemPackagedHex.TAG_PROGRAM))
				event.toolTip.add("hexical.cracked.hex".asTranslatedComponent(nbt.getList(ItemPackagedHex.TAG_PROGRAM, Tag.TAG_COMPOUND.toInt())))
			else
				event.toolTip.add("hexical.cracked.cracked".asTranslatedComponent.withStyle(ChatFormatting.GOLD))
	}

	fun initItemTooltipCallbackCurio(event: ItemTooltipEvent) {
		val nbt = event.itemStack.tag ?: return
		if (event.itemStack.item !is CurioItem || !nbt.getBoolean("cracked"))
			return
		if (CharmUtilities.isStackCharmed(event.itemStack))
			event.toolTip.add("hexical.cracked.hex".asTranslatedComponent(getText(CharmUtilities.getCompound(event.itemStack).getList("hex", Tag.TAG_COMPOUND.toInt()))))
		else
			event.toolTip.add("hexical.cracked.cracked".asTranslatedComponent.withStyle(ChatFormatting.GOLD))
	}

	private fun getText(hex: ListTag) = hex.fold(Component.empty()) { acc, curr -> acc.append(IotaType.getDisplay(curr.asCompound)) }
}