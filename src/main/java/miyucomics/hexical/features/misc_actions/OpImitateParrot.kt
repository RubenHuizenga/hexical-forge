package miyucomics.hexical.features.misc_actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexpose.iotas.getIdentifier
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Parrot
import net.minecraftforge.registries.ForgeRegistries
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import net.minecraft.resources.ResourceLocation

object OpImitateParrot : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val pos = args.getVec3(0, argc)
		env.assertVecInRange(pos)
		val id = args.getIdentifier(1, argc)
		if (!ForgeRegistries.ENTITY_TYPES.containsKey(id))
			throw MishapInvalidIota.of(args[0], 0, "entity_id")
		return SpellAction.Result(Spell(pos, ForgeRegistries.ENTITY_TYPES.getValue(id)!!), MediaConstants.DUST_UNIT / 2, listOf())
	}

	private data class Spell(val pos: Vec3, val type: EntityType<*>) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val sound = ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation("entity.${type.toShortString()}.ambient")) ?: return
			env.world.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.MASTER, 1.0f, Parrot.getPitch(env.world.random))
		}
	}
}