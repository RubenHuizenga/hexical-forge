package miyucomics.hexical.features.transmuting

import at.petrak.hexcasting.api.utils.isMediaItem
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.features.media_jar.MediaJarItem
import miyucomics.hexical.features.transmuting.TransmutingRecipe.Type
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.misc.InitHook
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.registries.ForgeRegistries
import net.minecraft.core.Registry
import net.minecraft.world.level.Level
import kotlin.math.min
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.common.MinecraftForge
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object TransmutingHelper : InitHook() {
	private val RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, HexicalMain.MOD_ID)

	val TRANSMUTING_RECIPE: RegistryObject<RecipeType<TransmutingRecipe>> = 
    	RECIPE_TYPES.register("transmuting") { TransmutingRecipe.Type.INSTANCE }

	override fun init() {
		RECIPE_TYPES.register(MOD_BUS)
		
		MOD_BUS.addListener(::registerSerializers)
	}
	
	
	private fun registerSerializers(event: RegisterEvent) {
		if (event.registryKey == ForgeRegistries.RECIPE_SERIALIZERS.registryKey) {
			event.register(ForgeRegistries.RECIPE_SERIALIZERS.registryKey, HexicalMain.id("transmuting")) {
				TransmutingSerializer.INSTANCE
			}
		}
	}

	fun transmuteItem(world: Level, stack: ItemStack, media: Long, insertMedia: (Long) -> Long, withdrawMedia: (Long) -> Boolean): TransmutationResult {
		if (stack.`is`(HexItems.BATTERY)) {
			val mediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack)!!
			val given = min(mediaHolder.maxMedia - mediaHolder.media, media)
			mediaHolder.insertMedia(given, false)
			withdrawMedia(given)
			return TransmutationResult.RefilledHolder
		}

		if (stack.`is`(HexicalBlocks.MEDIA_JAR_ITEM.get()) && stack.hasTag() && stack.tag!!.contains("BlockEntityTag")) {
			val jarData = stack.tag!!.getCompound("BlockEntityTag")
			val given = min(MediaJarBlock.MAX_CAPACITY - MediaJarItem.getMedia(jarData), media)
			MediaJarItem.insertMedia(jarData, given)
			withdrawMedia(given)
			return TransmutationResult.RefilledHolder
		}

		if (isMediaItem(stack) && media < MediaJarBlock.MAX_CAPACITY) {
			val mediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack)!!
			val consumed = insertMedia(mediaHolder.media)
			mediaHolder.withdrawMedia(consumed, false)
			return TransmutationResult.AbsorbedMedia
		}

		val recipe = getRecipe(stack, world)
		if (recipe != null && media >= recipe.cost) {
			stack.shrink(1)
			withdrawMedia(recipe.cost)
			return TransmutationResult.TransmutedItems(recipe.output.map { it.copy() })
		}

		return TransmutationResult.Pass
	}

	private fun getRecipe(input: ItemStack, world: Level): TransmutingRecipe? {
		world.recipeManager.getAllRecipesFor(TRANSMUTING_RECIPE.get()).forEach { recipe ->
			if (recipe.matches(SimpleContainer(input), world))
				return recipe
		}
		return null
	}
}

sealed class TransmutationResult {
	object AbsorbedMedia : TransmutationResult()
	object Pass : TransmutationResult()
	object RefilledHolder : TransmutationResult()
	data class TransmutedItems(val output: List<ItemStack>) : TransmutationResult()
}
