package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.eval.ExecutionClientView
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel

object GrimoireHandler {
	@JvmStatic
	fun handleGrimoire(vm: CastingVM, iota: Iota, world: ServerLevel): ExecutionClientView? {
		val env = vm.env
		if (env !is StaffCastEnv)
			return null
		if (vm.image.escapeNext || iota.type !== HexIotaTypes.PATTERN)
			return null

		val pattern = (iota as PatternIota).pattern
		val expansion = getExpansion(env.castingEntity!! as ServerPlayer, pattern) ?: return null
		return vm.queueExecuteAndWrapIotas(expansion, world)
	}

	private fun getExpansion(player: ServerPlayer, pattern: HexPattern): List<Iota>? {
		val inventory = player.inventory
		inventory.offhand.toMutableList().also {
			it.addAll(inventory.items)
			it.addAll(inventory.armor)
		}.forEach {
			val nbt = it.tag?.getCompound("expansions")
			if (it.`is`(HexicalItems.GRIMOIRE_ITEM.get()) && nbt != null && nbt.contains(pattern.anglesSignature()))
				return HexSerialization.backwardsCompatibleReadHex(it.getOrCreateTag().getCompound("expansions"), pattern.anglesSignature(), player.serverLevel())
		}
		return null
	}
}