package miyucomics.hexical.features.media_jar

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.Util
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceProvider
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RegisterShadersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.common.MinecraftForge
import java.io.IOException
import java.util.function.Function
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object MediaJarShader : InitHook() {
   	val PERLIN_NOISE: ResourceLocation = HexicalMain.id("textures/misc/perlin.png")

    @Volatile
    private var shaderInstance: ShaderInstance? = null
    
    fun getShader(): ShaderInstance = shaderInstance 
        ?: throw IllegalStateException("Shader not loaded yet!")

    fun mediaJar(): RenderType = createMediaJarType(PERLIN_NOISE)

    override fun init() {
        // Done this using event bus subscribtion, even if I don't fully understand that yet
    }
    
    @SubscribeEvent
    fun initRegisterShadersEvent(event: RegisterShadersEvent) {
        try {
            event.registerShader(
                ShaderInstance(
                    event.resourceProvider,
                    HexicalMain.id("media_jar"),
                    DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL                
                )
            ) { loadedShader ->
                shaderInstance = loadedShader
            }
        } catch (e: IOException) {
            HexicalMain.LOGGER.error("Shader loading failed", e)
        }
    }

    private fun createMediaJarType(texture: ResourceLocation): RenderType {
        return RenderType.create(
            "${HexicalMain.MOD_ID}_media_jar",
            DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL,
            VertexFormat.Mode.QUADS,
            512, 
            true,
            false,
            RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.ShaderStateShard { getShader() })
                .setTextureState(RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                .setOverlayState(RenderStateShard.NO_OVERLAY)
                .createCompositeState(true)
        )
    }
}