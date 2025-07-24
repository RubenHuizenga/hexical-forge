package miyucomics.hexical.casting.environments

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.utils.CharmedItemUtilities
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

class CharmedItemCastEnv(caster: ServerPlayer, castingHand: InteractionHand, val stack: ItemStack) : PlayerBasedCastEnv(caster, castingHand) {
	override fun extractMediaEnvironment(cost: Long, simulate: Boolean): Long {
		if (caster.isCreative) return 0
		var costLeft = cost
		val currentMedia = CharmedItemUtilities.getMedia(stack)
		val mediaToDeduct = minOf(currentMedia, costLeft)
		costLeft -= mediaToDeduct
		if (!simulate)
			CharmedItemUtilities.deductMedia(stack, mediaToDeduct)
		return costLeft
	}

	override fun getCastingHand(): InteractionHand = this.castingHand
	override fun getPigment(): FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(this.caster)

	fun getInternalStorage(): Iota {
		val nbt = this.stack.orCreateTag
		if (nbt.contains("charmed_storage"))
			return IotaType.deserialize(nbt.getCompound("charmed_storage"), caster.serverLevel())
		return NullIota()
	}

	fun setInternalStorage(iota: Iota) =
		this.stack.orCreateTag.putCompound("charmed_storage", IotaType.serialize(iota))
}