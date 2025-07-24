package miyucomics.hexical.casting.patterns

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import miyucomics.hexical.blocks.HexCandleCakeBlock
import miyucomics.hexical.blocks.HexCandleCakeBlockEntity
import miyucomics.hexical.registry.HexicalAdvancements
import miyucomics.hexical.registry.HexicalBlocks
import net.minecraft.world.level.block.*
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.server.level.ServerPlayer
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

class OpHorrible : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val pos = args.getBlockPos(0, argc)
		env.assertPosInRange(pos)
		if (!env.world.getBlockState(pos).canBeReplaced())
			throw MishapBadBlock.of(pos, "replaceable")
		return SpellAction.Result(Spell(pos), 0, listOf(ParticleSpray.burst(Vec3.atCenterOf(pos), 2.0)))
	}

	private data class Spell(val pos: BlockPos) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			env.world.setBlockAndUpdate(pos, HexicalBlocks.HEX_CANDLE_CAKE_BLOCK.get().defaultBlockState().setValue(AbstractCandleBlock.LIT, true))
			(env.world.getBlockEntity(pos) as HexCandleCakeBlockEntity).setPigment(env.pigment)
			if (env.castingEntity is ServerPlayer)
				HexicalAdvancements.CONJURE_CAKE.trigger(env.castingEntity as ServerPlayer)
		}
	}
}