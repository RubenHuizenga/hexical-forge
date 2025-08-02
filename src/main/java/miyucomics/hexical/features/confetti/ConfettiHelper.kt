package miyucomics.hexical.features.confetti

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.HexicalNetworking
import miyucomics.hexical.misc.HexicalNetworking.AbstractPacket
import miyucomics.hexical.misc.HexicalNetworking.SimpleChannelWrapper
import miyucomics.hexical.inits.HexicalParticles
import net.minecraft.server.level.ServerLevel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkDirection
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.client.Minecraft
import java.util.function.Supplier
import kotlin.random.Random

object ConfettiHelper {
	fun initNetworking(wrapper: SimpleChannelWrapper ) {
        wrapper.channel.messageBuilder<ConfettiPacket>(ConfettiPacket::class.java, wrapper.nextId())
            .encoder(ConfettiPacket::encode)
            .decoder(ConfettiPacket::decode)
            .consumerMainThread(ConfettiPacket::handle)
            .add()
	}

	fun spawn(world: ServerLevel, pos: Vec3, dir: Vec3, speed: Double) {
		world.players().forEach { player -> HexicalNetworking.sendToPlayer(player, ConfettiPacket(HexicalMain.RANDOM.nextLong(), pos, dir, speed)) }
	}

	class ConfettiPacket(
		val seed: Long,
		val pos: Vec3,
		val dir: Vec3,
		val speed: Double,
	) : AbstractPacket<ConfettiPacket>() {
		override val handler: PacketHandler<ConfettiPacket> = Companion

		companion object : PacketHandler<ConfettiPacket> {
			override fun encode(packet: ConfettiPacket, buffer: FriendlyByteBuf) {
				HexicalMain.LOGGER.debug("encode confetti packet")
				buffer.writeLong(packet.seed)
				buffer.writeDouble(packet.pos.x)
				buffer.writeDouble(packet.pos.y)
				buffer.writeDouble(packet.pos.z)
				buffer.writeDouble(packet.dir.x)
				buffer.writeDouble(packet.dir.y)
				buffer.writeDouble(packet.dir.z)
				buffer.writeDouble(packet.speed)
			}

			override fun decode(buffer: FriendlyByteBuf): ConfettiPacket {
				HexicalMain.LOGGER.debug("decode confetti packet")
				return ConfettiPacket(
					buffer.readLong(),
					Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
					Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
					buffer.readDouble()
				)
			}

			override fun handle(packet: ConfettiPacket, ctx: Supplier<NetworkEvent.Context>) {	
				ctx.get().enqueueWork {
					val client = Minecraft.getInstance() ?: return@enqueueWork
					val random = Random(packet.seed)

					client.level!!.playLocalSound(packet.pos.x, packet.pos.y, packet.pos.z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.MASTER, 1f, 1f, true)
					for (i in 0..99) {
						val alteredVelocity = if (packet.dir == Vec3.ZERO) {
							Vec3.directionFromRotation(random.nextFloat() * 180f - 90f, random.nextFloat() * 360f).scale(packet.speed)
						} else {
							packet.dir.add(
								(random.nextDouble() * 2 - 1) / 5,
								(random.nextDouble() * 2 - 1) / 5,
								(random.nextDouble() * 2 - 1) / 5
							).scale((random.nextFloat() * 0.25 + 0.75) * packet.speed)
						}
						client.level!!.addParticle(HexicalParticles.CONFETTI_PARTICLE.get(), packet.pos.x, packet.pos.y, packet.pos.z, alteredVelocity.x, alteredVelocity.y, alteredVelocity.z)
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}
}