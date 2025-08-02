package miyucomics.hexical.features.curios.curios

import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.item.CompassItemPropertyFunction
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos

object CompassCurioModelHook : InitHook() {
	override fun init() {
		ItemProperties.register(
			HexicalItems.CURIO_COMPASS.get(),
			ResourceLocation("angle"),
			CompassItemPropertyFunction(CompassItemPropertyFunction.CompassTarget { world: ClientLevel, stack: ItemStack, player: Entity ->
				if (!stack.hasTag() || !stack.tag?.contains("needle")!!)
					return@CompassTarget null
				val needle = stack.tag!!.getIntArray("needle")
				return@CompassTarget GlobalPos.of(player.level().dimension(), BlockPos(needle[0], needle[1], needle[2]))
			})
		)
	}
}