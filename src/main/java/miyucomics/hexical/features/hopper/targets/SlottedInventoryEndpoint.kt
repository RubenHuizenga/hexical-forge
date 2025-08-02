package miyucomics.hexical.features.hopper.targets

import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import miyucomics.hexical.features.hopper.NotEnoughSlotsMishap
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack

class SlottedInventoryEndpoint(private val inventory: Container, private val slot: Int, iota: Iota) : HopperSource, HopperDestination {
	init {
		if (slot >= inventory.containerSize || slot < 0)
			throw NotEnoughSlotsMishap(iota, slot)
	}

	override fun getItems(): List<ItemStack> {
		val stack = inventory.getItem(slot).copy()
		return if (stack.isEmpty) emptyList() else listOf(stack)
	}

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		val existing = inventory.getItem(slot)
		if (!ItemStack.isSameItem(existing, stack)) return false
		if (existing.count < amount) return false

		existing.shrink(amount)
		return true
	}

	override fun deposit(stack: ItemStack): ItemStack {
		val target = inventory.getItem(slot)

		if (!inventory.canPlaceItem(slot, stack)) return stack.copy()

		if (target.isEmpty) {
			// Place only if exactly 1 stack can be deposited (like vanilla hopper logic)
			val toPlace = stack.copy()
			val amount = toPlace.count.coerceAtMost(stack.maxStackSize)
			toPlace.count = amount
			inventory.setItem(slot, toPlace)
			return if (stack.count > amount) stack.copy().apply { shrink(amount) } else ItemStack.EMPTY
		}

		if (!ItemStack.isSameItemSameTags(target, stack)) return stack.copy()

		val canAdd = target.maxStackSize - target.count
		if (canAdd <= 0) return stack.copy()

		val toAdd = stack.count.coerceAtMost(canAdd)
		target.grow(toAdd)

		return if (stack.count > toAdd) stack.copy().apply { shrink(toAdd) } else ItemStack.EMPTY
	}

	override fun simulateDeposit(stack: ItemStack): Int {
		val target = inventory.getItem(slot)

		if (!inventory.canPlaceItem(slot, stack)) return 0

		if (target.isEmpty) {
			return stack.count.coerceAtMost(stack.maxStackSize)
		}

		if (!ItemStack.isSameItemSameTags(target, stack)) return 0

		val space = target.maxStackSize - target.count
		return stack.count.coerceAtMost(space)
	}
}