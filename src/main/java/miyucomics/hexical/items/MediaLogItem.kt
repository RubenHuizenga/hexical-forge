package miyucomics.hexical.items

import miyucomics.hexical.data.LedgerData
import miyucomics.hexical.registry.HexicalNetworking
import miyucomics.hexical.screens.LedgerScreen
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.level.Level

class MediaLogItem : Item(Properties().stacksTo(1)) {
	override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		if (world.isClientSide)
			Minecraft.getInstance().setScreen(LedgerScreen())
		else
			HexicalNetworking.sendToPlayer(player as ServerPlayer, HexicalNetworking.LedgerPacket(LedgerData.getLedger(player).toNbt()))
		return InteractionResultHolder.success(stack)
	}

	override fun getDescriptionId() = "item.hexical.media_log." + ((System.currentTimeMillis() / 2000) % 2).toString()
}