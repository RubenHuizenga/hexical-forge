package miyucomics.hexical.features.curios.curios

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import miyucomics.hexical.features.curios.CurioItem
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand

object CompassCurio : CurioItem() {
	override fun postCharmCast(user: ServerPlayer, item: ItemStack, hand: InteractionHand, world: ServerLevel, stack: List<Iota>) {
		val location = stack.lastOrNull()
		if (location !is Vec3Iota) {
			item.orCreateTag.remove("needle")
			return
		}
		item.orCreateTag.putIntArray("needle", listOf(location.vec3.x.toInt(), location.vec3.y.toInt(), location.vec3.z.toInt()))
	}
}