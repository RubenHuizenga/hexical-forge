package miyucomics.hexical.inits

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW

object HexicalKeybinds {
	@JvmField
	val OPEN_HEXBOOK = KeyMapping("key.hexical.open_hexbook", GLFW.GLFW_KEY_N, "key.categories.hexical")
	val TELEPATHY_KEYBIND = KeyMapping("key.hexical.telepathy", GLFW.GLFW_KEY_G, "key.categories.hexical")
	private val EVOKE_KEYBIND = KeyMapping("key.hexical.evoke", GLFW.GLFW_KEY_R, "key.categories.hexical")
	private var states = mutableMapOf<String, Boolean>()

	fun clientInit(event: RegisterKeyMappingsEvent) {
		event.register(OPEN_HEXBOOK)
		event.register(EVOKE_KEYBIND)
		event.register(TELEPATHY_KEYBIND)
	}	
}