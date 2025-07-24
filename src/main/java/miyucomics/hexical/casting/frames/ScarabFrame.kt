package miyucomics.hexical.casting.frames

import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel

data class ScarabFrame(val signature: String) : ContinuationFrame {
	override val type: ContinuationFrame.Type<*> = TYPE

	override fun evaluate(continuation: SpellContinuation, level: ServerLevel, harness: CastingVM): CastResult {
		return CastResult(
			NullIota(),
			continuation,
			null,
			listOf(),
			ResolvedPatternType.EVALUATED,
			HexEvalSounds.NOTHING,
		)
	}

	override fun serializeToNBT(): CompoundTag {
		val compound = CompoundTag()
		compound.putString("signature", signature)
		return compound
	}

	override fun breakDownwards(stack: List<Iota>) = true to stack
	override fun size() = 0

	companion object {
		val TYPE: ContinuationFrame.Type<ScarabFrame> = object : ContinuationFrame.Type<ScarabFrame> {
			override fun deserializeFromNBT(compound: CompoundTag, world: ServerLevel) = ScarabFrame(compound.getString("signature"))
		}
	}
}