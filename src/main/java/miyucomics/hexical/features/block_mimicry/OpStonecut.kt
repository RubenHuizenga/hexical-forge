package miyucomics.hexical.features.block_mimicry

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadItem
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexpose.iotas.getIdentifier
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.StonecutterRecipe
import net.minecraft.core.RegistryAccess
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistry

object OpStonecut : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val item = args.getItemEntity(0, argc)
		env.assertEntityInRange(item)

		val id = args.getIdentifier(1, argc)
		if (!ForgeRegistries.ITEMS.containsKey(id))
			throw MishapInvalidIota.of(args[1], 0, "item_id")
		val type = ForgeRegistries.ITEMS.getValue(id)

		val recipe = env.world.recipeManager
			.getRecipesFor(RecipeType.STONECUTTING, SimpleContainer(item.item), env.world)
			.firstOrNull { it.getResultItem(RegistryAccess.EMPTY).`is`(type!!) }
			?: throw MishapBadItem.of(item, "target.stonecutting")

		return SpellAction.Result(Spell(recipe, item), MediaConstants.DUST_UNIT / 8, listOf())
	}

	private data class Spell(val recipe: StonecutterRecipe, val item: ItemEntity) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val stack = item.item
			val result = recipe.assemble(SimpleContainer(stack), RegistryAccess.EMPTY)
			result.count *= stack.count
			stack.count = 0
			val resultItem = ItemEntity(env.world, item.x, item.y, item.z, result)
			env.world.addFreshEntity(resultItem)
		}
	}
}