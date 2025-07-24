package miyucomics.hexical.utils

import at.petrak.hexcasting.api.casting.PatternShapeMatch
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.casting.PatternRegistryManifest
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexical.casting.frames.ScarabFrame
import miyucomics.hexical.items.ScarabBeetleItem
import miyucomics.hexical.registry.HexicalItems
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel

object InjectionHelper {
	@JvmStatic
	fun handleScarab(vm: CastingVM, iota: PatternIota, continuation: SpellContinuation, world: ServerLevel): CastResult? {
		val env = vm.env
		if (env !is PlayerBasedCastEnv)
			return null

		val pattern = iota.pattern
		val patternTest = PatternRegistryManifest.matchPattern(pattern, env, false)
		if (patternTest !is PatternShapeMatch.Nothing)
			return null

		if (ScarabBeetleItem.wouldBeRecursive(pattern.anglesSignature(), continuation))
			return null
		val scarab = getScarab(env.castingEntity!! as ServerPlayer) ?: return null
		val program = IotaType.deserialize(scarab.getOrCreateTag().getCompound("program"), world) as? ListIota ?: return null

		val newStack = vm.image.stack.toMutableList()
		newStack.add(iota)

		return CastResult(
			iota,
			continuation
				.pushFrame(ScarabFrame(pattern.anglesSignature()))
				.pushFrame(FrameEvaluate(program.list, false)),
			vm.image.copy(stack = newStack),
			listOf(),
			ResolvedPatternType.EVALUATED,
			HexEvalSounds.NOTHING
		)
	}

	private fun getScarab(player: ServerPlayer): ItemStack? {
		val inventory = player.inventory
		for (smallInventory in listOf(inventory.items, inventory.armor, inventory.offhand)) {
			for (stack in smallInventory) {
				val nbt = stack.tag
				if (stack.`is`(HexicalItems.SCARAB_BEETLE_ITEM.get()) && nbt != null && nbt.getBoolean("active"))
					return stack
			}
		}
		return null
	}

	@JvmStatic
	fun handleGrimoire(vm: CastingVM, iota: Iota, world: ServerLevel): ExecutionClientView? {
		val env = vm.env
		if (env !is StaffCastEnv)
			return null
		if (vm.image.escapeNext || iota.type !== HexIotaTypes.PATTERN)
			return null

		val pattern = (iota as PatternIota).pattern
		val grimoire = getGrimoire(env.castingEntity!! as ServerPlayer, pattern) ?: return null

		val data = grimoire.getOrCreateTag().getCompound("expansions")
		if (!data.contains(pattern.anglesSignature())) return null
		val deserialized = IotaType.deserialize(data.getCompound(pattern.anglesSignature()), world) as? ListIota ?: return null

		return vm.queueExecuteAndWrapIotas(deserialized.list.toList(), world)
	}

	private fun getGrimoire(player: ServerPlayer, pattern: HexPattern): ItemStack? {
		val inventory = player.inventory
		for (smallInventory in listOf(inventory.items, inventory.armor, inventory.offhand)) {
			for (stack in smallInventory) {
				val nbt = stack.tag
				if (stack.`is`(HexicalItems.GRIMOIRE_ITEM.get()) && nbt != null && nbt.getCompound("expansions").contains(pattern.anglesSignature()))
					return stack
			}
		}
		return null
	}
}