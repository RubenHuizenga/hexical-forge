package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.xplat.IXplatAbstractions

object OpProxyReadCharmed : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env !is CharmCastEnv)
			throw NeedsCharmedItemMishap()
		val dataHolder = IXplatAbstractions.INSTANCE.findDataHolder(env.stack)
		if (dataHolder == null)
			throw MishapBadOffhandItem.of(env.stack, "iota.read")
		return listOf(dataHolder.readIota(env.world) ?: return listOf(NullIota()))
	}
}