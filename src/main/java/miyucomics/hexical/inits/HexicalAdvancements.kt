package miyucomics.hexical.inits

import com.google.gson.JsonObject
import miyucomics.hexical.HexicalMain
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.critereon.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.core.registries.Registries
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object HexicalAdvancements {
	lateinit var AR: SpecklikeCriterion
	lateinit var CONJURE_CAKE: ConjureCakeCriterion
	lateinit var HEXXY: HexxyCriterion
	lateinit var DIY: DIYCriterion
	lateinit var HALLUCINATE: HallucinateCriterion
	lateinit var EDUCATE_GENIE: EducateGenieCriterion
	lateinit var RELOAD_LAMP: ReloadLampCriterion

	val EVOCATION_STATISTIC: ResourceLocation = HexicalMain.id("evocation")

	fun init(event: RegisterEvent) {
		if (event.registryKey == Registries.CUSTOM_STAT) {
			event.register(Registries.CUSTOM_STAT, EVOCATION_STATISTIC) {
				EVOCATION_STATISTIC
			}
		}
	}

	@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	object Registration {
		@SubscribeEvent
		fun onCommonSetup(event: FMLCommonSetupEvent) {
			event.enqueueWork {
				AR = CriteriaTriggers.register(SpecklikeCriterion())
				CONJURE_CAKE = CriteriaTriggers.register(ConjureCakeCriterion())
				HEXXY = CriteriaTriggers.register(HexxyCriterion())
				DIY = CriteriaTriggers.register(DIYCriterion())
				HALLUCINATE = CriteriaTriggers.register(HallucinateCriterion())
				EDUCATE_GENIE = CriteriaTriggers.register(EducateGenieCriterion())
				RELOAD_LAMP = CriteriaTriggers.register(ReloadLampCriterion())
			}
		}
	}
}

abstract class BaseCriterion<T : BaseCriterion.BaseCondition>(private val id: ResourceLocation) : SimpleCriterionTrigger<T>() {
	override fun createInstance(obj: JsonObject, playerPredicate: ContextAwarePredicate, predicateDeserializer: DeserializationContext): T = createCondition()
	abstract class BaseCondition(id: ResourceLocation) : AbstractCriterionTriggerInstance(id, ContextAwarePredicate.ANY)
	fun trigger(player: ServerPlayer) = trigger(player) { true }
	protected abstract fun createCondition(): T
	override fun getId(): ResourceLocation = id
}

class ConjureCakeCriterion : BaseCriterion<ConjureCakeCriterion.Condition>(HexicalMain.id("conjure_cake")) {
	override fun createCondition() = Condition()
	class Condition() : BaseCondition(HexicalMain.id("conjure_cake"))
}

class DIYCriterion : BaseCriterion<DIYCriterion.Condition>(HexicalMain.id("diy_conjuring")) {
	override fun createCondition() = Condition()
	class Condition() : BaseCondition(HexicalMain.id("diy_conjuring"))
}

class HexxyCriterion : BaseCriterion<HexxyCriterion.Condition>(HexicalMain.id("summon_hexxy")) {
	override fun createCondition() = Condition()
	class Condition() : BaseCondition(HexicalMain.id("summon_hexxy"))
}

class HallucinateCriterion : BaseCriterion<HallucinateCriterion.Condition>(HexicalMain.id("hallucinate")) {
	override fun createCondition() = Condition()
	class Condition() : BaseCondition(HexicalMain.id("hallucinate"))
}

class EducateGenieCriterion : BaseCriterion<EducateGenieCriterion.Condition>(HexicalMain.id("educate_genie")) {
	override fun createCondition() = Condition()
	class Condition() : BaseCondition(HexicalMain.id("educate_genie"))
}

class ReloadLampCriterion : BaseCriterion<ReloadLampCriterion.Condition>(HexicalMain.id("reload_lamp")) {
	override fun createCondition() = Condition()
	class Condition() : BaseCondition(HexicalMain.id("reload_lamp"))
}

class SpecklikeCriterion : BaseCriterion<SpecklikeCriterion.Condition>(HexicalMain.id("specklike")) {
	override fun createCondition() = Condition()
	class Condition() : BaseCondition(HexicalMain.id("specklike"))
}