package miyucomics.hexical.features.hex_candles

import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity

class HexCandleCakeBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexicalBlocks.HEX_CANDLE_CAKE_BLOCK_ENTITY.get(), pos, state) {
	private var pigment: FrozenPigment = FrozenPigment.DEFAULT.get()

	fun getPigment() = this.pigment
	fun setPigment(pigment: FrozenPigment) {
		this.pigment = pigment
		setChanged()
	}

	override fun saveAdditional(nbt: CompoundTag) {
		nbt.put("pigment", pigment.serializeToNBT())
	}

	override fun load(nbt: CompoundTag) {
		pigment = FrozenPigment.fromNBT(nbt.getCompound("pigment"))
	}

	override fun getUpdateTag(): CompoundTag = saveWithoutMetadata()
	override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)
}