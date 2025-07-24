package miyucomics.hexical.casting.patterns.charms

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.putCompound
import miyucomics.hexical.utils.CastingUtils
import miyucomics.hexical.utils.CharmedItemUtilities
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag

class OpCharmItem : SpellAction {
	override val argc = 7
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val item = args.getItemEntity(0, argc)
		env.assertEntityInRange(item)
		if (CharmedItemUtilities.isStackCharmed(item.item))
			throw MishapBadEntity.of(item, "uncharmed_item")

		args.getList(1, argc)
		CastingUtils.assertNoTruename(args[1], env)
		val battery = args.getPositiveDouble(2, argc)
		return SpellAction.Result(
			Spell(
				item.item,
				args[1],
				(battery * MediaConstants.DUST_UNIT).toLong(),
				args.getBool(3, argc),
				args.getBool(4, argc),
				args.getBool(5, argc),
				args.getBool(6, argc)
			),
			3 * MediaConstants.CRYSTAL_UNIT + MediaConstants.DUST_UNIT * battery.toInt(),
			listOf(ParticleSpray.burst(item.position(), 1.0))
		)
	}

	private data class Spell(val stack: ItemStack, val instructions: Iota, val battery: Long, val left: Boolean, val leftSneak: Boolean, val right: Boolean, val rightSneak: Boolean) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val nbt = stack.orCreateTag
			val charm = CompoundTag()
			charm.putLong("media", battery)
			charm.putLong("max_media", battery)
			charm.putCompound("instructions", IotaType.serialize(instructions))
			charm.putBoolean("left", left)
			charm.putBoolean("right", right)
			charm.putBoolean("left_sneak", leftSneak)
			charm.putBoolean("right_sneak", rightSneak)
			nbt.putCompound("charmed", charm)
		}
	}
}