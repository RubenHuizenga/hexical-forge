package miyucomics.hexical.registry

import miyucomics.hexical.HexicalMain
import net.minecraft.client.Minecraft
import net.minecraft.client.KeyMapping
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.event.TickEvent
import org.lwjgl.glfw.GLFW
import io.netty.buffer.Unpooled
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import net.minecraft.client.player.LocalPlayer

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object HexicalKeybinds {
	@JvmField
	val OPEN_HEXBOOK = KeyMapping("key.hexical.open_hexbook", GLFW.GLFW_KEY_N, "key.categories.hexical")
	val TELEPATHY_KEYBIND = KeyMapping("key.hexical.telepathy", GLFW.GLFW_KEY_G, "key.categories.hexical")
	private val EVOKE_KEYBIND = KeyMapping("key.hexical.evoke", GLFW.GLFW_KEY_R, "key.categories.hexical")
	private var states = mutableMapOf<String, Boolean>()

	@SubscribeEvent
	fun registerBindings(event: RegisterKeyMappingsEvent) {
		event.register(OPEN_HEXBOOK)
		event.register(EVOKE_KEYBIND)
		event.register(TELEPATHY_KEYBIND)
	}
	
	@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
	object HexicalClientKeybinds {
		@SubscribeEvent
		fun onClientTick(event: TickEvent.ClientTickEvent) {
			if (event.phase == TickEvent.Phase.END) {
				val client = Minecraft.getInstance()

				if (client.player == null)
					return

				if (states.keys.contains(EVOKE_KEYBIND.name)) {
					val uuid = (client.player as LocalPlayer).uuid
					if (states[EVOKE_KEYBIND.name] == true && !EVOKE_KEYBIND.isDown()) {
						val packet = HexicalNetworking.EvokeStatePacket(uuid, false)
						HexicalNetworking.sendToServer(packet)
					} else if (states[EVOKE_KEYBIND.name] == false && EVOKE_KEYBIND.isDown()) {
						val packet = HexicalNetworking.EvokeStatePacket(uuid, true)
						HexicalNetworking.sendToServer(packet)
					}
				}

				for (key in listOf(client.options.keyUp, client.options.keyLeft, client.options.keyRight, client.options.keyDown, client.options.keyJump, client.options.keyShift, client.options.keyUse, client.options.keyAttack, TELEPATHY_KEYBIND, EVOKE_KEYBIND)) {
					if (states.keys.contains(key.name)) {
						if (states[key.name] == true && !key.isDown()) {
							val packet = HexicalNetworking.KeyPressPacket(key.name, false)
							HexicalNetworking.sendToServer(packet)
						} else if (states[key.name] == false && key.isDown()) {
							val packet = HexicalNetworking.KeyPressPacket(key.name, true)
							HexicalNetworking.sendToServer(packet)
						}
					}
					states[key.name] = key.isDown()
				}
			}
		}
	}
}