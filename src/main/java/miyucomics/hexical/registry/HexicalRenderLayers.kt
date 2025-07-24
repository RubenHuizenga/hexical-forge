package miyucomics.hexical.registry

import miyucomics.hexical.HexicalMain
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
import java.io.IOException
import java.util.function.Function
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.DefaultVertexFormat

@Mod.EventBusSubscriber(value = [Dist.CLIENT], modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object HexicalRenderLayers {
   	val PERLIN_NOISE: ResourceLocation = HexicalMain.id("textures/misc/perlin.png")

    fun mediaJar(): RenderType {
        return CustomRenderTypes.MEDIA_JAR.apply(PERLIN_NOISE)
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun registerShaders(event: RegisterShadersEvent) {
        event.registerShader(
            ShaderInstance(
                event.resourceProvider,
                HexicalMain.id("media_jar"),
                DefaultVertexFormat.NEW_ENTITY                
            )
        ) { shaderInstance ->
            CustomRenderTypes.mediaJarShader = shaderInstance
        }
    }

    private class CustomRenderTypes private constructor() : RenderType(
        "", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 0, false, false, {}, {}
    ) {
        companion object {
            internal var mediaJarShader: ShaderInstance? = null
            private val MEDIA_JAR_SHADER_STATE = RenderStateShard.ShaderStateShard { mediaJarShader }
            val MEDIA_JAR: Function<ResourceLocation, RenderType> = Util.memoize(Companion::createMediaJarType)

            private fun createMediaJarType(texture: ResourceLocation): RenderType {
                val state = RenderType.CompositeState.builder()
                    .setShaderState(MEDIA_JAR_SHADER_STATE)
                    .setTextureState(RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setOverlayState(RenderStateShard.NO_OVERLAY)
                    .createCompositeState(true)

                return create(
                    "${HexicalMain.MOD_ID}_media_jar",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    256,
                    true,
                    false,
                    state
                )
            }
        }
    }
}