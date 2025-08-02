package miyucomics.hexical.misc

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerLevel

object HexSerialization {
	fun serializeHex(hex: List<Iota>) = ListTag().also { hex.forEach { iota -> it.add(IotaType.serialize(iota)) } }
	fun deserializeHex(list: ListTag, world: ServerLevel) = list.map { IotaType.deserialize(it.asCompound, world) }

	fun backwardsCompatibleReadHex(holder: CompoundTag, key: String, world: ServerLevel): List<Iota> {
		val element = holder.get(key)
		if (element is CompoundTag) {
			val elementData = (IotaType.deserialize(element, world) as ListIota).list.toList()
			holder.remove(key)
			holder.putList(key, serializeHex(elementData))
			return elementData
		}
		return deserializeHex(element as ListTag, world)
	}
}