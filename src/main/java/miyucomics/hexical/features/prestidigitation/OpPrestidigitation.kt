package miyucomics.hexical.features.prestidigitation

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.data.prestidigitation.PrestidigitationHandlersHook
import net.minecraft.world.entity.Entity
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.forge.registerObject

object OpPrestidigitation : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		return when (args[0]) {
			is EntityIota -> {
				val entity = args.getEntity(0, argc)
				env.assertEntityInRange(entity)
				SpellAction.Result(EntitySpell(entity), MediaConstants.DUST_UNIT / 10, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
			}
			is Vec3Iota -> {
				val position = args.getBlockPos(0, argc)
				env.assertPosInRange(position)
				SpellAction.Result(BlockSpell(position), MediaConstants.DUST_UNIT / 10, listOf(
					ParticleSpray.cloud(
						Vec3.atCenterOf(position), 1.0)))
			}
			else -> throw MishapInvalidIota.of(args[0], 0, "entity_or_vector")
		}
	}

	private data class BlockSpell(val position: BlockPos) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			PrestidigitationHandlersHook.PRESTIDIGITATION_HANDLER.entries.forEach {
				if (it.get().tryHandleBlock(env, position))
					return
			}
		}
	}

	private data class EntitySpell(val entity: Entity) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			PrestidigitationHandlersHook.PRESTIDIGITATION_HANDLER.entries.forEach {
				if (it.get().tryHandleEntity(env, entity))
					return
			}
		}
	}
}