package miyucomics.hexical.features.scarabs

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.utils.*
import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.Tag
import net.minecraft.sounds.SoundSource
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Rarity
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.level.Level

class ScarabBeetleItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), IotaHolderItem {
	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = user.getItemInHand(hand)
		val nbt = stack.orCreateTag
		nbt.putBoolean("active", !nbt.getBoolean("active"))
		if (world.isClientSide)
			world.playLocalSound(user.x, user.y, user.z, HexicalSounds.SCARAB_CHIRPS.get(), SoundSource.MASTER, 1f, 1f, true)
		return InteractionResultHolder.success(stack)
	}

	override fun appendHoverText(stack: ItemStack, world: Level?, tooltip: MutableList<Component>, context: TooltipFlag) {
		val nbt = stack.tag ?: return
		if (!nbt.contains("hex"))
			return
		tooltip.add("hexical.scarab.hex".asTranslatedComponent(stack.getList("hex", Tag.TAG_COMPOUND.toInt())!!.fold(Component.empty()) { acc, curr -> acc.append(IotaType.getDisplay(curr.asCompound)) }).styledWith(ChatFormatting.GRAY))
		super.appendHoverText(stack, world, tooltip, context)
	}

	override fun readIotaTag(stack: ItemStack) = null
	override fun writeable(stack: ItemStack) = true
	override fun canWrite(stack: ItemStack, iota: Iota?) = iota == null || iota is ListIota
	override fun writeDatum(stack: ItemStack, iota: Iota?) {
		if (iota == null)
			stack.remove("hex")
		else
			stack.putList("hex", HexSerialization.serializeHex((iota as ListIota).list.toList()))
	}
}