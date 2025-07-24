package miyucomics.hexical.casting.patterns.grimoire

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.utils.getOrCreateCompound
import at.petrak.hexcasting.api.utils.putCompound
import miyucomics.hexical.registry.HexicalItems
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag

class OpGrimoireIndex : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val itemInfo = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.GRIMOIRE_ITEM.get()) }
		if (itemInfo == null)
			throw MishapBadOffhandItem.of(null, "grimoire")

		val stack = itemInfo.stack
		populateGrimoireMetadata(stack)
		val metadata = stack.orCreateTag.getCompound("metadata")

		val result = mutableListOf<PatternIota>()
		for (pattern in metadata.allKeys)
			result.add(PatternIota(HexPattern.fromAngles(pattern, HexDir.values()[metadata.getCompound(pattern).getInt("direction")])))
		return listOf(ListIota(result.toList()))
	}

	companion object {
		fun populateGrimoireMetadata(grimoire: ItemStack) {
			if (grimoire.orCreateTag.contains("metadata"))
				return
			val metadata = CompoundTag()
			for (key in grimoire.orCreateTag.getOrCreateCompound("expansions").allKeys) {
				val data = CompoundTag()
				data.putInt("direction", HexDir.EAST.ordinal)
				metadata.putCompound(key, data)
			}
			grimoire.orCreateTag.putCompound("metadata", metadata)
		}
	}
}