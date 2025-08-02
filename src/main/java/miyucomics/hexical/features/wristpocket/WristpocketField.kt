package miyucomics.hexical.features.wristpocket

import at.petrak.hexcasting.api.utils.serializeToNBT
import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag

class WristpocketField : PlayerField {
	var wristpocket: ItemStack = ItemStack.EMPTY

	override fun readNbt(compound: CompoundTag) {
		if (!compound.contains("wristpocket"))
			return
		wristpocket = ItemStack.of(compound.getCompound("wristpocket"))
	}

	override fun writeNbt(compound: CompoundTag) {
		compound.put("wristpocket", wristpocket.serializeToNBT())
	}

	override fun handleRespawn(new: Player, old: Player) {
		new.wristpocket = old.wristpocket
	}
}

var Player.wristpocket: ItemStack
	get() = this.getHexicalPlayerManager().get(WristpocketField::class).wristpocket
	set(stack) { this.getHexicalPlayerManager().get(WristpocketField::class).wristpocket = stack }