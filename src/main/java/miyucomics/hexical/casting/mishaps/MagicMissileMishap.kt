package miyucomics.hexical.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.HexicalMain
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

class MagicMissileMishap : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.RED)
	override fun particleSpray(env: CastingEnvironment) = ParticleSpray.burst(env.mishapSprayPos(), 1.0)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Component = error(HexicalMain.MOD_ID + ":magic_missile_coordinates")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}