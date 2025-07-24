package miyucomics.hexical.data

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.putList
import miyucomics.hexical.utils.RingBuffer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import io.netty.buffer.Unpooled

class LedgerInstance {
	var patterns: RingBuffer<HexPattern> = RingBuffer(32)
	var stack: RingBuffer<Component> = RingBuffer(16)
	var mishap: Component = Component.empty()
	var active = true

	fun saveMishap(text: Component) {
		mishap = text
	}

	fun pushPattern(pattern: HexPattern) {
		patterns.add(pattern)
	}

	fun saveStack(iotas: List<Iota>) {
		stack.clear()
		iotas.forEach { iota -> stack.add(iota.display()) }
	}

	fun toNbt(): CompoundTag {
		val tag = CompoundTag()

		val nbtLedger = ListTag()
		patterns.buffer().forEach { pattern -> nbtLedger.add(pattern.serializeToNBT()) }
		tag.putList("ledger", nbtLedger)

		val nbtStack = ListTag()
		stack.buffer().forEach { iota -> nbtStack.add(StringTag.valueOf(Component.Serializer.toJson(iota))) }
		tag.putList("stack", nbtStack)

		tag.putString("mishap", Component.Serializer.toJson(mishap))

		return tag
	}

	companion object {
		fun createFromNbt(tag: CompoundTag): LedgerInstance {
			val state = LedgerInstance()
			tag.getList("ledger", CompoundTag.TAG_COMPOUND.toInt()).forEach { pattern -> state.patterns.add(HexPattern.fromNBT(pattern as CompoundTag)) }
			tag.getList("stack", CompoundTag.TAG_STRING.toInt()).forEach { iota -> state.stack.add(Component.Serializer.fromJson((iota as StringTag).getAsString())!!) }
			state.mishap = Component.Serializer.fromJson(tag.getString("mishap"))!!
			return state
		}
	}
}