package miyucomics.hexical.features.confection

import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.IotaType
import miyucomics.hexical.misc.CastingUtils
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

class HexburstItem : Item(Properties().stacksTo(16).food(FoodProperties.Builder().alwaysEat().fast().build())) {
	override fun getUseDuration(stack: ItemStack) = 10
	override fun finishUsingItem(stack: ItemStack, world: Level, user: LivingEntity): ItemStack {
		if (world.isClientSide)
			return super.finishUsingItem(stack, world, user)
		if (user !is ServerPlayer)
			return super.finishUsingItem(stack, world, user)
		CastingUtils.giveIota(user, if (stack.orCreateTag.contains("iota"))
			IotaType.deserialize(stack.orCreateTag.getCompound("iota"), world as ServerLevel)
		else
			GarbageIota())
		return super.finishUsingItem(stack, world, user)
	}
}