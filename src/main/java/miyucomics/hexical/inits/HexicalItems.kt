package miyucomics.hexical.inits

import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.items.ItemStaff
import at.petrak.hexcasting.xplat.IXplatAbstractions
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
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
import net.minecraft.server.level.ServerLevel
import net.minecraft.ChatFormatting
import net.minecraft.world.level.Level
import net.minecraft.world.entity.player.Player
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.nbt.CompoundTag

object HexicalItems {
	private val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HexicalMain.MOD_ID)
	private val HEXICAL_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HexicalMain.MOD_ID)
	val HEXICAL_GROUP: RegistryObject<CreativeModeTab> = HEXICAL_TAB.register("general") {
		CreativeModeTab.builder()
			.icon { ItemStack(CURIO_COMPASS.get()) }
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

				for (item in PLUSHIES)
					output.accept(item.get())
			}
			.build()
	}

	@JvmField
	val HAND_LAMP_ITEM = ITEMS.register("hand_lamp") { HandLampItem() }
	
	@JvmField
	val ARCH_LAMP_ITEM = ITEMS.register("arch_lamp") { ArchLampItem() }
	
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
	val CURIOS: List<RegistryObject<Item>> = CURIO_NAMES.map { ITEMS.register("curio_$it") { CurioItem.getCurioFromName(it) }}
	@JvmField val CURIO_COMPASS = CURIOS[CURIO_NAMES.indexOf("compass")]
	@JvmField val CURIO_FLUTE = CURIOS[CURIO_NAMES.indexOf("flute")]
	@JvmField val CURIO_HANDBELL = CURIOS[CURIO_NAMES.indexOf("handbell")]
	@JvmField val CURIO_STAFF = CURIOS[CURIO_NAMES.indexOf("staff")]

	@JvmField
	val LEI = ITEMS.register("lei") { LeiItem }
	val GAUNTLET_STAFF = ITEMS.register("gauntlet_staff") { ItemStaff(Properties().stacksTo(1)) }
	val LIGHTNING_ROD_STAFF = ITEMS.register("lightning_rod_staff") { ItemStaff(Properties().stacksTo(1)) }
	val TCHOTCHKE_ITEM = ITEMS.register("tchotchke") { TchotchkeItem() }

	val PLUSHIE_NAMES: List<String> = listOf("hexxy", "irissy", "pentxxy", "quadxxy", "thothy", "flexxy")
	val PLUSHIES: List<RegistryObject<Item>> = PLUSHIE_NAMES.map { ITEMS.register("plush_$it") { Item(Properties().stacksTo(1)) }}

	@JvmStatic
	fun randomPlush() = ItemStack(PLUSHIES.random().get())

	fun init() {
		ITEMS.register(MOD_BUS)
		HEXICAL_TAB.register(MOD_BUS)
	}
}

class TchotchkeItem : ItemPackagedHex(Properties().stacksTo(1)) {
	override fun canDrawMediaFromInventory(stack: ItemStack) = false
	override fun isBarVisible(stack: ItemStack) = false
	override fun canRecharge(stack: ItemStack) = false
	override fun breakAfterDepletion() = true
	override fun cooldown() = 0

	override fun use(world: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (world.isClientSide)
			return InteractionResultHolder.success(player.getItemInHand(usedHand))
		val stack = player.getItemInHand(usedHand)
		if (hasHex(stack) && getMedia(stack) > 0) {
			val charmed = ItemStack(Items.STICK)
			val nbt = charmed.orCreateTag
			val charm = CompoundTag()
			charm.putLong("media", getMedia(stack))
			charm.putLong("max_media", getMaxMedia(stack))
			charm.putList("hex", HexSerialization.serializeHex(getHex(stack, world as ServerLevel)!!))
			charm.putBoolean("left", true)
			charm.putBoolean("right", true)
			charm.putBoolean("left_sneak", true)
			charm.putBoolean("right_sneak", true)
			nbt.putCompound("charmed", charm)
			player.setItemInHand(usedHand, charmed)
		}
		return InteractionResultHolder.success(player.getItemInHand(usedHand))
	}

	override fun appendHoverText(stack: ItemStack, world: Level?, lines: MutableList<Component>, advanced: TooltipFlag) {
		lines.add(Component.literal("Right-click this item to get a charmed stick.").withStyle(ChatFormatting.RED))
	}
}