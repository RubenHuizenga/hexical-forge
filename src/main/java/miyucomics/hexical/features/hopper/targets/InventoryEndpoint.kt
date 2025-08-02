package miyucomics.hexical.features.hopper.targets

import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack

class InventoryEndpoint(val inventory: Container) : HopperSource, HopperDestination {
	override fun getItems(): List<ItemStack> {
		return (0 until inventory.containerSize).map { inventory.getItem(it).copy() }.filterNot { it.isEmpty }
	}

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		var remaining = amount

		for (i in 0 until inventory.containerSize) {
			val existing = inventory.getItem(i)
			if (!ItemStack.isSameItem(existing, stack)) continue
			if (existing.isEmpty) continue
			val toTake = remaining.coerceAtMost(existing.count)
			remaining -= inventory.removeItem(i, toTake).count
			if (remaining <= 0) return true
		}

		return false
	}

	override fun deposit(stack: ItemStack): ItemStack {
		val remaining = stack.copy()

		// First, try to merge into existing stacks
		for (i in 0 until inventory.containerSize) {
			if (!inventory.canPlaceItem(i, remaining)) continue
			val existing = inventory.getItem(i)
			if (!ItemStack.isSameItemSameTags(existing, remaining)) continue

			val canAdd = existing.getMaxStackSize() - existing.count
			val toAdd = remaining.count.coerceAtMost(canAdd)
			if (toAdd > 0) {
				existing.grow(toAdd)
				remaining.shrink(toAdd)
				if (remaining.isEmpty) return ItemStack.EMPTY
			}
		}

		for (i in 0 until inventory.containerSize) {
			if (!inventory.canPlaceItem(i, remaining)) continue
			val existing = inventory.getItem(i)
			if (!existing.isEmpty) continue

			val toPlace = remaining.copy()
			val placedCount = toPlace.count.coerceAtMost(toPlace.getMaxStackSize())
			toPlace.count = placedCount
			inventory.setItem(i, toPlace)
			remaining.shrink(placedCount)

			if (remaining.isEmpty) return ItemStack.EMPTY
		}

		return remaining
	}

	override fun simulateDeposit(stack: ItemStack): Int {
		var remaining = stack.count
		val maxStackSize = stack.maxStackSize

		for (i in 0 until inventory.containerSize) {
			val existing = inventory.getItem(i)
			if (!inventory.canPlaceItem(i, stack)) continue
			if (ItemStack.isSameItemSameTags(existing, stack)) {
				val space = existing.maxStackSize - existing.count
				val toInsert = remaining.coerceAtMost(space)
				remaining -= toInsert
				if (remaining <= 0) return stack.count
			}
		}

		for (i in 0 until inventory.containerSize) {
			val existing = inventory.getItem(i)
			if (!inventory.canPlaceItem(i, stack)) continue
			if (!existing.isEmpty) continue

			val toInsert = remaining.coerceAtMost(maxStackSize)
			remaining -= toInsert
			if (remaining <= 0) return stack.count
		}

		return stack.count - remaining
	}
}