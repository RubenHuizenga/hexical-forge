package miyucomics.hexical.features.autographs

import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.misc.ClientStorage
import miyucomics.hexical.misc.InitHook
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.world.phys.Vec3
import java.util.function.Consumer

object AutographTooltip : InitHook() {
	override fun init() {
		MinecraftForge.EVENT_BUS.register(::initItemTooltipCallbackCurio)
	}

	fun initItemTooltipCallbackCurio(event: ItemTooltipEvent) {
		val nbt = event.itemStack.tag ?: return
		if (!nbt.contains("autographs"))
			return

		event.toolTip.add(Component.translatable("hexical.autograph.header").withStyle { style -> style.withColor(ChatFormatting.GRAY) })

		nbt.getList("autographs", CompoundTag.TAG_COMPOUND.toInt()).forEach(Consumer { element: Tag? ->
			val compound = element as CompoundTag
			val name = compound.getString("name")
			val pigment = FrozenPigment.fromNBT(compound.getCompound("pigment")).colorProvider
			val output = Component.literal("")
			for (i in 0 until name.length)
				output.append(Component.literal(name[i].toString()).withStyle { style -> style.withColor(pigment.getColor((ClientStorage.ticks * 3).toFloat(), Vec3(0.0, i.toDouble(), 0.0))) })
			event.toolTip.add(output)
		})
	}
}