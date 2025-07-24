package miyucomics.hexical.blocks

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.lib.HexItems
import com.mojang.datafixers.util.Pair
import miyucomics.hexical.registry.HexicalBlocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.network.chat.Component
import net.minecraft.core.BlockPos
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

class MediaJarBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexicalBlocks.MEDIA_JAR_BLOCK_ENTITY.get(), pos, state) {
	private var media: Long = 0

	fun scryingLensOverlay(lines: MutableList<Pair<ItemStack, Component>>) {
		lines.add(Pair(ItemStack(HexItems.AMETHYST_DUST), Component.translatable("hexcasting.tooltip.media", format.format(media.toFloat() / MediaConstants.DUST_UNIT.toFloat()))))
	}

	fun getMedia() = this.media
	private fun setMedia(media: Long) {
		this.media = max(min(media, MediaJarBlock.MAX_CAPACITY), 0)
		setChanged()
	}
	fun insertMedia(media: Long): Long {
		val currentMedia = this.media
		setMedia(this.media + media)
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
	}

	override fun load(nbt: CompoundTag) {
		this.media = nbt.getLong("media")
	}

	override fun getUpdateTag(): CompoundTag = saveWithoutMetadata()
	override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

	companion object {
		private var format = DecimalFormat("###,###.##")
	}
}