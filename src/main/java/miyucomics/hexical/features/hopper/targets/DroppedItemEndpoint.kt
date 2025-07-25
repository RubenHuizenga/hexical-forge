package miyucomics.hexical.features.hopper.targets

import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack

class DroppedItemEndpoint(private val entity: ItemEntity) : HopperSource, HopperDestination {
	override fun getItems(): List<ItemStack> {
		val stack = entity.stack
		return if (stack.isEmpty) emptyList() else listOf(stack.copy())
	}

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		val existing = entity.stack
		if (!ItemStack.areItemsEqual(existing, stack)) return false
		if (existing.count < amount) return false
		existing.decrement(amount)
		if (existing.isEmpty) {
			entity.discard()
		}
		return true
	}

	override fun simulateDeposit(stack: ItemStack): Int {
		val existing = entity.stack
		if (ItemStack.canCombine(existing, stack)) {
			val space = existing.maxCount - existing.count
			return stack.count.coerceAtMost(space)
		}
		return 0
	}

	override fun deposit(stack: ItemStack): ItemStack {
		val existing = entity.stack
		if (ItemStack.canCombine(existing, stack)) {
			val space = existing.maxCount - existing.count
			val toAdd = stack.count.coerceAtMost(space)
			existing.increment(toAdd)

			return if (stack.count > toAdd) {
				stack.copy().apply { decrement(toAdd) }
			} else {
				ItemStack.EMPTY
			}
		}
		return ItemStack.EMPTY
	}
}