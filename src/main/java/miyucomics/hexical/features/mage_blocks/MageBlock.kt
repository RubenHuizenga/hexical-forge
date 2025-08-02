package miyucomics.hexical.features.mage_blocks

import at.petrak.hexcasting.common.blocks.BlockConjured
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.SoundType
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType

class MageBlock : BlockConjured(
	Properties.of()
		.noOcclusion()
		.noLootTable()
		.instabreak()
		.lightLevel { _ -> 2 }
		.mapColor(MapColor.NONE)
		.isSuffocating { _, _, _ -> false }
		.isViewBlocking { _, _, _ -> false }
		.isValidSpawn { _, _, _, _ -> false }
		.sound(SoundType.AMETHYST_CLUSTER)
) {
	override fun isSignalSource(state: BlockState) = true
	override fun getSignal(state: BlockState, world: BlockGetter, pos: BlockPos, direction: Direction): Int {
		val tile = world.getBlockEntity(pos)
		if (tile !is MageBlockEntity)
			return 0
		if (tile.properties["energized"]!!)
			return tile.redstone
		return 0
	}

	override fun fallOn(world: Level, state: BlockState, pos: BlockPos, entity: Entity, fallDistance: Float) {
		val tile = world.getBlockEntity(pos) as MageBlockEntity
		if (tile.properties["bouncy"]!!)
			entity.causeFallDamage(fallDistance, 0.0f, world.damageSources().fall())
		else
			super.fallOn(world, state, pos, entity, fallDistance)
	}

	override fun updateEntityAfterFallOn(world: BlockGetter, entity: Entity) {
		val tile = world.getBlockEntity(entity.blockPosition().below())
		if (tile !is MageBlockEntity)
			return
		if (tile.properties["bouncy"]!!) {
			val velocity = entity.deltaMovement
			if (velocity.y < 0) {
				entity.deltaMovement = velocity.with(Direction.Axis.Y, -velocity.y)
				entity.fallDistance = 0f
			}
		} else
			super.updateEntityAfterFallOn(world, entity)
	}

	override fun use(state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
		val tile = world.getBlockEntity(pos) as MageBlockEntity
		if (!tile.properties["replaceable"]!!)
			return InteractionResult.PASS
		val stack = player.getItemInHand(hand)
		val item = stack.item
		if (item !is BlockItem)
			return InteractionResult.PASS
		if (!player.isCreative)
			stack.shrink(1)
		world.playLocalSound(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1f, 1f, true)
		world.setBlockAndUpdate(pos, item.block.getStateForPlacement(BlockPlaceContext(world, player, hand, stack, hit))!!)
		return InteractionResult.SUCCESS
	}

	override fun playerWillDestroy(world: Level, position: BlockPos, state: BlockState, player: Player?) {
		val tile = world.getBlockEntity(position) as MageBlockEntity
		world.playLocalSound(position.x.toDouble(), position.y.toDouble(), position.z.toDouble(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1f, 1f, true)
		world.gameEvent(player, GameEvent.BLOCK_DESTROY, position)
		world.setBlockAndUpdate(position, Blocks.AIR.defaultBlockState())
		world.removeBlockEntity(position)
		if (tile.properties["volatile"]!!) {
			for (offset in Direction.stream()) {
				val positionToTest = position.relative(offset)
				val otherState = world.getBlockState(positionToTest)
				val block = otherState.block
				if (block == HexicalBlocks.MAGE_BLOCK)
					block.playerWillDestroy(world, positionToTest, otherState, player)
			}
		}
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState) = MageBlockEntity(pos, state)
	override fun <T : BlockEntity> getTicker(pworld: Level, pstate: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T> = BlockEntityTicker { world, position, state, blockEntity -> tick(world, position, state, blockEntity as MageBlockEntity) }

	companion object {
		fun tick(world: Level, position: BlockPos, state: BlockState, blockEntity: MageBlockEntity) {
			if (blockEntity.properties["ephemeral"]!!) {
				blockEntity.lifespan--
				if (blockEntity.lifespan <= 0)
					HexicalBlocks.MAGE_BLOCK.get().playerWillDestroy(world, position, state, null)	
			}
		}
	}
}