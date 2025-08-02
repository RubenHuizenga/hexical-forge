package miyucomics.hexical.features.media_jar

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry
import miyucomics.hexical.inits.HexicalBlocks.MEDIA_JAR_BLOCK
import miyucomics.hexical.inits.HexicalBlocks.MEDIA_JAR_BLOCK_ENTITY
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

object MediaJarRenderHooks : InitHook() {
	override fun init() {
		// Set in the blockmodel json instead with "render_type": "minecraft:cutout"
		// BlockRenderLayerMap.INSTANCE.putBlock(MEDIA_JAR_BLOCK, RenderLayer.getCutout())
		MinecraftForge.EVENT_BUS.register(::initBlockEntityRenderer)
		ScryingLensOverlayRegistry.addDisplayer(MEDIA_JAR_BLOCK.get()) { lines, _, pos, _, world, _ -> (world.getBlockEntity(pos) as MediaJarBlockEntity).scryingLensOverlay(lines) }
	}

	fun initBlockEntityRenderer(event: FMLClientSetupEvent) {
		event.enqueueWork {
			BlockEntityRenderers.register(MEDIA_JAR_BLOCK_ENTITY.get(), MediaJarRendererFactory)
		}
	}
}