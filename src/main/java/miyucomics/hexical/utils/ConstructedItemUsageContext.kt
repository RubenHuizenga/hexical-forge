package miyucomics.hexical.utils

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level

class ConstructedItemUsageContext(world: Level, position: BlockPos, normal: Direction, private val horizontalNormal: Direction, stack: ItemStack, hand: InteractionHand) : UseOnContext(world, null, hand, stack, BlockHitResult(Vec3.atCenterOf(position.offset(normal.normal)), normal, position, false)) {
	override fun getHorizontalDirection() = horizontalNormal
	override fun isSecondaryUseActive() = false
}