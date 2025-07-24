package miyucomics.hexical

import miyucomics.hexical.registry.HexicalBlocks
import miyucomics.hexical.registry.HexicalItems
import miyucomics.hexical.HexicalMain
import net.minecraft.data.DataGenerator
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ConfiguredModel
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.client.model.generators.ModelFile
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object HexicalDataGenerators {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput
        val lookupProvider = event.lookupProvider
        val existingFileHelper = event.existingFileHelper

        if (event.includeClient()) {
            generator.addProvider(true, HexicalBlockStateProvider(output, existingFileHelper))
            generator.addProvider(true, HexicalItemModelProvider(output, existingFileHelper))
        }
    }
}

class HexicalBlockStateProvider(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : BlockStateProvider(output, HexicalMain.MOD_ID, existingFileHelper) {
    override fun registerStatesAndModels() {
        simpleBlock(HexicalBlocks.HEX_CANDLE_BLOCK.get())
        
        getVariantBuilder(HexicalBlocks.HEX_CANDLE_CAKE_BLOCK.get())
            .partialState().modelForState()
            .modelFile(models().getExistingFile(modLoc("block/hex_candle_cake")))
    }
}

private class HexicalItemModelProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) : 
    ItemModelProvider(output, HexicalMain.MOD_ID, existingFileHelper) {
    
    override fun registerModels() {
        with(this) {
            getBuilder(HexicalItems.CONJURED_COMPASS_ITEM.toString())
                .parent(ModelFile.UncheckedModelFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/conjured_compass"))
        }
    }
}