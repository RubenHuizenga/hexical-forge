package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.world.level.Level
import net.minecraft.nbt.CompoundTag

class GrimoireItem : Item(Properties().stacksTo(1)) {
	override fun appendHoverText(stack: ItemStack, world: Level?, list: MutableList<Component>, tooltipContext: TooltipFlag) {
		if (!stack.hasTag()) {
			super.appendHoverText(stack, world, list, tooltipContext)
			return
		}

		val metadata = stack.tag!!.getCompound("metadata")
		val text = Component.translatable("hexical.grimoire.contains")

		val components = metadata.allKeys.map { key -> PatternIota(
			HexPattern.fromAngles(
				key, 
				HexDir.values()[metadata.getCompound(key).getInt("direction")]
			)
		).display() }

		if (components.isNotEmpty()) {
			text.append(components[0])
			for (i in 1 until components.size) {
				text.append(", ").append(components[i])
			}
		}

		list.add(text.withStyle(ChatFormatting.GRAY))
		super.appendHoverText(stack, world, list, tooltipContext)
	}

	// Replaces PacketByteBufMixin.java
	override fun shouldOverrideMultiplayerNbt() = true

	override fun getShareTag(stack: ItemStack): CompoundTag? {
		val og = stack.getTag()
		if (og != null)
			og.remove("expansions");
		return og;
	}
}