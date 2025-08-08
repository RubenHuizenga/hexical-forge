package miyucomics.hexical

import miyucomics.hexical.features.animated_scrolls.AnimatedPatternTooltipComponent
import miyucomics.hexical.features.animated_scrolls.AnimatedPatternTooltip
import miyucomics.hexical.features.media_jar.MediaJarItemRenderer
import miyucomics.hexical.features.media_jar.MediaJarBlockEntityRenderer
import miyucomics.hexical.features.media_jar.MediaJarRendererFactory
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
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.common.MinecraftForge
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object HexicalClient {
	@SubscribeEvent
	fun clientInit(event: FMLClientSetupEvent) {
		HexicalBlocks.clientInit()
		MOD_BUS.addListener(HexicalEntities::clientInit)
		MOD_BUS.addListener(HexicalKeybinds::clientInit)
		HexicalParticles.clientInit()

		HexicalHooksClient.init()
	}

	@SubscribeEvent
    fun registerTooltipComponents(evt: RegisterClientTooltipComponentFactoriesEvent) {
        evt.register(AnimatedPatternTooltip::class.java, AnimatedPatternTooltipComponent::tryConvert)
    }

	@SubscribeEvent
	fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
		event.registerBlockEntityRenderer(HexicalBlocks.MEDIA_JAR_BLOCK_ENTITY.get()) {
			MediaJarRendererFactory.create(it)
		}
	}
}

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object HexicalClientForge {
	@SubscribeEvent
	fun initTickEvent(event: TickEvent.ClientTickEvent) {
		if (event.phase == TickEvent.Phase.END) {
			ClientStorage.ticks += 1
		}
	}
}