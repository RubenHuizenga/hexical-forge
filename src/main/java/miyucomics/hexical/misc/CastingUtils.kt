package miyucomics.hexical.misc

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

object CastingUtils {
	fun assertNoTruename(iota: Iota, env: CastingEnvironment) {
		val truename = MishapOthersName.getTrueNameFromDatum(iota, env.castingEntity as? ServerPlayer)
		if (truename != null)
			throw MishapOthersName(truename)
	}

	@JvmStatic
	fun isEnlightened(player: ServerPlayer): Boolean {
		val advancement = player.getServer()!!.advancements.getAdvancement(HexAPI.modLoc("enlightenment"))
		val tracker = player.advancements
		return tracker.getOrStartProgress(advancement!!).isDone
	}

	@JvmStatic
	fun giveIota(player: ServerPlayer, iota: Iota) {
		val image = IXplatAbstractions.INSTANCE.getStaffcastVM(player, InteractionHand.MAIN_HAND).image
		val newImage = if (image.parenCount == 0) {
			val stack = image.stack.toMutableList()
			stack.add(iota)
			image.copy(stack = stack)
		} else {
			val parenthesized = image.parenthesized.toMutableList()
			parenthesized.add(ParenthesizedIota(iota, false))
			image.copy(parenthesized = parenthesized)
		}
		IXplatAbstractions.INSTANCE.setStaffcastImage(player, newImage)
	}
}