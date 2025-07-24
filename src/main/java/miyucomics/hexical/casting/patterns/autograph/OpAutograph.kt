package miyucomics.hexical.casting.patterns.autograph

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.utils.getOrCreateList
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.casting.mishaps.NoStaffMishap
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

class OpAutograph : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env.castingEntity !is Player)
			throw MishapBadCaster()
		if (env !is StaffCastEnv)
			throw NoStaffMishap()
		val stack = env.getHeldItemToOperateOn { true }
		if (stack == null)
			throw MishapBadOffhandItem.of(null, "anything")
		return SpellAction.Result(Spell(stack.stack), 0, listOf())
	}

	private data class Spell(val stack: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val caster = env.castingEntity as ServerPlayer
			val list = stack.orCreateTag.getOrCreateList("autographs", CompoundTag.TAG_COMPOUND.toInt())
			list.removeIf { compound -> (compound as CompoundTag).getString("name") == caster.scoreboardName }

			val compound = CompoundTag()
			compound.putString("name", caster.scoreboardName)
			compound.putCompound("pigment", IXplatAbstractions.INSTANCE.getPigment(caster).serializeToNBT())
			list.add(0, compound)
		}
	}
}