package miyucomics.hexical.data

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.asDouble
import at.petrak.hexcasting.api.utils.putList
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.client.ClientStorage
import miyucomics.hexical.interfaces.PlayerEntityMinterface
import miyucomics.hexical.registry.HexicalNetworking
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import net.minecraft.world.entity.player.Player
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.event.entity.player.PlayerEvent
import java.util.function.Supplier

class LesserSentinelState {
	var instances: HashMap<ResourceKey<Level>, LesserSentinelsInstance> = hashMapOf()

	fun toNbt(): ListTag {
		val list = ListTag()
		instances.values.forEach { list.add(it.toNbt()) }
		return list
	}

	fun getCurrentInstance(player: ServerPlayer) = instances.getOrPut(player.serverLevel().dimension()) { LesserSentinelsInstance(mutableListOf(), player.serverLevel().dimension()) }

	fun syncToClient(player: ServerPlayer) {
		val positions = getCurrentInstance(player).lesserSentinels.toList()
		SentinelNetworking.syncToClient(player, positions)
	}

	companion object {
		@JvmStatic
		fun createFromNbt(list: ListTag): LesserSentinelState {
            val state = LesserSentinelState()
            list.forEach {
                val instance = LesserSentinelsInstance.createFromNbt(it as CompoundTag)
                state.instances[instance.dimension] = instance
            }
            return state
        }

        fun register() {
            SentinelNetworking.register()
        }
	}
}

data class LesserSentinelsInstance(var lesserSentinels: MutableList<Vec3>, val dimension: ResourceKey<Level>) {
	fun toNbt(): CompoundTag {
		val compound = CompoundTag()

		val location = ListTag()
		lesserSentinels.forEach { pos ->
			location.add(DoubleTag.valueOf(pos.x))
			location.add(DoubleTag.valueOf(pos.y))
			location.add(DoubleTag.valueOf(pos.z))
		}

		compound.putString("dimension", dimension.location().toString())
		compound.putList("positional", location)
		return compound
	}

	companion object {
		fun createFromNbt(compound: CompoundTag): LesserSentinelsInstance {
			val lesserSentinels = mutableListOf<Vec3>()
			compound.getList("positional", Tag.TAG_COMPOUND.toInt()).windowed(3, 3, false) { 
				lesserSentinels.add(Vec3(it[0].asDouble, it[1].asDouble, it[2].asDouble)) 
			}
			return LesserSentinelsInstance(lesserSentinels, ResourceKey.create(Registries.DIMENSION, ResourceLocation(compound.getString("dimension"))))
		}
	}
}

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID)
object SentinelNetworking {
    private const val PROTOCOL_VERSION = "1"
    val CHANNEL: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(HexicalMain.MOD_ID, "lesser_sentinel"),
        { PROTOCOL_VERSION },
        { it == PROTOCOL_VERSION },
        { it == PROTOCOL_VERSION }
    )

	fun register() {
		CHANNEL.messageBuilder<LesserSentinelSyncPacket>(LesserSentinelSyncPacket::class.java, 0)
		.encoder(LesserSentinelSyncPacket::encode)
		.decoder(LesserSentinelSyncPacket::decode)
		.consumerMainThread(LesserSentinelSyncPacket::handle)
		.add()
	}

	private class LesserSentinelSyncPacket(val positions: List<Vec3>) {
		companion object {
			fun encode(packet: LesserSentinelSyncPacket, buffer: FriendlyByteBuf) {
				buffer.writeInt(packet.positions.size)
				packet.positions.forEach { vec ->
					buffer.writeDouble(vec.x)
					buffer.writeDouble(vec.y)
					buffer.writeDouble(vec.z)
				}
			}

			fun decode(buffer: FriendlyByteBuf): LesserSentinelSyncPacket {
				val count = buffer.readInt()
				val positions = MutableList(count) {
					Vec3(
						buffer.readDouble(),
						buffer.readDouble(),
						buffer.readDouble()
					)
				}
				return LesserSentinelSyncPacket(positions)
			}

			fun handle(packet: LesserSentinelSyncPacket, ctx: Supplier<NetworkEvent.Context>) {
				ctx.get().enqueueWork {
					ClientStorage.lesserSentinels.clear()
					ClientStorage.lesserSentinels.addAll(packet.positions)
				}
				ctx.get().packetHandled = true
			}
		}
	}

	fun syncToClient(player: ServerPlayer, positions: List<Vec3>) {
		CHANNEL.send(
			PacketDistributor.PLAYER.with { player },
			LesserSentinelSyncPacket(positions)
		)
	}

    @SubscribeEvent
    fun onDimensionChange(event: PlayerEvent.PlayerChangedDimensionEvent) {
        val player = event.entity as? ServerPlayer ?: return
        if (player is PlayerEntityMinterface) {
            player.getLesserSentinels().syncToClient(player)
        }
    }
}