package miyucomics.hexical.features.media_jar

import miyucomics.hexical.blocks.MediaJarBlock
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.utils.MediaJarRenderStuffs
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.client.model.data.ModelData
import com.mojang.blaze3d.vertex.PoseStack

class MediaJarItemRenderer : BlockEntityWithoutLevelRenderer(
    Minecraft.getInstance().blockEntityRenderDispatcher,
    Minecraft.getInstance().entityModels
) {
	override fun renderByItem(stack: ItemStack, mode: ItemDisplayContext, matrices: PoseStack, vertexConsumers: MultiBufferSource, light: Int, overlay: Int) { 
		Minecraft.getInstance().blockRenderer.renderSingleBlock(HexicalBlocks.MEDIA_JAR_BLOCK.get().defaultBlockState(), matrices, vertexConsumers, light, overlay, ModelData.EMPTY, RenderType.cutout())		
		val tag = stack.tag?.getCompound("BlockEntityTag")
		val media = tag?.getLong("media") ?: 0
		MediaJarRenderStuffs.renderFluid(matrices, vertexConsumers, media.toFloat() / MediaJarBlock.MAX_CAPACITY.toFloat())
	}
}