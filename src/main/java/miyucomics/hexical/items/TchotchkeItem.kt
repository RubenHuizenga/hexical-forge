package miyucomics.hexical.items

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.level.Level

class TchotchkeItem : ItemPackagedHex(Properties().stacksTo(1)) {
	override fun canDrawMediaFromInventory(stack: ItemStack) = false
	override fun isBarVisible(stack: ItemStack) = false
	override fun canRecharge(stack: ItemStack) = false
	override fun breakAfterDepletion() = true
	override fun cooldown() = 0

	override fun use(world: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (world.isClientSide)
			return InteractionResultHolder.success(player.getItemInHand(usedHand))
		val stack = player.getItemInHand(usedHand)
		if (hasHex(stack) && getMedia(stack) > 0) {
			val charmed = ItemStack(Items.STICK)
			val nbt = charmed.orCreateTag
			val charm = CompoundTag()
			charm.putLong("media", getMedia(stack))
			charm.putLong("max_media", getMedia(stack))
			charm.putCompound("instructions", IotaType.serialize(ListIota(getHex(stack, world as ServerLevel)!!)))
			charm.putBoolean("left", true)
			charm.putBoolean("right", true)
			charm.putBoolean("left_sneak", true)
			charm.putBoolean("right_sneak", true)
			nbt.putCompound("charmed", charm)
			player.setItemInHand(usedHand, charmed)
		}
		return InteractionResultHolder.success(player.getItemInHand(usedHand))
	}
}