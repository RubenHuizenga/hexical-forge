package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.features.lamps.NeedsArchLampMishap
import miyucomics.hexical.features.lamps.ArchLampItem
import miyucomics.hexical.features.lamps.hasActiveArchLamp
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.server.level.ServerPlayer

object OpGetArchLampMedia : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster !is ServerPlayer)
			return listOf(NullIota())
		if (!hasActiveArchLamp(caster))
			throw NeedsArchLampMishap()
		for (stack in caster.inventory.items)
			if (stack.item == HexicalItems.ARCH_LAMP_ITEM && stack.orCreateTag.getBoolean("active"))
				return ((stack.item as ArchLampItem).getMedia(stack).toDouble() / MediaConstants.DUST_UNIT).asActionResult
		if (caster.offhandItem.item == HexicalItems.ARCH_LAMP_ITEM && caster.offhandItem.orCreateTag.getBoolean("active"))
			return ((caster.offhandItem.item as ArchLampItem).getMedia(caster.offhandItem).toDouble() / MediaConstants.DUST_UNIT).asActionResult
		return listOf(NullIota())
	}
}