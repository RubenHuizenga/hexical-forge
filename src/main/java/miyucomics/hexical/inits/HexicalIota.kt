package miyucomics.hexical.inits

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.dyes.DyeIota
import miyucomics.hexical.features.pigments.PigmentIota
import net.minecraft.core.Registry
import net.minecraftforge.registries.RegisterEvent

object HexicalIota {
	fun init(event: RegisterEvent) {
		event.register(HexIotaTypes.REGISTRY.key(), HexicalMain.id("dye")) { DyeIota.TYPE }
		event.register(HexIotaTypes.REGISTRY.key(), HexicalMain.id("pigment")) { PigmentIota.TYPE }
	}
}