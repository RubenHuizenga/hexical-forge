package miyucomics.hexical.features.player

import dev.kosmx.playerAnim.api.layered.ModifierLayer
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess
import miyucomics.hexical.features.curios.curios.FluteCurioPlayerModel
import miyucomics.hexical.features.curios.curios.HandbellCurioItemModel
import miyucomics.hexical.features.curios.curios.HandbellCurioPlayerModel
import miyucomics.hexical.features.evocation.EvocationAnimation
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.InitHook
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.InteractionHand

object PlayerAnimatorHook : InitHook() {
	override fun init() {
		PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register { player, stack ->
			stack.addAnimLayer(1000, EvocationAnimation(player))

			stack.addAnimLayer(100, ModifierLayer(FluteCurioPlayerModel(player)).also {
				it.addModifierBefore(object : MirrorModifier() {
					override fun isEnabled() = (player.mainArm == HumanoidArm.LEFT) xor (player.getItemInHand(InteractionHand.OFF_HAND).`is`(HexicalItems.CURIO_FLUTE.get()) && !player.getItemInHand(InteractionHand.MAIN_HAND).`is`(HexicalItems.CURIO_FLUTE.get()))
				})
			})

			val handbellAnimation = ModifierLayer(HandbellCurioPlayerModel(player)).also {
				it.addModifierBefore(object : MirrorModifier() {
					override fun isEnabled() = (player.mainArm == HumanoidArm.LEFT) xor (player.getItemInHand(InteractionHand.OFF_HAND).`is`(HexicalItems.CURIO_HANDBELL.get()) && !player.getItemInHand(InteractionHand.MAIN_HAND).`is`(HexicalItems.CURIO_HANDBELL.get()))
				})
			}
			PlayerAnimationAccess.getPlayerAssociatedData(player).set(HandbellCurioItemModel.clientReceiver, handbellAnimation)
			stack.addAnimLayer(100, handbellAnimation)
		}
	}
}