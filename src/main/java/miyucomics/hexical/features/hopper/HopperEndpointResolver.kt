package miyucomics.hexical.features.hopper

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota

fun interface HopperEndpointResolver {
	fun resolve(iota: Iota, env: CastingEnvironment, slot: Int?): HopperEndpoint?
}