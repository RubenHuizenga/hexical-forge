package miyucomics.hexical.datagen

import miyucomics.hexical.datagen.generators.*
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.common.data.ForgeAdvancementProvider

@Mod.EventBusSubscriber(modid = "hexical", bus = Mod.EventBusSubscriber.Bus.MOD)
object HexicalDatagen {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput
        val registries = event.lookupProvider
        val helper = event.existingFileHelper

        Transmutations.init()

		generator.addProvider(event.includeServer(), ForgeAdvancementProvider(output, registries, helper, listOf(HexicalAdvancementGenerator())))
		generator.addProvider(event.includeServer(), HexicalBlockLootTableGenerator(output))
		generator.addProvider(event.includeClient(), HexicalModelGenerator(output, helper))
		generator.addProvider(event.includeClient(), HexicalPatchouliGenerator(output))
        generator.addProvider(event.includeServer(), HexicalRecipeGenerator(output))
    }
}