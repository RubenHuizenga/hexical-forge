package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.utils.MediaHelper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@WrapMethod(method = "isBarVisible")
	public boolean addCharmedMediaDisplay(Operation<Boolean> original) {
		ItemStack stack = ((ItemStack) (Object) this);
		CompoundTag nbt = stack.getTag();
		if (nbt == null)
			return original.call();
		if (!nbt.contains("charmed"))
			return original.call();
		return true;
	}

	@WrapMethod(method = "getBarWidth")
	public int addCharmedMediaStep(Operation<Integer> original) {
		ItemStack stack = ((ItemStack) (Object) this);
		CompoundTag nbt = stack.getTag();
		if (nbt == null)
			return original.call();
		if (!nbt.contains("charmed"))
			return original.call();

		CompoundTag charm = nbt.getCompound("charmed");
		int maxMedia = charm.getInt("max_media");
		int media = charm.getInt("media");
		return MediaHelper.mediaBarWidth(media, maxMedia);
	}

	@WrapMethod(method = "getBarColor")
	public int addCharmedMediaColor(Operation<Integer> original) {
		ItemStack stack = ((ItemStack) (Object) this);
		CompoundTag nbt = stack.getTag();
		if (nbt == null)
			return original.call();
		if (!nbt.contains("charmed"))
			return original.call();
		return 0xff_e83d72;
	}
}