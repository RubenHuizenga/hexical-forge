package miyucomics.hexical.registry

import miyucomics.hexical.HexicalMain
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.item.alchemy.PotionBrewing
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.brewing.BrewingRecipeRegistry
import net.minecraftforge.common.brewing.IBrewingRecipe
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraft.core.Registry
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object HexicalPotions {
	private val EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, HexicalMain.MOD_ID)
    private val POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, HexicalMain.MOD_ID)

    @JvmField
	val WOOLEYED_EFFECT: RegistryObject<MobEffect> = EFFECTS.register("wooleyed") { WooleyedEffect() }
    val WOOLEYED_POTION: RegistryObject<Potion> = POTIONS.register("wooleyed") { 
        Potion(MobEffectInstance(WOOLEYED_EFFECT.get(), 12000, 0)) 
    }
    val LONG_WOOLEYED_POTION: RegistryObject<Potion> = POTIONS.register("long_wooleyed") { 
        Potion(MobEffectInstance(WOOLEYED_EFFECT.get(), 48000, 0)) 
    }
    val STRONG_WOOLEYED_POTION: RegistryObject<Potion> = POTIONS.register("strong_wooleyed") { 
        Potion(MobEffectInstance(WOOLEYED_EFFECT.get(), 6000, 1)) 
    }

	fun init() {
		EFFECTS.register(MOD_BUS)
        POTIONS.register(MOD_BUS)
	}
    
    private fun createPotion(potion: Potion) : ItemStack {
        return PotionUtils.setPotion(ItemStack(Items.POTION), potion);
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork {
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(createPotion(Potions.AWKWARD).getItem()),
                Ingredient.of(HexicalBlocks.PERIWINKLE_FLOWER_ITEM.get()),
                createPotion(WOOLEYED_POTION.get())
			)
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(createPotion(WOOLEYED_POTION.get()).getItem()),
                Ingredient.of(Items.REDSTONE),
                createPotion(LONG_WOOLEYED_POTION.get())
            )
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(createPotion(WOOLEYED_POTION.get()).getItem()),
                Ingredient.of(Items.GLOWSTONE_DUST),
                createPotion(STRONG_WOOLEYED_POTION.get())
            )
        }
    }

	private class PotionBrewing(
		private val inputPotion: Potion,
		private val ingredient: Ingredient,
		private val outputPotion: Potion
	) : IBrewingRecipe {
		override fun isInput(input: ItemStack): Boolean {
			return PotionUtils.getPotion(input) == inputPotion
		}

		override fun isIngredient(ingredient: ItemStack): Boolean {
			return this.ingredient.test(ingredient)
		}

		override fun getOutput(input: ItemStack, ingredient: ItemStack): ItemStack {
			if (!isInput(input) || !isIngredient(ingredient)) 
				return ItemStack.EMPTY
			
			return PotionUtils.setPotion(input.copy(), outputPotion)
		}
	}
}

class WooleyedEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0xff_a678f1.toInt())