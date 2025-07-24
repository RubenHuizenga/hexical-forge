package miyucomics.hexical.items

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.mediaBarColor
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import miyucomics.hexical.blocks.MediaJarBlock
import miyucomics.hexical.registry.HexicalBlocks
import miyucomics.hexical.utils.RenderUtils
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.world.level.Level
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import java.util.function.Consumer
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer

class MediaJarItem : BlockItem(HexicalBlocks.MEDIA_JAR_BLOCK.get(), Properties().stacksTo(1)) {
	override fun appendHoverText(stack: ItemStack, world: Level?, list: MutableList<Component>, tooltipContext: TooltipFlag) {
		val tag = stack.tag?.getCompound("BlockEntityTag")
		val media = tag?.getLong("media") ?: 0
		list.add(Component.translatable("hexcasting.tooltip.media_amount.advanced",
			Component.literal(RenderUtils.DUST_AMOUNT.format((media / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { style -> style.withColor(ItemMediaHolder.HEX_COLOR) },
			Component.translatable("hexcasting.tooltip.media", RenderUtils.DUST_AMOUNT.format((MediaJarBlock.MAX_CAPACITY / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { style -> style.withColor(ItemMediaHolder.HEX_COLOR) },
			Component.literal(RenderUtils.PERCENTAGE.format((100f * media / MediaJarBlock.MAX_CAPACITY).toDouble()) + "%").withStyle { style -> style.withColor(TextColor.fromRgb(mediaBarColor(media, MediaJarBlock.MAX_CAPACITY))) }
		))
	}

    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) {
        consumer.accept(object : IClientItemExtensions {
            override fun getCustomRenderer() = MediaJarItemRenderer()
        })
    }
}