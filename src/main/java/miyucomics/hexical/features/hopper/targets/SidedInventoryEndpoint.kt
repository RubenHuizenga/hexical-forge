package miyucomics.hexical.features.hopper.targets

import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction

class SidedInventoryEndpoint(private val inventory: WorldlyContainer, private val direction: Direction) : HopperSource, HopperDestination {
	override fun getItems(): List<ItemStack> {
		val slots = inventory.getSlotsForFace(direction)
		return slots.map { inventory.getItem(it).copy() }.filterNot { it.isEmpty }
	}

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		var remaining = amount
		val slots = inventory.getSlotsForFace(direction)

		for (slot in slots) {
			val existing = inventory.getItem(slot)

			if (!ItemStack.isSameItem(existing, stack)) continue
			if (!inventory.canTakeItemThroughFace(slot, stack, direction)) continue

			val toTake = remaining.coerceAtMost(existing.count)
			existing.shrink(toTake)
			remaining -= toTake

			if (remaining <= 0) return true
		}

		return false
	}

	override fun simulateDeposit(stack: ItemStack): Int {
		var remaining = stack.count
		val slots = inventory.getSlotsForFace(direction)

		for (slot in slots) {
			if (!inventory.canPlaceItemThroughFace(slot, stack, direction)) continue

			val existing = inventory.getItem(slot)

			if (existing.isEmpty) {
				val toInsert = remaining.coerceAtMost(stack.maxStackSize)
				remaining -= toInsert
			} else if (ItemStack.isSameItemSameTags(existing, stack)) {
				val space = existing.maxStackSize - existing.count
				val toInsert = remaining.coerceAtMost(space)
				remaining -= toInsert
			}

			if (remaining <= 0) break
		}

		return stack.count - remaining
	}

	override fun deposit(stack: ItemStack): ItemStack {
		val working = stack.copy()
		val slots = inventory.getSlotsForFace(direction)

		for (slot in slots) {
			if (!inventory.canPlaceItemThroughFace(slot, working, direction)) continue

			val existing = inventory.getItem(slot)

			if (existing.isEmpty) {
				val placed = working.copy()
				val toPlace = working.count.coerceAtMost(placed.maxStackSize)
				placed.count = toPlace
				inventory.setItem(slot, placed)
				working.shrink(toPlace)
			} else if (ItemStack.isSameItemSameTags(existing, working)) {
				val space = existing.maxStackSize - existing.count
				val toAdd = working.count.coerceAtMost(space)
				existing.grow(toAdd)
				working.shrink(toAdd)
			}

			if (working.isEmpty) break
		}

		return if (working.isEmpty) ItemStack.EMPTY else working
	}
}