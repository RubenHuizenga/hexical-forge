package miyucomics.hexical.features.prestidigitation

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import miyucomics.hexical.HexicalMain
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.registries.ForgeRegistries
import java.io.InputStreamReader

object PrestidigitationBlockTransformations {
    private val map: MutableMap<Block, BlockState> = mutableMapOf()

    @Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    object ReloadListenerHandler {
        @SubscribeEvent
        fun onAddReloadListeners(event: AddReloadListenerEvent) {
            event.addListener(object : SimplePreparableReloadListener<Unit>() {
                override fun prepare(manager: ResourceManager, profiler: ProfilerFiller): Unit = Unit

                override fun apply(prepared: Unit, manager: ResourceManager, profiler: ProfilerFiller) {
                    map.clear()
                    manager.listResources("prestidigitation") { it.path.endsWith("block_transformations.json") }.forEach { (path, resource) ->
                        resource.open().use { inputStream ->
                            val json = JsonParser.parseReader(InputStreamReader(inputStream, "UTF-8")) as JsonObject
                            json.entrySet().forEach {
                                val sourceBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation(it.key))
                                val targetBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation(it.value.asString))
                                map[sourceBlock!!] = targetBlock!!.defaultBlockState()
                            }
                        }
                    }
                }
            })
        }
    }

    @Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    object RegistryHandler {
        @SubscribeEvent
        fun onRegister(event: RegisterEvent) {
            if (event.registryKey == PrestidigitationData.DEFERRED_REGISTER.registryKey) {
                event.register(
                    PrestidigitationData.DEFERRED_REGISTER.registryKey,
                    HexicalMain.id("transform_block")
                ) {
                    object : PrestidigitationHandler {
                        override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
                            val state = env.world.getBlockState(position)
                            if (state.block !in map) return false
                            env.world.setBlockAndUpdate(position, map[state.block]!!)
                            return true
                        }
                    }
                }
            }
        }
    }
}