package miyucomics.hexical.features.scarabs

import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation

object ScarabWingRenderer : InitHook() {
	override fun init() {
		ItemProperties.register(HexicalItems.SCARAB_BEETLE_ITEM.get(), ResourceLocation("active")) { stack, _, _, _ ->
			if (stack.tag?.getBoolean("active") == true)
				1.0f
			else
				0.0f
		}
	}
}