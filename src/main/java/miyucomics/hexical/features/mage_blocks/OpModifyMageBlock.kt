package miyucomics.hexical.features.mage_blocks

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import miyucomics.hexical.blocks.MageBlock
import miyucomics.hexical.blocks.MageBlockEntity
import miyucomics.hexical.inits.HexicalAdvancements
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.server.level.ServerPlayer
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

class OpModifyMageBlock(private val property: String, arguments: Int = 0) : SpellAction {
	override val argc = arguments + 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getBlockPos(0, argc)
		env.assertPosInRange(position)
		if (env.world.getBlockState(position).block !is MageBlock)
			throw MishapBadBlock.of(position, "mage_block")
		if (property == "energized")
			args.getPositiveIntUnder(1, 16, argc)
		if (property == "ephemeral")
			args.getPositiveInt(1, argc)
		return SpellAction.Result(Spell(position, property, args.subList(1, args.size).toList()), 0, listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0)))
	}

	private data class Spell(val pos: BlockPos, val property: String, val args: List<Iota>) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (env.castingEntity is ServerPlayer)
				HexicalAdvancements.DIY.trigger(env.castingEntity as ServerPlayer)
			(env.world.getBlockEntity(pos) as MageBlockEntity).setProperty(property, args)
			env.world.updateNeighborsAt(pos, HexicalBlocks.MAGE_BLOCK.get())
		}
	}
}