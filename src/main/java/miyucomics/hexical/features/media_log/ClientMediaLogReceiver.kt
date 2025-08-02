package miyucomics.hexical.features.media_log

import miyucomics.hexical.misc.ClientStorage
import miyucomics.hexical.misc.InitHook

object ClientMediaLogReceiver : InitHook() {
	override fun init() {
		// Done in the handle of the MediaLogPacket in MediaLogRenderer
		// ClientPlayNetworking.registerGlobalReceiver(MediaLogField.MEDIA_LOG_CHANNEL) { _, _, packet, _ ->
		// 	ClientStorage.mediaLog = MediaLogField().also { it.fromNbt(packet.readNbt()!!) }
		// }
	}
}