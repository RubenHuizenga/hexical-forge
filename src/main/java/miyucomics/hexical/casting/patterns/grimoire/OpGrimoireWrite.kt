package miyucomics.hexical.casting.patterns.grimoire

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.utils.containsTag
import at.petrak.hexcasting.api.utils.getCompound
import at.petrak.hexcasting.api.utils.putCompound
import miyucomics.hexical.casting.patterns.grimoire.OpGrimoireIndex.Companion.populateGrimoireMetadata
import miyucomics.hexical.registry.HexicalItems
import miyucomics.hexical.utils.CastingUtils
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag

class OpGrimoireWrite : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val itemInfo = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.GRIMOIRE_ITEM.get()) }
		if (itemInfo == null)
			throw MishapBadOffhandItem.of(null, "grimoire")

		val stack = itemInfo.stack
		populateGrimoireMetadata(stack)
		val key = args.getPattern(0, argc)
		args.getList(1, argc)
		CastingUtils.assertNoTruename(args[1], env)

		if (stack.containsTag("expansions") && stack.getCompound("expansions")!!.size() > 512)
			throw MishapBadOffhandItem.of(null, "nonfull_grimoire")

		return SpellAction.Result(Spell(stack, key, args[1] as ListIota), 0, listOf())
	}

	private data class Spell(val stack: ItemStack, val key: HexPattern, val expansion: ListIota) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (!stack.orCreateTag.contains("expansions"))
				stack.orCreateTag.putCompound("expansions", CompoundTag())
			stack.orCreateTag.getCompound("expansions").putCompound(key.anglesSignature(), IotaType.serialize(expansion))

			if (!stack.orCreateTag.contains("metadata"))
				stack.orCreateTag.putCompound("metadata", CompoundTag())
			val data = CompoundTag()
			data.putInt("direction", key.startDir.ordinal)
			stack.orCreateTag.getCompound("metadata").putCompound(key.anglesSignature(), data)
		}
	}
}