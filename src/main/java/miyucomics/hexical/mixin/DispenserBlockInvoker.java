package miyucomics.hexical.mixin;

import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DispenserBlock.class)
public interface DispenserBlockInvoker {
	@Invoker("getDispenseMethod")
	DispenseItemBehavior invokeGetBehaviorForItem(ItemStack item);
}