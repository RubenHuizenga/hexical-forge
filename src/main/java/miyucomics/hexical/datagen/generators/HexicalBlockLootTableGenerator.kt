package miyucomics.hexical.datagen.generators

import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets

class HexicalBlockLootTableGenerator(output: PackOutput) : LootTableProvider(
    output,
    emptySet(),
    listOf(
        LootTableProvider.SubProviderEntry(::Provider, LootContextParamSets.BLOCK)
    )
) {
    private class Provider : BlockLootSubProvider(emptySet(), FeatureFlags.REGISTRY.allFlags()) {
        override fun generate() {
            dropSelf(HexicalBlocks.CASTING_CARPET.get())
            add(HexicalBlocks.HEX_CANDLE_BLOCK.get(), createCandleDrops(HexicalBlocks.HEX_CANDLE_BLOCK.get()))
            dropSelf(HexicalBlocks.PEDESTAL_BLOCK.get())
            add(HexicalBlocks.PERIWINKLE_FLOWER.get(), createPotFlowerItemTable(HexicalBlocks.PERIWINKLE_FLOWER.get()))
            dropSelf(HexicalBlocks.SENTINEL_BED_BLOCK.get())
        }

        override fun getKnownBlocks(): Iterable<Block> = listOf(
            HexicalBlocks.CASTING_CARPET.get(),
            HexicalBlocks.HEX_CANDLE_BLOCK.get(),
            HexicalBlocks.PEDESTAL_BLOCK.get(),
            HexicalBlocks.PERIWINKLE_FLOWER.get(),
            HexicalBlocks.SENTINEL_BED_BLOCK.get()
        )
    }
}