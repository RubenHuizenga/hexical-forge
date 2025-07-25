package miyucomics.hexical.features.periwinkle

import at.petrak.hexcasting.common.lib.HexAttributes
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand
import java.util.*

object LeiItem : ArmorItem(LeiArmorMaterial.INSTANCE, Type.HELMET, Properties()) {
	private var bakedAttributes: Multimap<Attribute, AttributeModifier>

	private val GRID_ZOOM: AttributeModifier = AttributeModifier(
		UUID.fromString("9794eabc-2eec-42ee-b10a-c7d1fcd3de74"),
		"Scrying Lens Zoom", 0.25, AttributeModifier.Operation.MULTIPLY_TOTAL
	)

	private val SCRY_SIGHT: AttributeModifier = AttributeModifier(
		UUID.fromString("d7e97f0c-3a57-4be6-aee2-7e4913b6dc1f"), 
		"Scrying Lens Sight", 1.0, AttributeModifier.Operation.ADDITION
	)

	init {
		val attributes = ImmutableMultimap.builder<Attribute, AttributeModifier>()
		attributes.put(HexAttributes.GRID_ZOOM, GRID_ZOOM)
		attributes.put(HexAttributes.SCRY_SIGHT, SCRY_SIGHT)
		bakedAttributes = attributes.build()
	}

	override fun interactLivingEntity(stack: ItemStack, player: Player, friend: LivingEntity, hand: InteractionHand): InteractionResult {
		if (friend is Player && friend.getItemBySlot(EquipmentSlot.HEAD).isEmpty) {
			friend.setItemSlot(EquipmentSlot.HEAD, stack.copy())
			stack.shrink(1)
			return InteractionResult.SUCCESS
		}
		return InteractionResult.PASS
	}

	override fun getDefaultAttributeModifiers(equipmentSlot: EquipmentSlot): Multimap<Attribute, AttributeModifier> {
		if (equipmentSlot == EquipmentSlot.HEAD)
			return bakedAttributes
		return super.getDefaultAttributeModifiers(equipmentSlot)
	}
}