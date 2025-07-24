package miyucomics.hexical.data

import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.api.utils.vecFromNBT
import at.petrak.hexcasting.api.utils.vec2FromNBT
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.Vec3

class ArchLampState {
	var position: Vec3 = Vec3.ZERO
	var rotation: Vec3 = Vec3.ZERO
	var velocity: Vec3 = Vec3.ZERO
	var storage: CompoundTag = CompoundTag()
	var time: Long = 0

	fun toNbt(): CompoundTag {
		val tag = CompoundTag()
		tag.put("position", position.serializeToNBT())
		tag.put("rotation", rotation.serializeToNBT())
		tag.put("velocity", velocity.serializeToNBT())
		tag.putCompound("storage", storage)
		tag.putLong("time", time)
		return tag
	}

	companion object {
		@JvmStatic
		fun createFromNbt(tag: CompoundTag): ArchLampState {
			val state = ArchLampState()
			state.position = vecFromNBT(tag.getLongArray("position"))
			state.rotation = vecFromNBT(tag.getLongArray("rotation"))
			state.velocity = vecFromNBT(tag.getLongArray("velocity"))
			state.storage = tag.getCompound("storage")
			state.time = tag.getLong("time")
			return state
		}
	}
}