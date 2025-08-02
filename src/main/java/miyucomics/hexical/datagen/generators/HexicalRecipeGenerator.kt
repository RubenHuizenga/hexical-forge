package miyucomics.hexical.datagen.generators

import at.petrak.hexcasting.common.lib.HexItems
import miyucomics.hexical.datagen.Transmutations
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.SingleItemRecipeBuilder
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.PackOutput
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.registries.ForgeRegistries
import net.minecraft.resources.ResourceLocation
import java.util.function.Consumer
import java.io.DataOutput

class HexicalRecipeGenerator(output: PackOutput) : RecipeProvider(output) {
    override fun buildRecipes(consumer: Consumer<FinishedRecipe>) {
        HexicalItems.CURIOS.forEach { curio ->
            SingleItemRecipeBuilder.stonecutting(
                Ingredient.of(HexItems.CHARGED_AMETHYST),
                RecipeCategory.MISC,
                curio.get(),
                1
            )
            .unlockedBy(
                "has_charged_amethyst",
                has(HexItems.CHARGED_AMETHYST)
            )
            .save(consumer, ResourceLocation(
                "curio/${ForgeRegistries.ITEMS.getKey(curio.get())!!.path}_from_stonecutting"
            ))
        }

        Transmutations.transmutationRecipeJsons.forEach {
            consumer.accept(it)
        }
    }
}