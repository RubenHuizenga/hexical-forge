package miyucomics.hexical.features.player

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.common.MinecraftForge
import miyucomics.hexical.misc.InitHook

object RespawnPersistHook : InitHook() {
	// I think this is the Forge equivalent of Fabrics AFTER_RESPAWN
	override fun init() {
		MinecraftForge.EVENT_BUS.register(::initPlayerAfterRespawn)
	}

	fun initPlayerAfterRespawn(event: PlayerEvent.Clone) {
		if (event.isWasDeath)
			event.getEntity().getHexicalPlayerManager().handleRespawn(event.getEntity(), event.getOriginal())
	}
}