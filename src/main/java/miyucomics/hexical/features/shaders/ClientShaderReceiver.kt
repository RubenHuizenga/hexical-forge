package miyucomics.hexical.features.shaders

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.common.MinecraftForge
import miyucomics.hexical.misc.InitHook

object ClientShaderReceiver : InitHook() {
    override fun init() {
        MinecraftForge.EVENT_BUS.register(::initPlayerLoggedOut)
    }
    
    fun initPlayerLoggedOut(event: PlayerEvent.PlayerLoggedOutEvent) {
        ShaderRenderer.setEffect(null)
    }
}