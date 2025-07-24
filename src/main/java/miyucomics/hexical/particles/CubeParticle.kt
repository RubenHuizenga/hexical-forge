package miyucomics.hexical.particles

import com.mojang.brigadier.StringReader
import miyucomics.hexical.registry.HexicalParticles
import net.minecraft.client.particle.*
import net.minecraft.client.Camera
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LightTexture
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.core.particles.DustParticleOptionsBase
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f
import java.util.*
import kotlin.math.max
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

class CubeParticle(world: ClientLevel, x: Double, y: Double, z: Double) : TextureSheetParticle(world, x, y, z) {
	override fun render(vertexConsumer: VertexConsumer, camera: Camera, tickDelta: Float) {
		val cam = camera.position
		val centerX = (x - cam.x).toFloat()
		val centerY = (y - cam.y).toFloat()
		val centerZ = (z - cam.z).toFloat()
		val alpha = max(0f, (1f - (age + tickDelta) / lifetime.toFloat()))
		for (face in faces)
			renderFace(vertexConsumer, face, centerX, centerY, centerZ, alpha)
	}

	private fun renderFace(consumer: VertexConsumer, indices: IntArray, x: Float, y: Float, z: Float, alpha: Float) {
		val uvs = arrayOf(
			floatArrayOf(this.u1, this.v1),
			floatArrayOf(this.u1, this.v0),
			floatArrayOf(this.u0, this.v0),
			floatArrayOf(this.u0, this.v1)
		)

		for (i in indices.indices) {
			val pos = positions[indices[i]]
			val uv = uvs[i]
			consumer.vertex(x + pos.x, y + pos.y, z + pos.z).uv(uv[0], uv[1]).color(rCol, gCol, bCol, alpha).uv2(LightTexture.FULL_BRIGHT)
		}
	}

	override fun getRenderType(): ParticleRenderType {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
	}

	class Factory(private val spriteProvider: SpriteSet) : ParticleProvider<CubeParticleEffect> {
		override fun createParticle(effect: CubeParticleEffect, clientWorld: ClientLevel, d: Double, e: Double, f: Double, g: Double, h: Double, i: Double): Particle {
			val particle = CubeParticle(clientWorld, d, e, f)
			particle.pickSprite(this.spriteProvider)
			particle.setColor(effect.color.x, effect.color.y, effect.color.z)
			particle.setLifetime(effect.lifespan)
			return particle
		}
	}

	companion object {
		private const val SCALE = 0.501

		private val positions = arrayOf(
			Vec3( SCALE,  SCALE,  SCALE),
			Vec3( SCALE, -SCALE,  SCALE),
			Vec3(-SCALE, -SCALE,  SCALE),
			Vec3(-SCALE,  SCALE,  SCALE),
			Vec3( SCALE,  SCALE, -SCALE),
			Vec3( SCALE, -SCALE, -SCALE),
			Vec3(-SCALE, -SCALE, -SCALE),
			Vec3(-SCALE,  SCALE, -SCALE)
		)

		private val faces = arrayOf(
			intArrayOf(2, 1, 0, 3),
			intArrayOf(6, 7, 4, 5),
			intArrayOf(7, 3, 0, 4),
			intArrayOf(2, 6, 5, 1),
			intArrayOf(1, 5, 4, 0),
			intArrayOf(2, 3, 7, 6)
		)
	}
}

class CubeParticleEffect(val color: Vector3f, val lifespan: Int) : ParticleOptions {
	object Factory : ParticleOptions.Deserializer<CubeParticleEffect> {
		override fun fromNetwork(type: ParticleType<CubeParticleEffect>, buf: FriendlyByteBuf): CubeParticleEffect {
			return CubeParticleEffect(DustParticleOptionsBase.readVector3f(buf), buf.readInt())
		}

		override fun fromCommand(particleType: ParticleType<CubeParticleEffect>, stringReader: StringReader): CubeParticleEffect {
			val color = DustParticleOptionsBase.readVector3f(stringReader)
			stringReader.expect(' ')
			val lifespan = stringReader.readInt()
			return CubeParticleEffect(color, lifespan)
		}
	}

	override fun getType() = HexicalParticles.CUBE_PARTICLE.get()
	override fun writeToString() = String.format(Locale.ROOT, "cube_particle %.2f %.2f %.2f", color.x(), color.y(), color.z())

	override fun writeToNetwork(packet: FriendlyByteBuf) {
		packet.writeFloat(color.x())
		packet.writeFloat(color.y())
		packet.writeFloat(color.z())
		packet.writeInt(lifespan)
	}
		
	object Type : ParticleType<CubeParticleEffect>(false, CubeParticleEffect.Factory) {
		override fun codec(): Codec<CubeParticleEffect> { 
			return CODEC
		}

		val CODEC: Codec<CubeParticleEffect> = RecordCodecBuilder.create { instance ->
			instance.group(
				Codec.FLOAT.fieldOf("r").forGetter { it.color.x() },
				Codec.FLOAT.fieldOf("g").forGetter { it.color.y() },
				Codec.FLOAT.fieldOf("b").forGetter { it.color.z() },
				Codec.INT.fieldOf("lifespan").forGetter { it.lifespan }
			).apply(instance) { r, g, b, lifespan -> 
				CubeParticleEffect(Vector3f(r, g, b), lifespan)
			}
		}
	}
}