package miyucomics.hexical.registry

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.recipe.TransmutingRecipe
import miyucomics.hexical.recipe.TransmutingSerializer
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.RegisterEvent

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object HexicalRecipe {
    lateinit var TRANSMUTING_RECIPE: RecipeType<TransmutingRecipe>

    @SubscribeEvent
    fun init(event: RegisterEvent) {
        if (event.registryKey == Registries.RECIPE_TYPE) {
            event.register(Registries.RECIPE_TYPE, HexicalMain.id("transmuting")) {
                TransmutingRecipe.Type.INSTANCE
            }
            TRANSMUTING_RECIPE = TransmutingRecipe.Type.INSTANCE
        }
        
        if (event.registryKey == Registries.RECIPE_SERIALIZER) {
            event.register(Registries.RECIPE_SERIALIZER, HexicalMain.id("transmuting")) {
                TransmutingSerializer.INSTANCE
            }
        }
    }
}