package miyucomics.hexical

import miyucomics.hexical.features.animated_scrolls.AnimatedPatternTooltipComponent
import miyucomics.hexical.features.animated_scrolls.AnimatedPatternTooltip
import miyucomics.hexical.features.media_jar.MediaJarItemRenderer
import miyucomics.hexical.features.media_jar.MediaJarBlockEntityRenderer
import miyucomics.hexical.inits.*
import miyucomics.hexical.misc.ClientStorage
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.api.distmarker.Dist
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.common.MinecraftForge
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
class HexicalClient {
	@SubscribeEvent
	fun clientInit(event: FMLClientSetupEvent) {
		HexicalBlocks.clientInit()
		MOD_BUS.addListener(HexicalEntities::clientInit)
		MOD_BUS.addListener(HexicalKeybinds::clientInit)
		HexicalParticles.clientInit()

		HexicalHooksClient.init()

		//ItemBlockRenderTypes.setRenderLayer(HexicalBlocks.MEDIA_JAR_BLOCK.get(), RenderType.cutout())
		// MinecraftForge.EVENT_BUS.addListener { _: FMLClientSetupEvent ->
		// 	ItemProperties.register(
		// 		HexicalBlocks.MEDIA_JAR_ITEM.get(),
		// 		ResourceLocation("hexical", "custom_render"),
		// 		{ _, _, _, _ -> 1f }
		// 	)
			
		// 	BlockEntityRenderers.register(
		// 		HexicalBlocks.MEDIA_JAR_BLOCK_ENTITY.get(),
		// 		{ ctx -> MediaJarBlockEntityRenderer(ctx) }
		// 	)
		// }
	}

	@SubscribeEvent
	fun initTickEvent(event: TickEvent.ClientTickEvent) {
		if (event.phase == TickEvent.Phase.END) {
			ClientStorage.ticks += 1
		}
	}

	@SubscribeEvent
    fun registerTooltipComponents(evt: RegisterClientTooltipComponentFactoriesEvent) {
        evt.register(AnimatedPatternTooltip::class.java, AnimatedPatternTooltipComponent::tryConvert);
    }
}
