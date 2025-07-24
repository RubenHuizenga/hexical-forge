package miyucomics.hexical.registry

import at.petrak.hexcasting.api.casting.asActionResult
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.Vec3
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkDirection
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.casting.environments.CharmedItemCastEnv
import miyucomics.hexical.utils.CharmedItemUtilities
import miyucomics.hexical.utils.CastingUtils
import miyucomics.hexical.data.KeybindData
import miyucomics.hexical.data.EvokeState
import miyucomics.hexical.data.LedgerInstance
import miyucomics.hexical.data.LedgerData
import miyucomics.hexical.client.ShaderRenderer
import miyucomics.hexical.client.ClientStorage
import miyucomics.hexical.client.PlayerAnimations
import java.util.function.BiConsumer
import java.util.function.Supplier
import java.util.UUID
import java.util.Random
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer

object HexicalNetworking {
	private const val PROTOCOL_VERSION = "1"
    private lateinit var channel: SimpleChannel

	fun serverInit() {
		channel = NetworkRegistry.newSimpleChannel(
			HexicalMain.id("main"),
			{ PROTOCOL_VERSION },
			{ it == PROTOCOL_VERSION },
			{ it == PROTOCOL_VERSION }
		)

		var id = 0
		channel.messageBuilder<CharmedItemUsePacket>(CharmedItemUsePacket::class.java, id++)
        	.encoder(CharmedItemUsePacket::encode)
            .decoder(CharmedItemUsePacket::decode)
            .consumerMainThread(CharmedItemUsePacket::handle)
            .add()

        channel.messageBuilder<KeyPressPacket>(KeyPressPacket::class.java, id++)
            .encoder(KeyPressPacket::encode)
            .decoder(KeyPressPacket::decode)
            .consumerMainThread(KeyPressPacket::handle)
            .add()
		
        channel.messageBuilder<ScrollPacket>(ScrollPacket::class.java, id++)
            .encoder(ScrollPacket::encode)
            .decoder(ScrollPacket::decode)
            .consumerMainThread(ScrollPacket::handle)
            .add()

        channel.messageBuilder<EvokeStatePacket>(EvokeStatePacket::class.java, id++)
            .encoder(EvokeStatePacket::encode)
            .decoder(EvokeStatePacket::decode)
            .consumerMainThread(EvokeStatePacket::handle)
            .add()

        channel.messageBuilder<LedgerPacket>(LedgerPacket::class.java, id++)
            .encoder(LedgerPacket::encode)
            .decoder(LedgerPacket::decode)
            .consumerMainThread(LedgerPacket::handle)
            .add()

        channel.messageBuilder<ShaderPacket>(ShaderPacket::class.java, id++)
            .encoder(ShaderPacket::encode)
            .decoder(ShaderPacket::decode)
            .consumerMainThread(ShaderPacket::handle)
            .add()

        channel.messageBuilder<ConfettiPacket>(ConfettiPacket::class.java, id++)
            .encoder(ConfettiPacket::encode)
            .decoder(ConfettiPacket::decode)
            .consumerMainThread(ConfettiPacket::handle)
            .add()
	}

	@JvmStatic
	fun sendToServer(packet: Any) {
        channel.sendToServer(packet)
    }

    fun sendToPlayer(player: ServerPlayer, packet: Any) {
        channel.send(PacketDistributor.PLAYER.with { player }, packet)
    }

    fun sendToAllPlayers(packet: Any) {
        channel.send(PacketDistributor.ALL.noArg(), packet)
    }

