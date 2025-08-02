package miyucomics.hexical.mixin;

import miyucomics.hexical.features.player.PlayerEntityMinterface;
import miyucomics.hexical.features.player.PlayerManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerEntityMixin implements PlayerEntityMinterface {
	@Unique
	private final PlayerManager hexicalPlayerManager = new PlayerManager();

	@Inject(method = "tick", at = @At("TAIL"))
	void tick(CallbackInfo ci) {
		hexicalPlayerManager.tick((Player) (Object) this);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	void reaadPlayerData(CompoundTag compound, CallbackInfo ci) {
		hexicalPlayerManager.readNbt(compound);
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	void writePlayerData(CompoundTag compound, CallbackInfo ci) {
		hexicalPlayerManager.writeNbt(compound);
	}

	@Override
	public @NotNull PlayerManager getPlayerManager() {
		return hexicalPlayerManager;
	}
}