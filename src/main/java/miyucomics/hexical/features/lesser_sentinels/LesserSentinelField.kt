package miyucomics.hexical.features.lesser_sentinels

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.asDouble
import at.petrak.hexcasting.api.utils.putList
import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import miyucomics.hexical.misc.HexicalNetworking
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

class LesserSentinelField : PlayerField {
	var instances: HashMap<ResourceKey<Level>, DimensionalLesserSentinelInstance> = hashMapOf()

	override fun readNbt(compound: CompoundTag) {
		instances.clear()
		if (!compound.contains("lesser_sentinels"))
			return
		compound.getList("lesser_sentinels", Tag.TAG_COMPOUND.toInt()).forEach {
			val instance = DimensionalLesserSentinelInstance.createFromNbt(it.asCompound)
			instances[instance.dimension] = instance
		}
	}

	override fun writeNbt(compound: CompoundTag) {
		compound.putList("lesser_sentinels", ListTag().also {
			instances.values.forEach { instance -> it.add(instance.toNbt()) }
		})
	}

	fun getCurrentInstance(player: ServerPlayer) = instances.getOrPut(player.serverLevel().dimension()) { DimensionalLesserSentinelInstance(mutableListOf(), player.serverLevel().dimension()) }
}

data class DimensionalLesserSentinelInstance(var lesserSentinels: MutableList<Vec3>, val dimension: ResourceKey<Level>) {
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
		fun createFromNbt(compound: CompoundTag): DimensionalLesserSentinelInstance {
			val lesserSentinels = mutableListOf<Vec3>()
			val positions = compound.getList("positional", Tag.TAG_DOUBLE.toInt()).toMutableList()
			while (positions.isNotEmpty())
				lesserSentinels.add(Vec3(positions.removeFirst().asDouble, positions.removeFirst().asDouble, positions.removeFirst().asDouble))
			return DimensionalLesserSentinelInstance(lesserSentinels, ResourceKey.create(Registries.DIMENSION, ResourceLocation(compound.getString("dimension"))))
		}
	}
}

var ServerPlayer.currentLesserSentinels: MutableList<Vec3>
	get() = this.getHexicalPlayerManager().get(LesserSentinelField::class).getCurrentInstance(this).lesserSentinels
	set(sentinels) { this.getHexicalPlayerManager().get(LesserSentinelField::class).getCurrentInstance(this).lesserSentinels = sentinels }
fun ServerPlayer.syncLesserSentinels() {
	val instance = this.currentLesserSentinels
	HexicalNetworking.sendToPlayer(this, ServerLesserSentinelPusher.LesserSentinelPacket(instance.size, instance))
}