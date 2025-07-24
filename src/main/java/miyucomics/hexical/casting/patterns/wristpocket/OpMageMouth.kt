package miyucomics.hexical.casting.patterns.wristpocket

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.casting.mishaps.InedibleWristpocketMishap
import miyucomics.hexical.interfaces.PlayerEntityMinterface
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.server.level.ServerPlayer

class OpMageMouth : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env.castingEntity !is ServerPlayer)
			throw MishapBadCaster()

		val stack = (env.castingEntity as PlayerEntityMinterface).getWristpocket()
		if (stack.`is`(Items.POTION) || stack.`is`(Items.HONEY_BOTTLE) || stack.`is`(Items.MILK_BUCKET) || stack.item.isEdible())
			return SpellAction.Result(Spell(stack), MediaConstants.DUST_UNIT, listOf())
		throw InedibleWristpocketMishap()
	}

	private data class Spell(val stack: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val caster = env.castingEntity as ServerPlayer
			val originalItem = caster.getItemInHand(env.castingHand)
			caster.setItemInHand(env.castingHand, stack)
			val newStack = stack.finishUsingItem(env.world, caster)
			(caster as PlayerEntityMinterface).setWristpocket(newStack)
			caster.setItemInHand(env.castingHand, originalItem)
		}
	}
}