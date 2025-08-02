package miyucomics.hexical.features.peripherals

import miyucomics.hexical.features.player.types.PlayerTicker
import net.minecraft.world.entity.player.Player

class KeybindTicker : PlayerTicker {
	override fun tick(player: Player) {
		player.serverKeybindActive().keys.filter { player.serverKeybindActive()[it] == true }.forEach { key ->
			player.serverKeybindDuration()[key] = player.serverKeybindDuration()[key]!! + 1
		}
	}
}