package miyucomics.hexical.client

import miyucomics.hexical.data.LedgerInstance
import net.minecraft.world.phys.Vec3

object ClientStorage {
	@JvmField
	var ticks: Int = 0
	var ledger: LedgerInstance = LedgerInstance()
	var lesserSentinels: MutableList<Vec3> = mutableListOf()
}