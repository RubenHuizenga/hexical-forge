package miyucomics.hexical.features.transmuting

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.NbtOps
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraftforge.registries.ForgeRegistries
import net.minecraft.resources.ResourceLocation

class TransmutingJsonProvider(private val id: ResourceLocation, private val input: Ingredient, private val output: List<ItemStack>, private val cost: Long) : FinishedRecipe {
	override fun serializeRecipeData(json: JsonObject) {
		json.add("input", input.toJson())
		json.addProperty("cost", cost)

		if (output.size == 1) {
			json.add("output", serializeItemStack(output[0]))
		} else {
			val array = JsonArray()
			output.forEach { array.add(serializeItemStack(it)) }
			json.add("output", array)
		}
	}

	override fun getId(): ResourceLocation = id
	override fun getType(): RecipeSerializer<*> = TransmutingSerializer.INSTANCE
	override fun serializeAdvancement(): JsonObject? = null
	override fun getAdvancementId(): ResourceLocation? = null

	private fun serializeItemStack(stack: ItemStack): JsonObject {
		val obj = JsonObject()
		obj.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.item).toString())
		if (stack.count > 1) obj.addProperty("count", stack.count)
		if (stack.hasTag()) obj.add("nbt", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, stack.tag))
		return obj
	}
}