package miyucomics.hexical.features.breaking

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.core.BlockPos

object OpBreakSilk : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val pos = args.getBlockPos(0, argc)
		env.assertPosInRange(pos)
		return SpellAction.Result(Spell(pos), MediaConstants.DUST_UNIT / 2, listOf())
	}

	private data class Spell(val pos: BlockPos) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val state = env.world.getBlockState(pos)
			if (state.isAir)
				return
			val tool = ItemStack(Items.DIAMOND_PICKAXE)
			tool.enchant(Enchantments.SILK_TOUCH, 1)
			Block.dropResources(state, env.world, pos, null, null, tool)
			env.world.destroyBlock(pos, false)
		}
	}
}