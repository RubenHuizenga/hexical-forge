package miyucomics.hexical

import miyucomics.hexical.features.animated_scrolls.AnimatedPatternTooltipComponent
import miyucomics.hexical.features.media_jar.MediaJarItemRenderer
import miyucomics.hexical.inits.*
import miyucomics.hexical.misc.ClientStorage
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.api.distmarker.Dist
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
class HexicalClient {
	@SubscribeEvent
	fun clientInit(event: FMLClientSetupEvent) {
		HexicalBlocks.clientInit(event)
		HexicalItems.clientInit()
		HexicalParticles.clientInit()

		// New 
		HexicalEntities.clientInit()
		HexicalKeybinds.clientInit()
		HexicalParticles.clientInit()

		HexicalHooksClient.init()

		ClientTickEvents.END_CLIENT_TICK.register { ClientStorage.ticks += 1 }
		BuiltinItemRendererRegistry.INSTANCE.register(HexicalBlocks.MEDIA_JAR_ITEM, MediaJarItemRenderer())
		TooltipComponentCallback.EVENT.register(AnimatedPatternTooltipComponent::tryConvert)
	}

	@SubscribeEvent
    fun registerTooltipComponents(evt: RegisterClientTooltipComponentFactoriesEvent) {
        evt.register(AnimatedPatternTooltip::class.java, AnimatedPatternTooltipComponent::tryConvert);
    }
}
