package miyucomics.hexical.casting.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.putCompound
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3

class PigmentIota(pigment: FrozenPigment) : Iota(TYPE, pigment) {
	override fun isTruthy() = true
	val pigment: FrozenPigment = this.payload as FrozenPigment
	override fun toleratesOther(that: Iota) = typesMatch(this, that) && this.pigment == (that as PigmentIota).pigment

	override fun serialize(): Tag {
		val compound = CompoundTag()
		compound.putCompound("pigment", pigment.serializeToNBT())
		compound.putString("name", pigment.item.getDescriptionId())
		return compound
	}

	companion object {
		var TYPE: IotaType<PigmentIota> = object : IotaType<PigmentIota>() {
			override fun color() = 0xff_c466e3.toInt()
			override fun deserialize(tag: Tag, world: ServerLevel) = PigmentIota(FrozenPigment.fromNBT((tag as CompoundTag).getCompound("pigment")))
			override fun display(tag: Tag): Component {
				val compound = tag as CompoundTag
				val colorizer = FrozenPigment.fromNBT(compound.getCompound("pigment"))
				val name = Component.translatable(compound.getString("name")).string

				val display = Component.literal("")
				val steps = name.length
				for (i in 0 until steps) {
					val progress = i.toFloat() / steps.toFloat()
					val color = colorizer.colorProvider.getColor(0f, Vec3(0.0, (-4 + progress * 8).toDouble(), 0.0))
					display.append(Component.literal(name[i].toString()).withStyle { style: Style -> style.withColor(color) })
				}

				return display
			}
		}
	}
}

fun List<Iota>.getPigment(idx: Int, argc: Int = 0): FrozenPigment {
	val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
	if (x is PigmentIota)
		return x.pigment
	throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "pigment")
}