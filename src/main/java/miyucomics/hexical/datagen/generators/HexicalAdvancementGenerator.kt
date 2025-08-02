package miyucomics.hexical.datagen.generators

import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.common.lib.HexItems
import miyucomics.hexical.inits.*
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.FrameType
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraftforge.common.data.ForgeAdvancementProvider
import net.minecraftforge.common.data.ExistingFileHelper
import java.util.function.Consumer
import java.util.concurrent.CompletableFuture

class HexicalAdvancementGenerator() : ForgeAdvancementProvider.AdvancementGenerator {
    override fun generate(registries: HolderLookup.Provider, consumer: Consumer<Advancement>, existingFileHelper: ExistingFileHelper) {
		val root = Advancement.Builder.advancement()
			.display(ItemStack(HexicalItems.CURIO_COMPASS.get()),
				Component.translatable("advancement.hexical:root.title"),
				Component.translatable("advancement.hexical:root.description"),
				ResourceLocation("minecraft", "textures/block/blackstone.png"),
				FrameType.TASK, true, true, true)
			.addCriterion("start_hexcasting", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(HexTags.Items.GRANTS_ROOT_ADVANCEMENT).build()))
			.save(consumer, "hexical:root")

		fun registerAdvancement(name: String, frame: FrameType, icon: Item, hidden: Boolean, condition: CriterionTriggerInstance, parent: Advancement = root): Advancement {
			return Advancement.Builder.advancement()
				.parent(parent)
				.display(ItemStack(icon),
					Component.translatable("advancements.hexical.$name.title"),
					Component.translatable("advancements.hexical.$name.description"),
					null, frame, true, true, hidden
				)
				.addCriterion(name, condition)
				.save(consumer, "hexical:$name")
		}

		registerAdvancement("conjure_cake", FrameType.CHALLENGE, Items.CAKE, true, ConjureCakeCriterion.Condition())
		registerAdvancement("conjure_hexxy", FrameType.CHALLENGE, HexItems.SCRYING_LENS, true, HexxyCriterion.Condition())
		registerAdvancement("hallucinate", FrameType.TASK, Items.WHITE_BANNER, false, HallucinateCriterion.Condition())
		registerAdvancement("diy_conjuring", FrameType.TASK, Items.SCAFFOLDING, false, DIYCriterion.Condition())
		registerAdvancement("specklike", FrameType.TASK, Items.BEACON, false, SpecklikeCriterion.Condition())

		val baseLamp = registerAdvancement("acquire_hand_lamp", FrameType.TASK, HexicalItems.HAND_LAMP_ITEM.get(), false, InventoryChangeTrigger.TriggerInstance.hasItems(HexicalItems.HAND_LAMP_ITEM.get()))
		registerAdvancement("educate_lamp", FrameType.TASK, Items.ENCHANTED_BOOK, false, EducateGenieCriterion.Condition(), baseLamp)
		registerAdvancement("reload_lamp", FrameType.TASK, Items.LIGHTNING_ROD, false, ReloadLampCriterion.Condition(), baseLamp)
		registerAdvancement("acquire_arch_lamp", FrameType.GOAL, HexicalItems.ARCH_LAMP_ITEM.get(), false, InventoryChangeTrigger.TriggerInstance.hasItems(HexicalItems.ARCH_LAMP_ITEM.get()), baseLamp)
	}
}