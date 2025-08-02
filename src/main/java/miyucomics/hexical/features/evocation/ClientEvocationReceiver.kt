package miyucomics.hexical.features.evocation

import miyucomics.hexical.misc.InitHook

object ClientEvocationReceiver : InitHook() {
	override fun init() {
		// Done in the handler of the EvocationStatePacket in ServerEvocationManager
		// ClientPlayNetworking.registerGlobalReceiver(ServerEvocationManager.START_EVOKE_CHANNEL) { client, _, packet, _ ->
		// 	val player = client.world!!.getPlayerByUuid(packet.readUuid()) ?: return@registerGlobalReceiver
		// 	player.evocationActive = true
		// }

		// ClientPlayNetworking.registerGlobalReceiver(ServerEvocationManager.END_EVOKING_CHANNEL) { client, _, packet, _ ->
		// 	val player = client.world!!.getPlayerByUuid(packet.readUuid()) ?: return@registerGlobalReceiver
		// 	player.evocationActive = false
		// }
	}
}