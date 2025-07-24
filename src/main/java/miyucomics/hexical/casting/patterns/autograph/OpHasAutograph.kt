package miyucomics.hexical.casting.patterns.autograph

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexpose.iotas.getItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag

class OpHasAutograph : ConstMediaAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val stack = args.getItemStack(0, argc)
		if (!stack.hasTag())
			return false.asActionResult
		if (!stack.tag!!.contains("autographs"))
			return false.asActionResult
		val player = args.getPlayer(1, argc)
		val list = stack.tag!!.getList("autographs", Tag.TAG_COMPOUND.toInt())
		return (list.count { (it as CompoundTag).getString("name") == player.scoreboardName } > 0).asActionResult
	}
}