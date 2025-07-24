package miyucomics.hexical.registry

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.particles.*
import miyucomics.hexical.particles.ConfettiParticle.Factory
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import com.mojang.serialization.Codec

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object HexicalParticles {
	private val PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, HexicalMain.MOD_ID)

	val CONFETTI_PARTICLE: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("confetti") {
        SimpleParticleType(true)
    }
   
    val CUBE_PARTICLE: RegistryObject<ParticleType<CubeParticleEffect>> = PARTICLE_TYPES.register("cube") { 
		CubeParticleEffect.Type
	}

    val SPARKLE_PARTICLE: RegistryObject<ParticleType<SparkleParticleEffect>> = PARTICLE_TYPES.register("sparkle") {
        SparkleParticleEffect.Type
	}

	fun init() {
		PARTICLE_TYPES.register(MOD_BUS)
	}

	fun clientInit() {
		Minecraft.getInstance().particleEngine.register(CONFETTI_PARTICLE.get()) { sprite -> ConfettiParticle.Factory(sprite) }
		Minecraft.getInstance().particleEngine.register(CUBE_PARTICLE.get()) { sprite -> CubeParticle.Factory(sprite) }
		Minecraft.getInstance().particleEngine.register(SPARKLE_PARTICLE.get()) { sprite -> SparkleParticle.Factory(sprite) }
	}
}