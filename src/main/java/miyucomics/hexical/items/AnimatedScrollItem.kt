package miyucomics.hexical.items

import at.petrak.hexcasting.api.casting.iota.*
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.utils.*
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexical.client.AnimatedPatternTooltip
import miyucomics.hexical.client.ClientStorage
import miyucomics.hexical.entities.AnimatedScrollEntity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag
import net.minecraft.sounds.SoundSource
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import java.util.*

class AnimatedScrollItem(private val size: Int) : Item(Properties()), IotaHolderItem {
	private fun canPlaceOn(player: Player, side: Direction, stack: ItemStack, pos: BlockPos) = !side.axis.isVertical && player.mayUseItemAt(pos, side, stack)

	override fun useOn(context: UseOnContext): InteractionResult {
		val direction = context.clickedFace
		val position = context.clickedPos.relative(direction)
		val player = context.player
		val stack = context.itemInHand
		val world = context.level

		if (!world.isClientSide && world.getBlockState(context.clickedPos).`is`(HexBlocks.AKASHIC_BOOKSHELF)) {
			val key = (world.getBlockEntity(context.clickedPos) as BlockEntityAkashicBookshelf).pattern
			if (key != null) {
				player!!.swing(context.hand)
				world.playSound(null, context.clickedPos, HexSounds.SCROLL_SCRIBBLE, SoundSource.BLOCKS, 1f, 1f)
				writeDatum(stack, PatternIota(key))
				return InteractionResult.SUCCESS
			}
		}

		if (player != null && !canPlaceOn(player, direction, stack, position))
			return InteractionResult.FAIL

		val patterns = if (stack.containsTag("patterns"))
			stack.getList("patterns", Tag.TAG_COMPOUND.toInt())!!.map { it.asCompound }
		else
			listOf()

		val scrollStack = stack.copy()
		scrollStack.count = 1
		val scroll = AnimatedScrollEntity(world, position, direction, size, patterns, scrollStack)

		scroll.setState(stack.orCreateTag.getInt("state"))
		if (stack.orCreateTag.getBoolean("glow"))
			scroll.toggleGlow()
		if (stack.orCreateTag.hasInt("color"))
			scroll.setColor(stack.orCreateTag.getInt("color"))

		if (scroll.survives()) {
			if (!world.isClientSide) {
				scroll.playPlacementSound()
				world.gameEvent(player, GameEvent.ENTITY_PLACE, scroll.pos)
				world.addFreshEntity(scroll)
			}
			stack.shrink(1)
			return InteractionResult.sidedSuccess(world.isClientSide)
		}

		return InteractionResult.CONSUME
	}

	override fun appendHoverText(stack: ItemStack, world: Level?, tooltip: MutableList<Component>, context: TooltipFlag) {
		if (stack.getBoolean("glow"))
			tooltip.add(Component.translatable("tooltip.hexical.scroll_glow").withStyle(ChatFormatting.GOLD))
		super.appendHoverText(stack, world, tooltip, context)
	}

	override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
		val patterns = stack.getList("patterns", Tag.TAG_COMPOUND.toInt())
		if (patterns != null && patterns.isNotEmpty()) {
			val pattern = HexPattern.fromNBT(patterns[(ClientStorage.ticks / 20) % patterns.size].asCompound)
			return Optional.of(AnimatedPatternTooltip(if (stack.containsTag("color")) stack.orCreateTag.getInt("color") else 0xff_000000.toInt(), pattern, stack.getInt("state")))
		}
		return Optional.empty()
	}

	override fun readIotaTag(stack: ItemStack): CompoundTag {
		val patterns = stack.getList("patterns", Tag.TAG_COMPOUND.toInt())
		if (patterns == null)
			return IotaType.serialize(NullIota())
		return IotaType.serialize(ListIota(patterns.map { PatternIota(HexPattern.fromNBT(it.asCompound)) }))
	}

	override fun writeable(stack: ItemStack) = true

	override fun canWrite(stack: ItemStack, iota: Iota?): Boolean {
		if (iota == null)
			return stack.containsTag("patterns")
		if (iota is PatternIota)
			return true
		if (iota !is ListIota)
			return false

		iota.list.forEach {
			if (it.type != HexIotaTypes.PATTERN)
				return false
		}
		return true
	}

	override fun writeDatum(stack: ItemStack, iota: Iota?) {
		if (iota == null) {
			stack.orCreateTag.remove("patterns")
			return
		}

		val list = ListTag()
		when (iota) {
			is PatternIota -> list.add(iota.pattern.serializeToNBT())
			is ListIota -> iota.list.forEach { list.add((it as PatternIota).pattern.serializeToNBT()) }
		}
		stack.orCreateTag.putList("patterns", list)
	}
}