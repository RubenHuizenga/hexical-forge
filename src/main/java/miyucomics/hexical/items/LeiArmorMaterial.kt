package miyucomics.hexical.items

import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents

class LeiArmorMaterial : ArmorMaterial {
	override fun getDurabilityForType(type: ArmorItem.Type) = 0
	override fun getDefenseForType(type: ArmorItem.Type) = 0
	override fun getEnchantmentValue() = 100
	override fun getEquipSound(): SoundEvent = SoundEvents.AMETHYST_BLOCK_CHIME
	override fun getRepairIngredient(): Ingredient = Ingredient.EMPTY
	override fun getName() = "lei"
	override fun getToughness() = 0f
	override fun getKnockbackResistance() = 0f

	companion object {
		val INSTANCE: LeiArmorMaterial = LeiArmorMaterial()
	}
}