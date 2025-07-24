package miyucomics.hexical.casting.patterns.evocation

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.casting.environments.EvocationCastEnv
import miyucomics.hexical.data.EvokeState
import miyucomics.hexical.interfaces.PlayerEntityMinterface
import miyucomics.hexical.registry.HexicalAdvancements
import miyucomics.hexical.registry.HexicalSounds
import miyucomics.hexical.utils.CastingUtils
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand

class OpSetEvocation : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env.castingEntity !is ServerPlayer)
			throw MishapBadCaster()
		args.getList(0, argc)
		CastingUtils.assertNoTruename(args[0], env)
		return SpellAction.Result(Spell(args[0]), MediaConstants.CRYSTAL_UNIT, listOf())
	}

	private data class Spell(val hex: Iota) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			(env.castingEntity as PlayerEntityMinterface).setEvocation(IotaType.serialize(hex))
		}
	}

	companion object {
		@JvmStatic
		fun evoke(player: ServerPlayer) {
			player.awardStat(HexicalAdvancements.EVOCATION_STATISTIC)

			EvokeState.duration[player.uuid] = HexicalMain.EVOKE_DURATION
			val nbt = (player as PlayerEntityMinterface).getEvocation()
			val hex = IotaType.deserialize(nbt, player.level() as ServerLevel)
			if (hex is ListIota) {
				val hand = if(!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty && player.getItemInHand(InteractionHand.OFF_HAND).isEmpty){ InteractionHand.OFF_HAND } else { InteractionHand.MAIN_HAND }
				val vm = CastingVM(CastingImage(), EvocationCastEnv(player, hand))
				vm.queueExecuteAndWrapIotas(hex.list.toList(), player.serverLevel())
				player.level().playSound(null, player.x, player.y, player.z, HexicalSounds.EVOKING_CAST.get(), SoundSource.PLAYERS, 1f, 1f)
			}
		}
	}
}