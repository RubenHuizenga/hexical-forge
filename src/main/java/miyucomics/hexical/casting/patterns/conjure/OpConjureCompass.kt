package miyucomics.hexical.casting.patterns.conjure

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.registry.HexicalItems
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

class OpConjureCompass : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getVec3(0, argc)
		env.assertVecInRange(position)
		val target = args.getBlockPos(1, argc)
		return SpellAction.Result(Spell(position, target), MediaConstants.DUST_UNIT * 3, listOf(ParticleSpray.burst(position, 1.0)))
	}

	private data class Spell(val position: Vec3, val target: BlockPos) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val stack = ItemStack(HexicalItems.CONJURED_COMPASS_ITEM.get(), 1)
			val nbt = stack.orCreateTag
			nbt.putInt("x", target.x)
			nbt.putInt("y", target.y)
			nbt.putInt("z", target.z)
			nbt.putString("dimension", env.world.dimensionTypeId().location().toString())
			env.world.addFreshEntity(ItemEntity(env.world, position.x, position.y, position.z, stack))
		}
	}
}