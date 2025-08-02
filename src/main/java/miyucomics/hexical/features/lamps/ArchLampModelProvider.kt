package miyucomics.hexical.features.lamps

import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation

object ArchLampModelProvider : InitHook() {
	override fun init() {
		ItemProperties.register(HexicalItems.ARCH_LAMP_ITEM.get(), ResourceLocation("active")) { stack, _, _, _ ->
			if (stack.tag?.getBoolean("active") == true) 1.0f else 0.0f
		}
	}
}