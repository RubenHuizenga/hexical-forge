package miyucomics.hexical.features.media_jar

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.common.lib.HexItems
import com.mojang.datafixers.util.Pair
import miyucomics.hexical.features.transmuting.TransmutationResult
import miyucomics.hexical.features.transmuting.TransmutingHelper
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.misc.RenderUtils
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.sounds.SoundSource
import net.minecraft.network.chat.Component
import net.minecraft.core.BlockPos
import kotlin.math.max
import kotlin.math.min

class MediaJarBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexicalBlocks.MEDIA_JAR_BLOCK_ENTITY.get(), pos, state), Container {
	private var media: Long = 0
	private var heldStack = ItemStack.EMPTY

	fun scryingLensOverlay(lines: MutableList<Pair<ItemStack, Component>>) {
		lines.add(Pair(ItemStack(HexItems.AMETHYST_DUST), Component.translatable("hexcasting.tooltip.media", RenderUtils.DUST_AMOUNT.format(media.toFloat() / MediaConstants.DUST_UNIT.toFloat()))))
	}

	fun getMedia() = this.media
	private fun setMedia(media: Long) {
		this.media = max(min(media, MediaJarBlock.MAX_CAPACITY), 0)
		setChanged()
		if (!level!!.isClientSide)
			level!!.sendBlockUpdated(worldPosition, blockState, blockState, Block.UPDATE_ALL)
	}
	fun insertMedia(media: Long): Long {
		val currentMedia = this.media
		setMedia(currentMedia + media)
		return this.getMedia() - currentMedia
	}
	fun withdrawMedia(media: Long): Boolean {
		if (getMedia() >= media) {
			setMedia(getMedia() - media)
			return true
		} else {
			setMedia(0)
			return false
		}
	}

	override fun saveAdditional(nbt: CompoundTag) {
		nbt.putLong("media", media)
		nbt.putCompound("heldStack", heldStack.serializeToNBT())
	}

	override fun load(nbt: CompoundTag) {
		this.media = nbt.getLong("media")
		this.heldStack = ItemStack.of(nbt.getCompound("heldStack"))
	}

	override fun getContainerSize() = 1
	override fun stillValid(playerEntity: Player) = false
	override fun getItem(i: Int): ItemStack = if (i == 0) heldStack else ItemStack.EMPTY
	override fun isEmpty() = heldStack.isEmpty

	override fun removeItem(i: Int, amount: Int): ItemStack {
		if (i == 0) {
			setChanged()
			return heldStack.split(amount)
		}
		return ItemStack.EMPTY
	}

	override fun removeItemNoUpdate(i: Int): ItemStack {
		if (i == 0) {
			val originalHeld = heldStack
			heldStack = ItemStack.EMPTY
			setChanged()
			return originalHeld
		}
		return ItemStack.EMPTY
	}

	override fun setItem(i: Int, stack: ItemStack) {
		if (i != 0)
			return
		if (level == null)
			return

		when (val result = TransmutingHelper.transmuteItem(level!!, stack, getMedia(), ::insertMedia, ::withdrawMedia)) {
			is TransmutationResult.AbsorbedMedia -> {
				level!!.playSound(null, worldPosition, HexicalSounds.AMETHYST_MELT.get(), SoundSource.BLOCKS, 1f, 1f)
				heldStack = stack
			}
			is TransmutationResult.TransmutedItems -> {
				val outputs = result.output.toMutableList()
				heldStack = outputs.removeFirst().copy()
				val spawnPosition = worldPosition.below().getCenter()
				outputs.forEach { level!!.addFreshEntity(ItemEntity(level!!, spawnPosition.x, spawnPosition.y, spawnPosition.z, it.copy(), 0.0, 0.0, 0.0)) }
				level!!.playSound(null, worldPosition, HexicalSounds.ITEM_DUNKS.get(), SoundSource.BLOCKS, 1f, 1f)
			}
			is TransmutationResult.RefilledHolder -> {
				level!!.playSound(null, worldPosition, HexicalSounds.ITEM_DUNKS.get(), SoundSource.BLOCKS, 1f, 1f)
				heldStack = stack
			}
			is TransmutationResult.Pass -> {
				heldStack = stack
			}
		}

		setChanged()
	}

	override fun clearContent() {
		heldStack = ItemStack.EMPTY
		setChanged()
	}

	override fun getUpdateTag(): CompoundTag = saveWithoutMetadata()
	override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)
}