	class CharmedItemUsePacket(
		val inputMethod: Int,
		val hand: InteractionHand
	) {
		companion object {
			fun encode(packet: CharmedItemUsePacket, buffer: FriendlyByteBuf) {
				buffer.writeInt(packet.inputMethod)
				buffer.writeEnum(packet.hand)
			}

			fun decode(buffer: FriendlyByteBuf): CharmedItemUsePacket {
				return CharmedItemUsePacket(
					buffer.readInt(),
					buffer.readEnum(InteractionHand::class.java)
				)
			}

			fun handle(packet: CharmedItemUsePacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					val player = ctx.get().sender ?: return@enqueueWork
					val stack = player.getItemInHand(packet.hand)
					val vm = CastingVM(
						CastingImage().copy(stack = packet.inputMethod.asActionResult),
						CharmedItemCastEnv(player, packet.hand, stack)
					)
					vm.queueExecuteAndWrapIotas(
						CharmedItemUtilities.getHex(stack, player.serverLevel()),
						player.serverLevel()
					)
				}
				ctx.get().packetHandled = true
			}
		}
	}

	class KeyPressPacket(val key: String, val pressed: Boolean) {
		companion object {
			fun encode(packet: KeyPressPacket, buffer: FriendlyByteBuf) {
				buffer.writeUtf(packet.key)
				buffer.writeBoolean(packet.pressed)
			}

			fun decode(buffer: FriendlyByteBuf): KeyPressPacket {
				return KeyPressPacket(buffer.readUtf(), buffer.readBoolean())
			}
		
			fun handle(packet: KeyPressPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					val player = ctx.get().sender ?: return@enqueueWork
					val uuid = player.uuid

					if (!KeybindData.active.containsKey(uuid)) {
						KeybindData.active[uuid] = HashMap()
						KeybindData.duration[uuid] = HashMap()
					}

					if (packet.pressed) {
						KeybindData.active[uuid]!![packet.key] = true
						KeybindData.duration[uuid]!![packet.key] = 0
						if (packet.key == "key.hexical.telepathy") {
							KeybindData.scroll[uuid] = 0
						}
					} else {
						KeybindData.active[uuid]!![packet.key] = false
						KeybindData.duration[uuid]!![packet.key] = 0
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}

	class ScrollPacket(val delta: Int) {
		companion object {
			fun encode(packet: ScrollPacket, buffer: FriendlyByteBuf) {
				buffer.writeInt(packet.delta)
			}

			fun decode(buffer: FriendlyByteBuf): ScrollPacket {
				return ScrollPacket(buffer.readInt())
			}
			
			fun handle(packet: ScrollPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					val player = ctx.get().sender ?: return@enqueueWork
					val uuid = player.uuid
					KeybindData.scroll[uuid] = KeybindData.scroll.getOrDefault(uuid, 0) + packet.delta
				}
				ctx.get().packetHandled = true
			}
		}
	}

	class EvokeStatePacket(val uuid: UUID, val start: Boolean) {
		companion object {
			fun encode(packet: EvokeStatePacket, buffer: FriendlyByteBuf) {
				buffer.writeUUID(packet.uuid)
				buffer.writeBoolean(packet.start)
			}

			fun decode(buffer: FriendlyByteBuf): EvokeStatePacket {
				return EvokeStatePacket(buffer.readUUID(), buffer.readBoolean())
			}
		
			fun handle(packet: EvokeStatePacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					if (ctx.get().direction == NetworkDirection.PLAY_TO_CLIENT) {
						// Client-side handling
						val player = Minecraft.getInstance().level?.getPlayerByUUID(packet.uuid) ?: return@enqueueWork
						val container = (player as PlayerAnimations).hexicalModAnimations()
						
						if (packet.start) {
							container.setAnimation(KeyframeAnimationPlayer(
								PlayerAnimationRegistry.getAnimation(HexicalMain.id("cast_loop"))!!
							))
							EvokeState.active[packet.uuid] = true
						} else {
							container.setAnimation(KeyframeAnimationPlayer(
								PlayerAnimationRegistry.getAnimation(HexicalMain.id("cast_end"))!!
							))
							EvokeState.active[packet.uuid] = false
						}
					} else {
						// Server-side handling
						val player = ctx.get().sender ?: return@enqueueWork
						if (!CastingUtils.isEnlightened(player)) return@enqueueWork
						
						if (packet.start) {
							EvokeState.active[player.uuid] = true
							EvokeState.duration[player.uuid] = HexicalMain.EVOKE_DURATION
							player.level().playSound(null, player.x, player.y, player.z, HexicalSounds.EVOKING_MURMUR.get(), SoundSource.PLAYERS, 1f, 1f)
							HexicalNetworking.sendToAllPlayers(EvokeStatePacket(player.uuid, true))
						} else {
							EvokeState.active[player.uuid] = false
							HexicalNetworking.sendToAllPlayers(EvokeStatePacket(player.uuid, false))
						}
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}

	class LedgerPacket(val nbt: CompoundTag?) {
		companion object {
			fun encode(packet: LedgerPacket, buffer: FriendlyByteBuf) {
				buffer.writeNbt(packet.nbt)
			}

			fun decode(buffer: FriendlyByteBuf): LedgerPacket {
				return LedgerPacket(buffer.readNbt())
			}
	
			fun handle(packet: LedgerPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					when (ctx.get().direction) {
						NetworkDirection.PLAY_TO_CLIENT -> {
							// Client-side handling
							ClientStorage.ledger = LedgerInstance.createFromNbt(packet.nbt!!)
						}
						NetworkDirection.PLAY_TO_SERVER -> {
							// Server-side handling
							val player = ctx.get().sender ?: return@enqueueWork
							LedgerData.clearLedger(player)
						}
						else -> {}
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}

	class ShaderPacket(val shader: ResourceLocation?) {
		companion object {
			fun encode(packet: ShaderPacket, buffer: FriendlyByteBuf) {
				buffer.writeUtf(packet.shader?.toString() ?: "null")
			}

			fun decode(buffer: FriendlyByteBuf): ShaderPacket {
				val str = buffer.readUtf()
				return ShaderPacket(if (str == "null") null else ResourceLocation(str))
			}
		
			fun handle(packet: ShaderPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					if (ctx.get().direction == NetworkDirection.PLAY_TO_CLIENT) {
						ShaderRenderer.setEffect(packet.shader)
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}

	class ConfettiPacket(
		val pos: Vec3,
		val dir: Vec3,
		val speed: Double,
		val seed: Long
	) {
		companion object {
			fun encode(packet: ConfettiPacket, buffer: FriendlyByteBuf) {
				HexicalMain.LOGGER.debug("encode confetti packet")
				buffer.writeDouble(packet.pos.x)
				buffer.writeDouble(packet.pos.y)
				buffer.writeDouble(packet.pos.z)
				buffer.writeDouble(packet.dir.x)
				buffer.writeDouble(packet.dir.y)
				buffer.writeDouble(packet.dir.z)
				buffer.writeDouble(packet.speed)
				buffer.writeLong(packet.seed)
			}

			fun decode(buffer: FriendlyByteBuf): ConfettiPacket {
				HexicalMain.LOGGER.debug("decode confetti packet")
				return ConfettiPacket(
					Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
					Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
					buffer.readDouble(),
					buffer.readLong()
				)
			}

			fun handle(packet: ConfettiPacket, ctx: Supplier<NetworkEvent.Context>) {
				HexicalMain.LOGGER.debug("handling confetti packet")
				
				ctx.get().enqueueWork {
					val level = Minecraft.getInstance().level ?: return@enqueueWork
					val random = Random(packet.seed)
					
					HexicalMain.LOGGER.debug("handling confetti packet equeued")
					HexicalMain.LOGGER.debug(packet.pos)
					HexicalMain.LOGGER.debug(packet.dir)
					HexicalMain.LOGGER.debug(packet.speed)
					HexicalMain.LOGGER.debug(packet.seed)
					HexicalMain.LOGGER.debug(level.isClientSide)

					level.playLocalSound(packet.pos.x, packet.pos.y, packet.pos.z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.MASTER, 10f, 10f, true)
					
					for (i in 0..99) {
						val alteredVelocity = if (packet.dir == Vec3.ZERO) {
							Vec3.directionFromRotation(
								random.nextFloat() * 180f - 90f,
								random.nextFloat() * 360f
							).multiply(packet.speed, packet.speed, packet.speed)
						} else {
							packet.dir.add(
								(random.nextDouble() * 2 - 1) / 5,
								(random.nextDouble() * 2 - 1) / 5,
								(random.nextDouble() * 2 - 1) / 5
							).scale((random.nextFloat() * 0.25 + 0.75) * packet.speed)
						}
						
						level.addParticle(
							HexicalParticles.CONFETTI_PARTICLE.get(),
							packet.pos.x, packet.pos.y, packet.pos.z,
							alteredVelocity.x, alteredVelocity.y, alteredVelocity.z
						)
					}
				}
				ctx.get().packetHandled = true
			}
		}
	}
}