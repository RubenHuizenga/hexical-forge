package miyucomics.hexical.features.hopper.targets

import net.minecraft.world.entity.player.Player
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.core.NonNullList

class ArmorStandInventory(private val armorItems: NonNullList<ItemStack>, private val heldItems: NonNullList<ItemStack>) : Container {
	override fun getItem(slot: Int): ItemStack {
		return when (slot) {
			in 0..3 -> armorItems[slot]
			in 4..5 -> heldItems[slot - 4]
			else -> throw IndexOutOfBoundsException("$slot out of bounds for ArmorStandInventory")
		}
	}

	override fun removeItem(slot: Int, amount: Int): ItemStack {
		val stack = getItem(slot)
		return if (stack.isEmpty) ItemStack.EMPTY else stack.split(amount)
	}

	override fun removeItemNoUpdate(slot: Int): ItemStack {
		val result = getItem(slot)
		setItem(slot, ItemStack.EMPTY)
		return result
	}

	override fun setItem(slot: Int, stack: ItemStack) {
		when (slot) {
			in 0..3 -> armorItems[slot] = stack
			in 4..5 -> heldItems[slot - 4] = stack
			else -> throw IndexOutOfBoundsException("$slot out of bounds for ArmorStandInventory")
		}
	}

	override fun clearContent() {
		for (i in 0 until 4) armorItems[i] = ItemStack.EMPTY
		for (i in 0 until 2) heldItems[i] = ItemStack.EMPTY
	}

	override fun getContainerSize() = 6
	override fun setChanged() {}
	override fun stillValid(player: Player?): Boolean = true
	override fun isEmpty() = armorItems.all { it.isEmpty } && heldItems.all { it.isEmpty }
}