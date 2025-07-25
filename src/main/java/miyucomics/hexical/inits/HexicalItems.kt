package miyucomics.hexical.inits

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.items.ItemStaff
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.animated_scrolls.AnimatedScrollItem
import miyucomics.hexical.features.confection.HexburstItem
import miyucomics.hexical.features.confection.HextitoItem
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.features.grimoires.GrimoireItem
import miyucomics.hexical.features.lamps.ArchLampItem
import miyucomics.hexical.features.lamps.HandLampItem
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.features.media_log.MediaLogItem
import miyucomics.hexical.features.periwinkle.LeiItem
import miyucomics.hexical.features.scarabs.ScarabBeetleItem
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.*
import net.minecraftforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent

object HexicalItems {
	private val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HexicalMain.MOD_ID)
	private val HEXICAL_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HexicalMain.MOD_ID)
	val HEXICAL_GROUP: RegistryObject<CreativeModeTab> = HEXICAL_TAB.register("general") {
		CreativeModeTab.builder()
			.icon { ItemStack(CONJURED_COMPASS_ITEM.get()) }
			.title(Component.translatable("itemGroup.hexical.general"))
			.displayItems { _, output ->
				val handLamp = ItemStack(HAND_LAMP_ITEM.get())
				IXplatAbstractions.INSTANCE.findHexHolder(handLamp)!!.writeHex(listOf(), null, 32000 * MediaConstants.DUST_UNIT)
				output.accept(handLamp)

				val archLamp = ItemStack(ARCH_LAMP_ITEM.get())
				IXplatAbstractions.INSTANCE.findHexHolder(archLamp)!!.writeHex(listOf(), null, 32000 * MediaConstants.DUST_UNIT)
				output.accept(archLamp)

				output.accept(ItemStack(SMALL_ANIMATED_SCROLL_ITEM.get()))
				output.accept(ItemStack(MEDIUM_ANIMATED_SCROLL_ITEM.get()))
				output.accept(ItemStack(LARGE_ANIMATED_SCROLL_ITEM.get()))

				output.accept(ItemStack(HEX_GUMMY.get()))

				output.accept(ItemStack(GAUNTLET_STAFF.get()))
				output.accept(ItemStack(LIGHTNING_ROD_STAFF.get()))

				output.accept(ItemStack(MEDIA_LOG_ITEM.get()))

				output.accept(ItemStack(LEI.get()))
				output.accept(ItemStack(SCARAB_BEETLE_ITEM.get()))
				output.accept(ItemStack(GRIMOIRE_ITEM.get()))

				output.accept(HEXXY.get())
				output.accept(IRISSY.get())
				output.accept(PENTXXY.get())
				output.accept(QUADXXY.get())
				output.accept(THOTHY.get())
				output.accept(FLEXXY.get())
			}
			.build()
	}

	@JvmField
	val HAND_LAMP_ITEM = ITEMS.register("hand_lamp") { HandLampItem() }
	
	@JvmField
	val ARCH_LAMP_ITEM = ITEMS.register("arch_lamp") { ArchLampItem() }
	
	@JvmField
	val CONJURED_COMPASS_ITEM = ITEMS.register("conjured_compass") { ConjuredCompassItem() }
	
	@JvmField
	val GRIMOIRE_ITEM = ITEMS.register("grimoire") { GrimoireItem() }
	val SCARAB_BEETLE_ITEM = ITEMS.register("scarab_beetle") { ScarabBeetleItem() }
	val HEXBURST_ITEM = ITEMS.register("hexburst") { HexburstItem() }
	val HEXTITO_ITEM = ITEMS.register("hextito") { HextitoItem() }
	val SMALL_ANIMATED_SCROLL_ITEM = ITEMS.register("animated_scroll_small") { AnimatedScrollItem(1) }
	val MEDIUM_ANIMATED_SCROLL_ITEM = ITEMS.register("animated_scroll_medium") { AnimatedScrollItem(2) }
	val LARGE_ANIMATED_SCROLL_ITEM = ITEMS.register("animated_scroll_large") { AnimatedScrollItem(3) }
	val HEX_GUMMY = ITEMS.register("hex_gummy") {
		Item(Properties().food(
			FoodProperties.Builder()
				.nutrition(2)
				.saturationMod(0.5f)
				.alwaysEat()
				.fast()
				.build()
		))
	}
	val MEDIA_LOG_ITEM = ITEMS.register("media_log") { MediaLogItem() }
	
	val CURIO_NAMES: List<String> = listOf("bismuth", "clover", "compass", "conch", "cube", "flute", "handbell", "heart", "interlock", "key", "staff", "charm", "strange", "beauty", "truth", "up", "down")
	val CURIOS: List<Item> = CURIO_NAMES.map { registerItem("curio_$it", CurioItem.getCurioFromName(it)) }
	@JvmField val CURIO_COMPASS = CURIOS[CURIO_NAMES.indexOf("compass")]
	@JvmField val CURIO_FLUTE = CURIOS[CURIO_NAMES.indexOf("flute")]
	@JvmField val CURIO_HANDBELL = CURIOS[CURIO_NAMES.indexOf("handbell")]
	@JvmField val CURIO_STAFF = CURIOS[CURIO_NAMES.indexOf("staff")]

	@JvmField
	val LEI = ITEMS.register("lei") { LeiItem() }
	val GAUNTLET_STAFF = ITEMS.register("gauntlet_staff") { ItemStaff(Properties().stacksTo(1)) }
	val LIGHTNING_ROD_STAFF = ITEMS.register("lightning_rod_staff") { ItemStaff(Properties().stacksTo(1)) }
	val TCHOTCHKE_ITEM = ITEMS.register("tchotchke") { TchotchkeItem() }
	val HEXXY = ITEMS.register("plush_hexxy") { Item(Properties().stacksTo(1)) }
	val IRISSY = ITEMS.register("plush_irissy") { Item(Properties().stacksTo(1)) }
	val PENTXXY = ITEMS.register("plush_pentxxy") { Item(Properties().stacksTo(1)) }
	val QUADXXY = ITEMS.register("plush_quadxxy") { Item(Properties().stacksTo(1)) }
	val THOTHY = ITEMS.register("plush_thothy") { Item(Properties().stacksTo(1)) }
	val FLEXXY = ITEMS.register("plush_flexxy") { Item(Properties().stacksTo(1)) }

	@JvmStatic
	fun randomPlush(): ItemStack {
		val itemType = listOf(HEXXY, IRISSY, PENTXXY, QUADXXY, THOTHY, FLEXXY).random().get()
		return ItemStack(itemType)
	}

	fun init() {
		ITEMS.register(MOD_BUS)
		HEXICAL_TAB.register(MOD_BUS)
		registerItem("tchotchke", TchotchkeItem())
	}

	fun clientInit() {
		ArchLampItem.registerModelPredicate()
		ConjuredCompassItem.registerModelPredicate()
		ScarabBeetleItem.registerModelPredicate()
	}
}


