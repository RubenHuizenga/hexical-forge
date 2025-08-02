package miyucomics.hexical.features.lore

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import miyucomics.hexpose.iotas.getText
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component

object OpItemName : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val item = args.getItemEntity(0, argc)
		env.assertEntityInRange(item)
		if (args[1] is NullIota)
			return SpellAction.Result(Spell(item.item, null), 0, listOf(ParticleSpray.cloud(item.position(), 1.0)))
		val title = args.getText(1, argc)
		return SpellAction.Result(Spell(item.item, title), 0, listOf(ParticleSpray.cloud(item.position(), 1.0)))
	}

	private data class Spell(val stack: ItemStack, val lore: Component?) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			stack.setHoverName(lore)
		}
	}
}