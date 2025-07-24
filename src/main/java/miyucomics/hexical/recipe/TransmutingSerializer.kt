package miyucomics.hexical.recipe

import com.google.gson.*
import com.mojang.serialization.JsonOps
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraftforge.registries.ForgeRegistries
import net.minecraft.resources.ResourceLocation

class TransmutingSerializer : RecipeSerializer<TransmutingRecipe> {
	override fun fromJson(recipeId: ResourceLocation, json: JsonObject): TransmutingRecipe {
		val recipeJson: TransmutingFormat = Gson().fromJson(json, TransmutingFormat::class.java)
		if (recipeJson.input == null)
			throw JsonSyntaxException("Input is missing in recipe $recipeId")
		if (recipeJson.output == null)
			throw JsonSyntaxException("Output is missing in recipe $recipeId")

		val outputs = when (val output = recipeJson.output!!) {
			is JsonArray -> output.map { deriveSingleItem(it, recipeId) }
			else -> listOf(deriveSingleItem(output, recipeId))
		}

		return TransmutingRecipe(recipeId, Ingredient.fromJson(recipeJson.input), recipeJson.cost, outputs)
	}

	override fun toNetwork(buf: FriendlyByteBuf, recipe: TransmutingRecipe) {
		recipe.input.toNetwork(buf)
		buf.writeLong(recipe.cost)
		buf.writeInt(recipe.output.size)
		for (item in recipe.output)
			buf.writeItem(item)
	}

	override fun fromNetwork(id: ResourceLocation, buf: FriendlyByteBuf): TransmutingRecipe {
		val input = Ingredient.fromNetwork(buf)
		val mediaCost = buf.readLong()

		val outputs = mutableListOf<ItemStack>()
		val length = buf.readInt()
		for (i in 0 until length)
			outputs.add(buf.readItem())

		return TransmutingRecipe(id, input, mediaCost, outputs)
	}

	companion object {
		val INSTANCE: TransmutingSerializer = TransmutingSerializer()

		fun deriveSingleItem(thing: JsonElement, recipeId: ResourceLocation): ItemStack {
			return when (thing) {
				is JsonObject -> deriveComplexItem(thing, recipeId)
				is JsonPrimitive -> ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation(thing.asString)) ?: throw JsonSyntaxException("No such item $thing"))
				else -> throw IllegalStateException("$thing is not a valid single item stack format.")
			}
		}

		private fun deriveComplexItem(output: JsonObject, recipeId: ResourceLocation): ItemStack {
			var outputCount = 1
			var outputNBT: Tag? = null

			val outputItemID: String = output.get("item").asString
			val outputItem = ForgeRegistries.ITEMS.getValue(ResourceLocation(outputItemID)) ?: throw JsonSyntaxException("No such item $outputItemID")

			if (output.has("count"))
				outputCount = output.get("count").asInt
			if (output.has("nbt"))
				outputNBT = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, output.get("nbt"))

			val outputStack = ItemStack(outputItem, outputCount)
			if (outputNBT != null) {
				if (outputNBT is CompoundTag)
					outputStack.tag = outputNBT
				else
					throw IllegalStateException("Weird NBT: $outputItemID in recipe $recipeId")
			}

			return outputStack
		}
	}
}

class TransmutingFormat {
	var input: JsonObject? = null
	var output: JsonElement? = null
	var cost: Long = 0
}