class TchotchkeItem : ItemPackagedHex(Settings().maxCount(1)) {
	override fun canDrawMediaFromInventory(stack: ItemStack) = false
	override fun isItemBarVisible(stack: ItemStack) = false
	override fun canRecharge(stack: ItemStack) = false
	override fun breakAfterDepletion() = true
	override fun cooldown() = 0

	override fun use(world: World, player: PlayerEntity, usedHand: Hand): TypedActionResult<ItemStack> {
		if (world.isClient)
			return TypedActionResult.success(player.getStackInHand(usedHand))
		val stack = player.getStackInHand(usedHand)
		if (hasHex(stack) && getMedia(stack) > 0) {
			val charmed = ItemStack(Items.STICK)
			val nbt = charmed.orCreateNbt
			val charm = NbtCompound()
			charm.putLong("media", getMedia(stack))
			charm.putLong("max_media", getMaxMedia(stack))
			charm.putList("hex", HexSerialization.serializeHex(getHex(stack, world as ServerWorld)!!))
			charm.putBoolean("left", true)
			charm.putBoolean("right", true)
			charm.putBoolean("left_sneak", true)
			charm.putBoolean("right_sneak", true)
			nbt.putCompound("charmed", charm)
			player.setStackInHand(usedHand, charmed)
		}
		return TypedActionResult.success(player.getStackInHand(usedHand))
	}

	override fun appendTooltip(stack: ItemStack, world: World?, lines: MutableList<Text>, advanced: TooltipContext) {
		lines.add(Text.literal("Right-click this item to get a charmed stick.").formatted(Formatting.RED))
	}
}