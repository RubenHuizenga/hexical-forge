package miyucomics.hexical.utils

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand

object CharmedItemUtilities {
	@JvmStatic
	fun getUseableCharmedItems(player: Player): List<Pair<InteractionHand, ItemStack>> {
		val options = mutableListOf<Pair<InteractionHand, ItemStack>>()
		if (isStackCharmed(player.getItemInHand(InteractionHand.MAIN_HAND))) options.add(Pair(InteractionHand.MAIN_HAND, player.getItemInHand(InteractionHand.MAIN_HAND)))
		if (isStackCharmed(player.getItemInHand(InteractionHand.OFF_HAND))) options.add(Pair(InteractionHand.OFF_HAND, player.getItemInHand(InteractionHand.OFF_HAND)))
		return options
	}

	@JvmStatic
	fun isStackCharmed(stack: ItemStack): Boolean {
		return stack.hasTag() && stack.tag!!.contains("charmed")
	}

	@JvmStatic
	fun removeCharm(stack: ItemStack) {
		stack.tag!!.remove("charmed")
	}

	@JvmStatic
	fun shouldIntercept(stack: ItemStack, button: Int, sneaking: Boolean): Boolean {
		val charmed = stack.tag!!.getCompound("charmed")
		val key = when (button) {
			0 -> if (sneaking) "left_sneak" else "left"
			1 -> if (sneaking) "right_sneak" else "right"
			else -> return true
		}
		return charmed.getBoolean(key)
	}

	@JvmStatic
	fun getHex(stack: ItemStack, world: ServerLevel): List<Iota> {
		return (IotaType.deserialize(stack.tag!!.getCompound("charmed").getCompound("instructions"), world) as ListIota).list.toList()
	}

	@JvmStatic
	fun getMedia(stack: ItemStack): Long {
		return stack.tag!!.getCompound("charmed").getLong("media")
	}

	@JvmStatic
	fun getMaxMedia(stack: ItemStack): Long {
		return stack.tag!!.getCompound("charmed").getLong("max_media")
	}

	@JvmStatic
	fun deductMedia(stack: ItemStack, cost: Long) {
		val oldMedia = getMedia(stack)
		if (oldMedia <= cost)
			removeCharm(stack)
		else
			stack.tag!!.getCompound("charmed").putLong("media", oldMedia - cost)
	}
}