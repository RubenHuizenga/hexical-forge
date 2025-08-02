package miyucomics.hexical.features.hopper.targets

import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack

class DroppedItemEndpoint(private val entity: ItemEntity) : HopperSource, HopperDestination {
	override fun getItems(): List<ItemStack> {
		val stack = entity.item
		return if (stack.isEmpty) emptyList() else listOf(stack.copy())
	}

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		val existing = entity.item
		if (!ItemStack.isSameItem(existing, stack)) return false
		if (existing.count < amount) return false
		existing.shrink(amount)
		if (existing.isEmpty) {
			entity.discard()
		}
		return true
	}

	override fun simulateDeposit(stack: ItemStack): Int {
		val existing = entity.item
		if (ItemStack.isSameItemSameTags(existing, stack)) {
			val space = existing.maxStackSize - existing.count
			return stack.count.coerceAtMost(space)
		}
		return 0
	}

	override fun deposit(stack: ItemStack): ItemStack {
		val existing = entity.item
		if (ItemStack.isSameItemSameTags(existing, stack)) {
			val space = existing.maxStackSize - existing.count
			val toAdd = stack.count.coerceAtMost(space)
			existing.grow(toAdd)

			return if (stack.count > toAdd) {
				stack.copy().apply { shrink(toAdd) }
			} else {
				ItemStack.EMPTY
			}
		}
		return ItemStack.EMPTY
	}
}