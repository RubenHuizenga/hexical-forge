package miyucomics.hexical.blocks

import at.petrak.hexcasting.api.block.HexBlockEntity
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.getPositiveIntUnder
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.registry.HexicalBlocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.nbt.CompoundTag
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity

class MageBlockEntity(pos: BlockPos, state: BlockState) : HexBlockEntity(HexicalBlocks.MAGE_BLOCK_ENTITY.get(), pos, state) {
	var properties: MutableMap<String, Boolean> = mutableMapOf(
		"bouncy" to false,
		"energized" to false,
		"ephemeral" to false,
		"invisible" to false,
		"replaceable" to false,
		"volatile" to false
	)
	var redstone: Int = 0
	var lifespan: Int = 0

	override fun saveModData(tag: CompoundTag) {
		properties.forEach { (key, value) -> tag.putBoolean(key, value) }
		tag.putInt("lifespan", this.lifespan)
		tag.putInt("redstone", this.redstone)
	}

	override fun loadModData(tag: CompoundTag) {
		properties.keys.forEach { key -> properties[key] = tag.getBoolean(key) }
		this.lifespan = tag.getInt("lifespan")
		this.redstone = tag.getInt("redstone")
	}

	fun setProperty(property: String, args: List<Iota>) {
		if (property == "energized")
			this.redstone = args.getPositiveIntUnder(0, 16, args.size)
		if (property == "ephemeral")
			this.lifespan = args.getPositiveInt(0, args.size)
		properties[property] = !properties[property]!!
		sync()
	}
}