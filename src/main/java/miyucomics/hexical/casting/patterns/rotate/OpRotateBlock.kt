package miyucomics.hexical.casting.patterns.rotate

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.commands.arguments.blocks.BlockStateParser

class OpRotateBlock : SpellAction {
	override val argc = 2

	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val target = args.getBlockPos(0, argc)
		env.assertPosInRange(target)
		val rotation = args.getVec3(1, argc)
		val direction = Direction.fromDelta(rotation.x.toInt(), rotation.y.toInt(), rotation.z.toInt()) ?: throw MishapInvalidIota.of(args[1], 0, "axis_vector")

		val block = env.world.getBlockState(target)
		rotationProperties.forEach {
			if (block.hasProperty(it))
				return SpellAction.Result(Spell(target, direction), MediaConstants.DUST_UNIT / 8, listOf(ParticleSpray.burst(target.getCenter(), 1.0)))
		}

		throw MishapBadBlock.of(target, "rotatable")
	}

	private data class Spell(val target: BlockPos, val direction: Direction) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val blockState = env.world.getBlockState(target)
			if (!env.canEditBlockAt(target) || !IXplatAbstractions.INSTANCE.isBreakingAllowed(env.world, target, blockState, env.caster))
				return
			setBlockDirection(env.world, target, direction)
		}
	}

	companion object {
		private val rotationProperties: List<DirectionProperty> = listOf(BlockStateProperties.FACING, BlockStateProperties.FACING_HOPPER, BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.VERTICAL_DIRECTION)

		private fun setBlockDirection(world: ServerLevel, blockPos: BlockPos, newDirection: Direction) {
			val blockState = world.getBlockState(blockPos)
			var modifiedState: BlockState? = null
			if (blockState.properties.contains(BlockStateProperties.FACING))
				modifiedState = blockState.setValue(BlockStateProperties.FACING, newDirection)
			if (blockState.properties.contains(BlockStateProperties.FACING_HOPPER))
				modifiedState = blockState.setValue(BlockStateProperties.FACING_HOPPER, if (newDirection == Direction.UP) Direction.DOWN else newDirection)
			if (blockState.properties.contains(BlockStateProperties.HORIZONTAL_FACING)) {
				if (newDirection == Direction.UP || newDirection == Direction.DOWN)
					return
				modifiedState = blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, newDirection)
			}
			if (blockState.properties.contains(BlockStateProperties.VERTICAL_DIRECTION)) {
				if (newDirection == Direction.EAST || newDirection == Direction.WEST || newDirection == Direction.NORTH || newDirection == Direction.SOUTH)
					return
				modifiedState = blockState.setValue(BlockStateProperties.VERTICAL_DIRECTION, newDirection)
			}

			if (modifiedState == null)
				return
			world.setBlockAndUpdate(blockPos, modifiedState)
			world.updateNeighborsAt(blockPos, modifiedState.block)
		}
	}
}