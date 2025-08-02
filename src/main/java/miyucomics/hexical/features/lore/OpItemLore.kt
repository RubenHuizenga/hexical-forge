package miyucomics.hexical.features.lore

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.utils.getCompound
import miyucomics.hexpose.iotas.TextIota
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component

object OpItemLore : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val item = args.getItemEntity(0, argc)
		env.assertEntityInRange(item)
		if (args[1] is NullIota)
			return SpellAction.Result(Spell(item.item, null), 0, listOf(ParticleSpray.cloud(item.position(), 1.0)))
		val lore = args.getList(1, argc).map {
			if (it !is TextIota)
				throw MishapInvalidIota.of(args[1], 0, "text_list")
			it.text
		}
		return SpellAction.Result(Spell(item.item, lore), 0, listOf(ParticleSpray.cloud(item.position(), 1.0)))
	}

	private data class Spell(val stack: ItemStack, val lore: List<Component>?) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (lore == null) {
				stack.getCompound("display")?.remove("Lore")
				return
			}

			val display = stack.getOrCreateTagElement("display")
			val loreList = ListTag()
			lore.forEach { loreList.add(StringTag.valueOf(Component.Serializer.toJson(it))) }
			display.put("Lore", loreList)
		}
	}
}