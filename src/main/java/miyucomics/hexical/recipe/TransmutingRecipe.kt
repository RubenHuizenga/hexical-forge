package miyucomics.hexical.recipe

import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

class TransmutingRecipe(private val id: ResourceLocation, val input: Ingredient, val cost: Long, val output: List<ItemStack>) : Recipe<Container> {
	override fun getId() = this.id
	override fun getType() = Type.INSTANCE
	override fun canCraftInDimensions(width: Int, height: Int) = false
	override fun getSerializer() = TransmutingSerializer.INSTANCE
	override fun getResultItem(dynamicRegistryManager: RegistryAccess) = output[0]
	override fun matches(inventory: Container, world: Level) = input.test(inventory.getItem(0))
	override fun assemble(inventory: Container, dynamicRegistryManager: RegistryAccess): ItemStack = ItemStack.EMPTY

	class Type : RecipeType<TransmutingRecipe> {
		companion object {
			val INSTANCE = Type()
		}
	}
}