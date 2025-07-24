package miyucomics.hexical.interfaces

import miyucomics.hexical.data.ArchLampState
import miyucomics.hexical.data.LesserSentinelState
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag

interface PlayerEntityMinterface {
	fun getArchLampCastedThisTick(): Boolean
	fun getArchLampState(): ArchLampState
	fun archLampCasted()

	fun getWristpocket(): ItemStack
	fun setWristpocket(stack: ItemStack)

	fun getEvocation(): CompoundTag
	fun setEvocation(hex: CompoundTag)

	fun getLesserSentinels(): LesserSentinelState
	fun setLesserSentinels(state: LesserSentinelState)
}