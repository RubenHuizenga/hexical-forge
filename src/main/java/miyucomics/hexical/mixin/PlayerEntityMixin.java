package miyucomics.hexical.mixin;

import miyucomics.hexical.casting.patterns.evocation.OpSetEvocation;
import miyucomics.hexical.data.ArchLampState;
import miyucomics.hexical.data.EvokeState;
import miyucomics.hexical.data.LesserSentinelState;
import miyucomics.hexical.interfaces.PlayerEntityMinterface;
import miyucomics.hexical.utils.CastingUtils;
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

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(Player.class)
public class PlayerEntityMixin implements PlayerEntityMinterface {
	@Unique
	private boolean hexical$archLampCastedThisTick = false;
	@Unique
	private ArchLampState hexical$archLampState = new ArchLampState();
	@Unique
	private CompoundTag hexical$evocation = new CompoundTag();
	@Unique
	private ItemStack hexical$wristpocket = ItemStack.EMPTY;
	@Unique
	private LesserSentinelState hexical$lesserSentinels = new LesserSentinelState();

	@Inject(method = "tick", at = @At("TAIL"))
	void tick(CallbackInfo ci) {
		Player player = ((Player) (Object) this);

		if (player.level().isClientSide)
			return;

		if (EvokeState.isEvoking(player.getUUID()) && CastingUtils.isEnlightened((ServerPlayer) player))
			if (EvokeState.getDuration(player.getUUID()) == 0)
				OpSetEvocation.evoke((ServerPlayer) player);

		hexical$archLampCastedThisTick = false;
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	void reaadPlayerData(CompoundTag compound, CallbackInfo ci) {
		if (compound.contains("arch_lamp"))
			hexical$archLampState = ArchLampState.createFromNbt(compound.getCompound("arch_lamp"));

		if (compound.contains("evocation"))
			hexical$evocation = compound.getCompound("evocation");

		if (compound.contains("lesser_sentinels"))
			hexical$lesserSentinels = LesserSentinelState.createFromNbt(compound.getList("lesser_sentinels", Tag.TAG_COMPOUND));

		hexical$wristpocket = ItemStack.of(compound.getCompound("wristpocket"));
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	void writePlayerData(CompoundTag nbtCompound, CallbackInfo ci) {
		nbtCompound.put("arch_lamp", hexical$archLampState.toNbt());
		nbtCompound.put("lesser_sentinels", hexical$lesserSentinels.toNbt());

		if (hexical$evocation != null)
			nbtCompound.put("evocation", hexical$evocation);

		CompoundTag wristpocket = new CompoundTag();
		hexical$wristpocket.save(wristpocket);
		nbtCompound.put("wristpocket", wristpocket);
	}

	public boolean getArchLampCastedThisTick() {
		return hexical$archLampCastedThisTick;
	}

	@Override
	public void archLampCasted() {
		hexical$archLampCastedThisTick = true;
	}

	@Override
	public @NotNull ArchLampState getArchLampState() {
		return hexical$archLampState;
	}

	@Override
	public @NotNull ItemStack getWristpocket() {
		return hexical$wristpocket;
	}

	@Override
	public void setWristpocket(@NotNull ItemStack stack) {
		hexical$wristpocket = stack;
	}

	@Override
	public @NotNull CompoundTag getEvocation() {
		return hexical$evocation;
	}

	@Override
	public void setEvocation(@NotNull CompoundTag hex) {
		hexical$evocation = hex;
	}

	@Override
	public @NotNull LesserSentinelState getLesserSentinels() {
		return hexical$lesserSentinels;
	}

	@Override
	public void setLesserSentinels(@NotNull LesserSentinelState state) {
		hexical$lesserSentinels = state;
	}
}