package miyucomics.hexical.items

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.casting.environments.HextitoCastEnv
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.Level

class HextitoItem : Item(Properties().stacksTo(16).food(FoodProperties.Builder().alwaysEat().fast().build())) {
	override fun getUseDuration(stack: ItemStack) = 10
	override fun finishUsingItem(stack: ItemStack, world: Level, user: LivingEntity): ItemStack {
		if (world.isClientSide)
			return super.finishUsingItem(stack, world, user)
		if (user !is ServerPlayer)
			return super.finishUsingItem(stack, world, user)
		val newVM = CastingVM(IXplatAbstractions.INSTANCE.getStaffcastVM(user, user.usedItemHand).image.copy(), HextitoCastEnv(user, InteractionHand.MAIN_HAND))
		if (newVM.image.parenCount == 0 && stack.orCreateTag.contains("hex")) {
			val deserialized = IotaType.deserialize(stack.orCreateTag.getCompound("hex"), world as ServerLevel)
			if (deserialized is ListIota)
				newVM.queueExecuteAndWrapIotas(deserialized.list.toList(), world)
		}
		IXplatAbstractions.INSTANCE.setStaffcastImage(user, newVM.image)
		return super.finishUsingItem(stack, world, user)
	}
}