package miyucomics.hexical.recipe

import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import vazkii.patchouli.api.IComponentProcessor
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.api.IVariableProvider
import com.mojang.authlib.minecraft.client.MinecraftClient
import miyucomics.hexical.HexicalMain

class TransmutingPatchouli : IComponentProcessor {
	lateinit var recipe: TransmutingRecipe

	override fun setup(world: Level, vars: IVariableProvider) {
		val id = ResourceLocation(vars["recipe"].asString())
		val recman = Minecraft.getInstance().level!!.recipeManager
		val transmutingRecipes = recman.getAllRecipesFor(TransmutingRecipe.Type.INSTANCE)
		for (recipe in transmutingRecipes) {
			if (recipe.getId() == id) {
				this.recipe = recipe
				break
			}
		}
	}

	override fun process(world: Level, key: String): IVariable? {
		if (key.length > 6 && key.substring(0, 6) == "output") {
			val index = Integer.parseInt(key.substring(6))
			if (index < recipe.output.size)
				return IVariable.from(recipe.output[index])
			return IVariable.from(ItemStack.EMPTY)
		}

		return when (key) {
			"input" -> IVariable.from(recipe.input)
			"cost" -> IVariable.from(costText(recipe.cost).setStyle(Style.EMPTY.withColor(ItemMediaHolder.HEX_COLOR)))
			else -> null
		}
	}
}

fun costText(media: Long): MutableComponent {
	val loss = media.toFloat() / 10000f
	if (loss > 0f)
		return Component.translatable("hexical.recipe.transmute.media_cost", loss)
	if (loss < 0f)
		return Component.translatable("hexical.recipe.transmute.media_yield", -loss)
	return Component.translatable("hexical.recipe.transmute.media_free")
}