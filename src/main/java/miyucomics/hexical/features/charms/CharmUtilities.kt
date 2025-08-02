package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.utils.putCompound
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.network.chat.TextColor
import net.minecraft.world.InteractionHand

object CharmUtilities {
	val CHARMED_COLOR: TextColor = TextColor.fromRgb(0xe83d72)

	@JvmStatic
	fun getUseableCharmedItems(player: Player): List<Pair<InteractionHand, ItemStack>> {
		val options = mutableListOf<Pair<InteractionHand, ItemStack>>()
		if (isStackCharmed(player.getItemInHand(InteractionHand.MAIN_HAND))) options.add(Pair(
			InteractionHand.MAIN_HAND, player.getItemInHand(
				InteractionHand.MAIN_HAND)))
		if (isStackCharmed(player.getItemInHand(InteractionHand.OFF_HAND))) options.add(Pair(
			InteractionHand.OFF_HAND, player.getItemInHand(
				InteractionHand.OFF_HAND)))
		return options
	}

	@JvmStatic
	fun shouldIntercept(stack: ItemStack, button: Int, sneaking: Boolean): Boolean {
		val charmed = getCompound(stack)
		val inputs = if (sneaking) charmed.getIntArray("sneak_inputs") else charmed.getIntArray("normal_inputs")
		return inputs.contains(button)
	}

	fun removeCharm(stack: ItemStack) {
		stack.tag!!.remove("charmed")
		if (stack.tag!!.isEmpty)
			stack.tag = null
		if (stack.item is CurioItem)
			stack.shrink(1)
	}

	fun deductMedia(stack: ItemStack, cost: Long) {
		val oldMedia = getMedia(stack)
		if (oldMedia <= cost)
			removeCharm(stack)
		else
			getCompound(stack).putLong("media", oldMedia - cost)
	}

	fun isStackCharmed(stack: ItemStack) = stack.hasTag() && stack.tag!!.contains("charmed")
	fun getCompound(stack: ItemStack): CompoundTag = stack.tag!!.getCompound("charmed")
	fun getHex(stack: ItemStack, world: ServerLevel) = HexSerialization.backwardsCompatibleReadHex(getCompound(stack), "hex", world)
	fun getMedia(stack: ItemStack) = getCompound(stack).getLong("media")
	fun getMaxMedia(stack: ItemStack) = getCompound(stack).getLong("max_media")

	fun getInternalStorage(stack: ItemStack, world: ServerLevel): Iota {
		val nbt = getCompound(stack)
		if (nbt.contains("storage"))
			return IotaType.deserialize(nbt.getCompound("storage"), world)
		return NullIota()
	}

	fun setInternalStorage(stack: ItemStack, iota: Iota) {
		getCompound(stack).putCompound("storage", IotaType.serialize(iota))
	}
}