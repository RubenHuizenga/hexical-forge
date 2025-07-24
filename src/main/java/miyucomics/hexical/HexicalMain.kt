package miyucomics.hexical

import miyucomics.hexical.registry.*
import miyucomics.hexical.data.LesserSentinelState
import miyucomics.hexical.data.prestidigitation.PrestidigitationData
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.MinecraftForge
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.entity.Entity
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import org.spongepowered.asm.mixin.Mixins
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.launch.MixinBootstrap
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(HexicalMain.MOD_ID)
class HexicalMain {
	init {
		MixinBootstrap.init()
        Mixins.addConfiguration("hexical.mixins.json")
        MixinEnvironment.getDefaultEnvironment().side = MixinEnvironment.Side.CLIENT

		PrestidigitationData.createRegistry()
		PrestidigitationData.DEFERRED_REGISTER.register(MOD_BUS)

		MOD_BUS.addListener(HexicalActions::init)
		MOD_BUS.addListener(HexicalAdvancements::init)
		
		HexicalBlocks.init()
		HexicalData.init()
		HexicalEntities.init()
		HexicalServerEvents.init()
		
		MOD_BUS.addListener(HexicalIota::init)
		
		HexicalItems.init()
		HexicalParticles.init()
		HexicalPotions.init()
		
		MOD_BUS.addListener(HexicalRecipe::init)
		
		HexicalSounds.init()
		HexicalNetworking.serverInit()

		LesserSentinelState.register()
		
        var evBus = MinecraftForge.EVENT_BUS;
        evBus.addGenericListener(ItemStack::class.java, HexicalForgeCapabilityHandler::attachItemCaps);
        evBus.addGenericListener(Entity::class.java, HexicalForgeCapabilityHandler::attachEntityCaps);
	}

	companion object {
		const val MOD_ID: String = "hexical"
		val LOGGER: Logger = LogManager.getLogger(MOD_ID);

		@JvmField
		val RANDOM: RandomSource = RandomSource.create()
		const val EVOKE_DURATION: Int = 20

		@JvmStatic
		fun id(string: String) = ResourceLocation(MOD_ID, string)
	}
}