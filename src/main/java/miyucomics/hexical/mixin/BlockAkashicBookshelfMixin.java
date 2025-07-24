package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicBookshelf;
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf;
import miyucomics.hexical.registry.HexicalSounds;
import miyucomics.hexical.utils.CastingUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.sounds.SoundSource.BLOCKS;

@Mixin(BlockAkashicBookshelf.class)
public class BlockAkashicBookshelfMixin {
	@Inject(method = "use", at = @At("TAIL"))
	private void copyIota(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		if (world.isClientSide() || player.isShiftKeyDown() || hand == InteractionHand.OFF_HAND || !player.getMainHandItem().isEmpty())
			return;

		BlockEntity shelf = world.getBlockEntity(pos);
		if (!(shelf instanceof BlockEntityAkashicBookshelf))
			return;

		CompoundTag nbt = ((BlockEntityAkashicBookshelf) shelf).getIotaTag();
		if (nbt == null)
			return;

		CastingUtils.giveIota((ServerPlayer) player, IotaType.deserialize(nbt, (ServerLevel) world));
		world.playSound(null, pos, HexicalSounds.SUDDEN_REALIZATION.get(), BLOCKS, 1f, 1f);
		player.swing(hand, true);
	}
}