package miyucomics.hexical.data

import net.minecraft.server.level.ServerPlayer
import java.util.*

object LedgerData {
	private var ledgers = HashMap<UUID, LedgerInstance>()

	@JvmStatic
	fun getLedger(player: ServerPlayer) = ledgers.computeIfAbsent(player.uuid) { LedgerInstance() }
	fun clearLedger(player: ServerPlayer) {
		ledgers[player.uuid] = LedgerInstance()
	}
}