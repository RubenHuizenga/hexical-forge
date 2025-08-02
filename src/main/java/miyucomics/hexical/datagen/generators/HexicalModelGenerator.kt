package miyucomics.hexical.datagen.generators

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.client.model.generators.ModelFile
import net.minecraftforge.common.data.ExistingFileHelper

class HexicalModelGenerator(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : ItemModelProvider(output, HexicalMain.MOD_ID, existingFileHelper) {

    override fun registerModels() {
        HexicalItems.CURIOS.forEach { curio ->
            when (curio.get()) {
                HexicalItems.CURIO_COMPASS.get() -> compass(curio.getId().path)
                HexicalItems.CURIO_STAFF.get() -> handheldRod(curio.getId().path)
                else -> basicItem(curio.getId())
            }
        }

        HexicalItems.PLUSHIES.forEach { plushie ->
            basicItem(plushie.getId())
        }
    }

    private fun compass(name: String) {
        withExistingParent(name, mcLoc("item/template_compass")).texture(
            "layer0", modLoc("item/$name")
        )
    }

    private fun handheldRod(name: String) {
        withExistingParent(name, mcLoc("item/handheld_rod")).texture(
            "layer0", modLoc("item/$name")
        )
    }
}