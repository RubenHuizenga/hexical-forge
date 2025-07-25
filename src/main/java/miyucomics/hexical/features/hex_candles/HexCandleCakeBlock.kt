package miyucomics.hexical.features.hex_candles

import at.petrak.hexcasting.api.pigment.ColorProvider
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.particles.ConjureParticleOptions
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.*
import net.minecraft.world.entity.player.Player
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity

class HexCandleCakeBlock : CandleCakeBlock(HexicalBlocks.HEX_CANDLE_BLOCK.get(), Properties.copy(Blocks.CANDLE_CAKE)), EntityBlock {
	override fun use(state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
		if (player.isShiftKeyDown)
			return super.use(state, world, pos, player, hand, hit)
		if (!state.getValue(AbstractCandleBlock.LIT))
			return super.use(state, world, pos, player, hand, hit)

		val stack = player.getItemInHand(hand)
		val candle = (world.getBlockEntity(pos)!! as HexCandleCakeBlockEntity)
		if (IXplatAbstractions.INSTANCE.isPigment(stack))
			candle.setPigment(FrozenPigment(stack.copy(), player.uuid))
		else
			candle.setPigment(IXplatAbstractions.INSTANCE.getPigment(player))
		world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
		return InteractionResult.SUCCESS
	}

	override fun animateTick(state: BlockState, world: Level, pos: BlockPos, random: RandomSource) {
		if (!state.getValue(AbstractCandleBlock.LIT))
			return

		val blockEntity = world.getBlockEntity(pos)
		if (blockEntity !is HexCandleCakeBlockEntity)
			return

		val colorProvider = blockEntity.getPigment().colorProvider
		getParticleOffsets(state).forEach { offset: Vec3 -> spawnCandleParticles(world, Vec3.atLowerCornerOf(pos).add(offset), random, colorProvider) }
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = HexCandleCakeBlockEntity(pos, state)

	companion object {
		fun spawnCandleParticles(world: Level, position: Vec3, random: RandomSource, colorProvider: ColorProvider) {
			if (random.nextFloat() < 0.17f)
				world.playLocalSound(position.x + 0.5, position.y + 0.5, position.z + 0.5, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, true)
			world.addParticle(
				ConjureParticleOptions(colorProvider.getColor(world.gameTime.toFloat(), position)),
				position.x, position.y, position.z,
				0.0, world.random.nextFloat() * 0.02, 0.0
			)
		}
	}
}