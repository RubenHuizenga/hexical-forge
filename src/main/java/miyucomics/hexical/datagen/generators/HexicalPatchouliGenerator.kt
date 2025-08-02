package miyucomics.hexical.datagen.generators

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import miyucomics.hexical.datagen.Transmutations
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.data.PackOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.CachedOutput
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

class HexicalPatchouliGenerator(val output: PackOutput) : DataProvider {
	override fun getName(): String = "Hexical Patchouli Pages"
	override fun run(writer: CachedOutput): CompletableFuture<*> = CompletableFuture.allOf(
		generateCurioPages(writer),
		generateMediaJarPages(writer)
	)

	private fun generateCurioPages(writer: CachedOutput): CompletableFuture<*> {
		val finalJson = JsonObject().apply {
			addProperty("name", "hexical.page.curios.title")
			addProperty("icon", "hexical:curio_clover")
			addProperty("advancement", "hexcasting:root")
			addProperty("category", "hexcasting:items")
			addProperty("sortnum", 10)
			add("pages", JsonArray().apply {
				add(JsonObject().apply {
					addProperty("type", "patchouli:text")
					addProperty("text", "hexical.page.curios.0")
				})
				for (name in HexicalItems.CURIO_NAMES) {
					add(JsonObject().apply {
						addProperty("type", "patchouli:spotlight")
						addProperty("item", "hexical:curio_$name")
						addProperty("text", "hexical.page.curio_$name.summary")
					})
				}
			})
		}

		val path = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "patchouli_books/thehexbook/en_us/entries/items")
		return DataProvider.saveStable(writer, finalJson, path.file(ResourceLocation("hexcasting", "curios"), "json"))
	}

	private fun generateMediaJarPages(writer: CachedOutput): CompletableFuture<*> {
		val finalJson = JsonObject().apply {
			addProperty("name", "hexical.page.media_jar.title")
			addProperty("icon", "hexical:media_jar")
			addProperty("advancement", "hexcasting:root")
			addProperty("category", "hexcasting:items")
			addProperty("sortnum", 4)
			add("pages", JsonArray().apply {
				add(JsonObject().apply {
					addProperty("type", "patchouli:text")
					addProperty("text", "hexical.page.media_jar.0")
				})
				add(JsonObject().apply {
					addProperty("type", "patchouli:crafting")
					addProperty("recipe", "hexical:media_jar")
					addProperty("text", "hexical.page.media_jar.description")
				})
				add(JsonObject().apply {
					addProperty("type", "patchouli:text")
					addProperty("text", "hexical.page.media_jar.1")
				})
				Transmutations.transmutationRecipePages.forEach { add(it) }
			})
		}

		val path = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "patchouli_books/thehexbook/en_us/entries/items")
		return DataProvider.saveStable(writer, finalJson, path.file(ResourceLocation("hexcasting", "media_jar"), "json"))
	}
}