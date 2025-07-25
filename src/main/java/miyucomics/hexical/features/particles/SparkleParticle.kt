package miyucomics.hexical.features.particles

import com.mojang.brigadier.StringReader
import miyucomics.hexical.inits.HexicalParticles
import net.minecraft.client.particle.*
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.core.particles.DustParticleOptionsBase
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import org.joml.Vector3f
import java.util.*
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

class SparkleParticle(world: ClientLevel, x: Double, y: Double, z: Double, velocityX: Double, velocityY: Double, velocityZ: Double, provider: SpriteSet) : TextureSheetParticle(world, x, y, z, velocityX, velocityY, velocityZ) {
	private val spriteProvider: SpriteSet

	init {
		this.lifetime = 20
		this.xd = 0.0
		this.yd = 0.0
		this.zd = 0.0
		this.spriteProvider = provider
		this.pickSprite(provider)
		this.setSpriteFromAge(provider)
		this.scale(2.5f)
	}

	override fun tick() {
		super.tick()
		this.setSpriteFromAge(this.spriteProvider)
	}

	public override fun getLightColor(tint: Float): Int {
		val i = super.getLightColor(tint)
		val k = i shr 16 and 0xFF
		return 240 or (k shl 16)
	}

	override fun getRenderType(): ParticleRenderType {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
	}

	@JvmRecord
	data class Factory(val spriteProvider: SpriteSet) : ParticleProvider<SparkleParticleEffect> {
		override fun createParticle(effect: SparkleParticleEffect, world: ClientLevel, d: Double, e: Double, f: Double, g: Double, h: Double, i: Double): Particle {
			val sparkleParticle = SparkleParticle(world, d, e, f, g, h, i, this.spriteProvider)
			sparkleParticle.setColor(effect.color.x, effect.color.y, effect.color.z)
			sparkleParticle.setLifetime(effect.lifespan)
			return sparkleParticle
		}
	}
}

class SparkleParticleEffect(val color: Vector3f, val lifespan: Int) : ParticleOptions {
	object Factory : ParticleOptions.Deserializer<SparkleParticleEffect> {
		override fun fromNetwork(type: ParticleType<SparkleParticleEffect>, buf: FriendlyByteBuf): SparkleParticleEffect {
			return SparkleParticleEffect(DustParticleOptionsBase.readVector3f(buf), buf.readInt())
		}

		override fun fromCommand(particleType: ParticleType<SparkleParticleEffect>, stringReader: StringReader): SparkleParticleEffect {
			val color = DustParticleOptionsBase.readVector3f(stringReader)
			stringReader.expect(' ')
			val lifespan = stringReader.readInt()
			return SparkleParticleEffect(color, lifespan)
		}
	}

	override fun getType() = HexicalParticles.SPARKLE_PARTICLE.get()
	override fun writeToString() = String.format(Locale.ROOT, "sparkle_particle %.2f %.2f %.2f", color.x(), color.y(), color.z())

	override fun writeToNetwork(packet: FriendlyByteBuf) {
		packet.writeFloat(color.x())
		packet.writeFloat(color.y())
		packet.writeFloat(color.z())
		packet.writeInt(lifespan)
	}

	object Type : ParticleType<SparkleParticleEffect>(false, SparkleParticleEffect.Factory) {
		override fun codec(): Codec<SparkleParticleEffect> { 
			return CODEC
		}

		val CODEC: Codec<SparkleParticleEffect> = RecordCodecBuilder.create { instance ->
			instance.group(
				Codec.FLOAT.fieldOf("r").forGetter { it.color.x() },
				Codec.FLOAT.fieldOf("g").forGetter { it.color.y() },
				Codec.FLOAT.fieldOf("b").forGetter { it.color.z() },
				Codec.INT.fieldOf("lifespan").forGetter { it.lifespan }
			).apply(instance) { r, g, b, lifespan -> 
				SparkleParticleEffect(Vector3f(r, g, b), lifespan)
			}
		}
	}
}