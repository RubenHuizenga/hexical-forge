package miyucomics.hexical.client

import at.petrak.hexcasting.api.casting.math.HexPattern
import net.minecraft.world.inventory.tooltip.TooltipComponent

data class AnimatedPatternTooltip(val color: Int, val pattern: HexPattern, val state: Int) : TooltipComponent