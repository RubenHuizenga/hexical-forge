package miyucomics.hexical.datagen.generators

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import miyucomics.hexical.datagen.Transmutations
import miyucomics.hexical.inits.HexicalItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.data.DataOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class HexicalPatchouliGenerator(val output: FabricDataOutput) : DataProvider {
	override fun getName(): String = "Hexical Patchouli Pages"
	override fun run(writer: DataWriter): CompletableFuture<*> = CompletableFuture.allOf(
		generateCurioPages(writer),
		generateMediaJarPages(writer)
	)

	private fun generateCurioPages(writer: DataWriter): CompletableFuture<*> {
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

		val path = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "patchouli_books/thehexbook/en_us/entries/items")
		return DataProvider.writeToPath(writer, finalJson, path.resolve(Identifier("hexcasting", "curios"), "json"))
	}

	private fun generateMediaJarPages(writer: DataWriter): CompletableFuture<*> {
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

		val path = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "patchouli_books/thehexbook/en_us/entries/items")
		return DataProvider.writeToPath(writer, finalJson, path.resolve(Identifier("hexcasting", "media_jar"), "json"))
	}
}