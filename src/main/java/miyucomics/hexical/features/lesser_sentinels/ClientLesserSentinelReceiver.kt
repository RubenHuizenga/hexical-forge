package miyucomics.hexical.features.lesser_sentinels

import miyucomics.hexical.misc.ClientStorage
import miyucomics.hexical.misc.InitHook
import net.minecraft.world.phys.Vec3

object ClientLesserSentinelReceiver : InitHook() {
	override fun init() {
		// Done in the handle of LesserSentinelPacket in ServerLesserSentinelPusher
		// ClientPlayNetworking.registerGlobalReceiver(ServerLesserSentinelPusher.LESSER_SENTINEL_CHANNEL) { client, _, packet, _ ->
		// 	val count = packet.readInt()
		// 	val list = mutableListOf<Vec3>()

		// 	repeat(count) {
		// 		val x = packet.readDouble()
		// 		val y = packet.readDouble()
		// 		val z = packet.readDouble()
		// 		list.add(Vec3(x, y, z))
		// 	}

		// 	client.execute {
		// 		ClientStorage.lesserSentinels.clear()
		// 		ClientStorage.lesserSentinels.addAll(list)
		// 	}
		// }
	}
}