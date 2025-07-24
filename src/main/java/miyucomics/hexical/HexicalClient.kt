package miyucomics.hexical

import miyucomics.hexical.client.AnimatedPatternTooltipComponent
import miyucomics.hexical.items.MediaJarItemRenderer
import miyucomics.hexical.registry.*
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.api.distmarker.Dist
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.Minecraft
import miyucomics.hexical.client.AnimatedPatternTooltip
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
class HexicalClient {
	@SubscribeEvent
	fun clientInit(event: FMLClientSetupEvent) {
		HexicalBlocks.clientInit(event)
		HexicalItems.clientInit()
		HexicalParticles.clientInit()
	}

	@SubscribeEvent
    fun registerTooltipComponents(evt: RegisterClientTooltipComponentFactoriesEvent) {
        evt.register(AnimatedPatternTooltip::class.java, AnimatedPatternTooltipComponent::tryConvert);
    }
}
