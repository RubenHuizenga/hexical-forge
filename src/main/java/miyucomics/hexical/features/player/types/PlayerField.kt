package miyucomics.hexical.features.player.types

import net.minecraft.world.entity.player.Player
import net.minecraft.nbt.CompoundTag

interface PlayerField {
	fun readNbt(compound: CompoundTag) {}
	fun writeNbt(compound: CompoundTag) {}
	fun handleRespawn(new: Player, old: Player) {}
}