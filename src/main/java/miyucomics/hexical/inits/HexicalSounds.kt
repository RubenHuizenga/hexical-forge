package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject
import net.minecraft.core.Registry
import net.minecraft.sounds.SoundEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object HexicalSounds {
    private val SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, HexicalMain.MOD_ID)

	val AMETHYST_MELT: RegistryObject<SoundEvent> = SOUNDS.register("amethyst_melt") {
		SoundEvent.createVariableRangeEvent(HexicalMain.id("amethyst_melt"))
	}
	val ITEM_DUNKS: RegistryObject<SoundEvent> = SOUNDS.register("item_dunks") {
		SoundEvent.createVariableRangeEvent(HexicalMain.id("item_dunks"))
	}
	val EVOKING_MURMUR: RegistryObject<SoundEvent> = SOUNDS.register("evoking_murmur") {
		SoundEvent.createVariableRangeEvent(HexicalMain.id("evoking_murmur"))
	}
	val EVOKING_CAST: RegistryObject<SoundEvent> = SOUNDS.register("evoking_casts") {
		SoundEvent.createVariableRangeEvent(HexicalMain.id("evoking_casts"))
	}
	val LAMP_ACTIVATE: RegistryObject<SoundEvent> = SOUNDS.register("lamp_activate") {
		SoundEvent.createVariableRangeEvent(HexicalMain.id("lamp_activate"))
	}
	val LAMP_DEACTIVATE: RegistryObject<SoundEvent> = SOUNDS.register("lamp_deactivate") {
		SoundEvent.createVariableRangeEvent(HexicalMain.id("lamp_deactivate"))
	}
	
	@JvmField
	val SUDDEN_REALIZATION: RegistryObject<SoundEvent> = SOUNDS.register("sudden_realization") {
		SoundEvent.createVariableRangeEvent(HexicalMain.id("sudden_realization"))
	}
	val REPLENISH_AIR: RegistryObject<SoundEvent> = SOUNDS.register("replenish_air") {
		SoundEvent.createVariableRangeEvent(HexicalMain.id("replenish_air"))
	}
	val SCARAB_CHIRPS: RegistryObject<SoundEvent> = SOUNDS.register("scarab_chirps") {
		SoundEvent.createVariableRangeEvent(HexicalMain.id("scarab_chirps"))
	}

	val HANDBELL_CHIMES: RegistryObject<SoundEvent> = SOUNDS.register("handbell_chimes") {
		SoundEvent.createVariableRangeEvent(HexicalMain.id("handbell_chimes"))
	}

	fun init() {
		SOUNDS.register(MOD_BUS)
	}
}