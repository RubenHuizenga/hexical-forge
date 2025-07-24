package miyucomics.hexical.casting.patterns.lamp

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.casting.mishaps.NeedsArchGenieLampMishap
import miyucomics.hexical.items.ArchLampItem
import miyucomics.hexical.items.hasActiveArchLamp
import miyucomics.hexical.registry.HexicalItems
import net.minecraft.server.level.ServerPlayer

class OpGetArchLampMedia : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster !is ServerPlayer)
			return listOf(NullIota())
		if (!hasActiveArchLamp(caster))
			throw NeedsArchGenieLampMishap()
		for (stack in caster.inventory.items)
			if (stack.item == HexicalItems.ARCH_LAMP_ITEM && stack.orCreateTag.getBoolean("active"))
				return ((stack.item as ArchLampItem).getMedia(stack).toDouble() / MediaConstants.DUST_UNIT).asActionResult
		if (caster.offhandItem.item == HexicalItems.ARCH_LAMP_ITEM && caster.offhandItem.orCreateTag.getBoolean("active"))
			return ((caster.offhandItem.item as ArchLampItem).getMedia(caster.offhandItem).toDouble() / MediaConstants.DUST_UNIT).asActionResult
		return listOf(NullIota())
	}
}