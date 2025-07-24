package miyucomics.hexical.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import miyucomics.hexical.HexicalMain
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.item.Item
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.server.packs.PackType
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.profiling.ProfilerFiller
import java.io.InputStream
import java.io.InputStreamReader

object DyeData {
	private val flatBlockLookup = HashMap<String, String>()
	private val flatItemLookup = HashMap<String, String>()
	private val blockFamilies = HashMap<String, MutableMap<String, String>>()
	private val itemFamilies = HashMap<String, MutableMap<String, String>>()

    @Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    object ReloadListener {
        @SubscribeEvent
        fun onAddReloadListeners(event: AddReloadListenerEvent) {
            event.addListener(object : SimplePreparableReloadListener<Unit>() {
                override fun prepare(manager: ResourceManager, profiler: ProfilerFiller): Unit = Unit

                override fun apply(prepared: Unit, manager: ResourceManager, profiler: ProfilerFiller) {
                    flatBlockLookup.clear()
                    flatItemLookup.clear()
                    blockFamilies.clear()
                    itemFamilies.clear()

                    manager.listResources("dyes") { loc -> loc.path.endsWith(".json") }.forEach { (id, resource) ->
                        resource.open().use { stream ->
                            loadData(stream)
                        }
                    }
                }
            })
        }
    }

	fun isDyeable(block: Block): Boolean = flatBlockLookup.containsKey(ForgeRegistries.BLOCKS.getKey(block).toString())
	fun isDyeable(item: Item): Boolean = flatItemLookup.containsKey(ForgeRegistries.ITEMS.getKey(item).toString())
	fun getDye(block: Block): String? = flatBlockLookup[ForgeRegistries.BLOCKS.getKey(block).toString()]
	fun getDye(item: Item): String? = flatItemLookup[ForgeRegistries.ITEMS.getKey(item).toString()]

	fun getNewBlock(block: Block, dye: String): BlockState {
		blockFamilies.forEach { (_, family) ->
			if (family.containsValue(ForgeRegistries.BLOCKS.getKey(block).toString()) && family.containsKey(dye))
				return ForgeRegistries.BLOCKS.getValue(ResourceLocation(family[dye]!!))!!.defaultBlockState()
		}
		return block.defaultBlockState()
	}

	fun getNewItem(item: Item, dye: String): Item {
		itemFamilies.forEach { (_, family) ->
			if (family.containsValue(ForgeRegistries.ITEMS.getKey(item).toString()) && family.containsKey(dye))
				return ForgeRegistries.ITEMS.getValue(ResourceLocation(family[dye]!!))!!
		}
		return item
	}

	private fun loadData(stream: InputStream) {
		val json = JsonParser.parseReader(InputStreamReader(stream, "UTF-8")) as JsonObject

		val blocks = json.getAsJsonObject("blocks")
		blocks.keySet().forEach { familyKey ->
			val family = blocks.getAsJsonObject(familyKey)
			family.keySet().forEach { block ->
				flatBlockLookup[block] = family.get(block).asString
				if (!blockFamilies.containsKey(familyKey))
					blockFamilies[familyKey] = mutableMapOf()
				blockFamilies[familyKey]!![family.get(block).asString] = block
			}
		}

		val items = json.getAsJsonObject("items")
		items.keySet().forEach { familyKey ->
			val family = items.getAsJsonObject(familyKey)
			family.keySet().forEach { item ->
				flatItemLookup[item] = family.get(item).asString
				if (!itemFamilies.containsKey(familyKey))
					itemFamilies[familyKey] = mutableMapOf()
				itemFamilies[familyKey]!![family.get(item).asString] = item
			}
		}
	}
}