package miyucomics.hexical.items

import miyucomics.hexical.registry.HexicalItems
import net.minecraft.client.renderer.item.CompassItemPropertyFunction
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.world.entity.Entity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import com.ibm.icu.impl.number.Properties

class ConjuredCompassItem : Item(Properties().stacksTo(16).food(FoodProperties.Builder().nutrition(2).alwaysEat().fast().build())) {
	override fun getUseDuration(stack: ItemStack) = 40

	companion object {
		fun registerModelPredicate() {
			ItemProperties.register(HexicalItems.CONJURED_COMPASS_ITEM.get(), ResourceLocation("angle"), CompassItemPropertyFunction(
				CompassItemPropertyFunction.CompassTarget { world: ClientLevel, stack: ItemStack, player: Entity ->
					val nbt = stack.tag ?: return@CompassTarget null
					if (nbt.getString("dimension") != world.dimensionTypeId().location().toString())
						return@CompassTarget null
					return@CompassTarget GlobalPos.of(
						player.level().dimension(),
						BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"))
					)
				}
			))
		}
	}
}