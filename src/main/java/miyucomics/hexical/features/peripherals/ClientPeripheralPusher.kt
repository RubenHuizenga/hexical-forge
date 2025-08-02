package miyucomics.hexical.features.peripherals

import miyucomics.hexical.inits.HexicalKeybinds
import miyucomics.hexical.misc.HexicalNetworking
import miyucomics.hexical.misc.InitHook
import net.minecraftforge.event.TickEvent
import net.minecraftforge.client.event.InputEvent;
import net.minecraft.client.Minecraft
import net.minecraft.client.player.Input
import net.minecraftforge.common.MinecraftForge

object ClientPeripheralPusher : InitHook() {
	private var previousState = mutableMapOf<String, Boolean>()

	override fun init() {
		MinecraftForge.EVENT_BUS.addListener(::initTickEvent)
		MinecraftForge.EVENT_BUS.addListener(::initMouseCallback)
	}

	fun initTickEvent(event: TickEvent.ClientTickEvent) {
		if (event.phase == TickEvent.Phase.END) {
			val client = Minecraft.getInstance()
			if (client.player == null)
				return

			for (key in listOf(client.options.keyUp, client.options.keyLeft, client.options.keyRight, client.options.keyDown, client.options.keyJump, client.options.keyShift, client.options.keyUse, client.options.keyAttack, HexicalKeybinds.TELEPATHY_KEYBIND, HexicalKeybinds.EVOKE_KEYBIND)) {
				if (previousState.keys.contains(key.name)) {
					if (previousState[key.name] == true && !key.isDown) {
						HexicalNetworking.sendToServer(ServerPeripheralReceiver.KeyPressPacket(key.name, false))
					} else if (previousState[key.name] == false && key.isDown) {
						HexicalNetworking.sendToServer(ServerPeripheralReceiver.KeyPressPacket(key.name, true))
					}
				}

				previousState[key.name] = key.isDown
			}
		}
	}

	fun initMouseCallback(event: InputEvent.MouseScrollingEvent) {
		if (HexicalKeybinds.TELEPATHY_KEYBIND.isDown) {
			HexicalNetworking.sendToServer(ServerPeripheralReceiver.ScrollPacket(event.scrollDelta.toInt()))
			event.isCanceled = true
		}
		event.isCanceled = false
	}
}