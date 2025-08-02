package miyucomics.hexical.features.scarabs

import at.petrak.hexcasting.api.casting.PatternShapeMatch
import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.utils.getList
import at.petrak.hexcasting.common.casting.PatternRegistryManifest
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.HexSerialization
import miyucomics.hexical.misc.InitHook
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.Tag
import net.minecraft.core.Registry
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel

object ScarabHandler : InitHook() {
	override fun init() {
		Registry.register(IXplatAbstractions.INSTANCE.continuationTypeRegistry, HexicalMain.id("scarab"), ScarabFrame.TYPE)
	}

	@JvmStatic
	fun handleScarab(vm: CastingVM, iota: PatternIota, continuation: SpellContinuation, world: ServerLevel): CastResult? {
		val env = vm.env
		if (env !is PlayerBasedCastEnv)
			return null

		val pattern = iota.pattern
		val patternTest = PatternRegistryManifest.matchPattern(pattern, env, false)
		if (patternTest !is PatternShapeMatch.Nothing)
			return null

		if (wouldBeRecursive(pattern.anglesSignature(), continuation))
			return null
		val scarab = getScarab(env.castingEntity!! as ServerPlayer) ?: return null
		val program = HexSerialization.deserializeHex(scarab.getList("hex", Tag.TAG_COMPOUND.toInt()) ?: return null, world)

		val newStack = vm.image.stack.toMutableList()
		newStack.add(iota)

		return CastResult(
			iota,
			continuation
				.pushFrame(ScarabFrame(pattern.anglesSignature()))
				.pushFrame(FrameEvaluate(SpellList.LList(program), false)),
			vm.image.copy(stack = newStack),
			listOf(),
			ResolvedPatternType.EVALUATED,
			HexEvalSounds.NOTHING
		)
	}

	private fun wouldBeRecursive(pattern: String, continuation: SpellContinuation): Boolean {
		var cont = continuation
		while (cont is SpellContinuation.NotDone) {
			if (cont.frame is ScarabFrame && (cont.frame as ScarabFrame).signature == pattern)
				return true
			cont = cont.next
		}
		return false
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
}