package miyucomics.hexical.inits

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.hex_candles.HexCandleBlock
import miyucomics.hexical.features.hex_candles.HexCandleBlockEntity
import miyucomics.hexical.features.hex_candles.HexCandleCakeBlock
import miyucomics.hexical.features.hex_candles.HexCandleCakeBlockEntity
import miyucomics.hexical.features.mage_blocks.MageBlock
import miyucomics.hexical.features.mage_blocks.MageBlockEntity
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.features.media_jar.MediaJarBlockEntity
import miyucomics.hexical.features.media_jar.MediaJarItem
import miyucomics.hexical.features.pedestal.PedestalBlock
import miyucomics.hexical.features.pedestal.PedestalBlockEntity
import net.minecraft.world.level.block.*
import com.mojang.blaze3d.vertex.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.SoundType
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.Minecraft
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

object HexicalBlocks {
	private val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HexicalMain.MOD_ID)
	private val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HexicalMain.MOD_ID)
	private val BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, HexicalMain.MOD_ID)

	val CONJURABLE_FLOWERS: TagKey<Block> = TagKey.create(Registries.BLOCK, HexicalMain.id("conjurable_flower"))

	val HEX_CANDLE_BLOCK: RegistryObject<HexCandleBlock> = BLOCKS.register("hex_candle") { HexCandleBlock() }
	val HEX_CANDLE_CAKE_BLOCK: RegistryObject<HexCandleCakeBlock> = BLOCKS.register("hex_candle_cake") { HexCandleCakeBlock() }

	val MAGE_BLOCK: RegistryObject<MageBlock> = BLOCKS.register("mage_block") { MageBlock() }
	val MEDIA_JAR_BLOCK: RegistryObject<MediaJarBlock> = BLOCKS.register("media_jar") { MediaJarBlock() }
	
	@JvmField
	val CASTING_CARPET: RegistryObject<CarpetBlock> = BLOCKS.register("casting_carpet") {
		CarpetBlock(Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.1f).sound(SoundType.WOOL).ignitedByLava())
	}
	val CASTING_CARPET_ITEM: RegistryObject<Item> = ITEMS.register("casting_carpet") {
		BlockItem(CASTING_CARPET.get(), Item.Properties())
	}

	@JvmField
	val SENTINEL_BED_BLOCK: RegistryObject<Block> = BLOCKS.register("sentinel_bed") {
		Block(Properties.copy(Blocks.DEEPSLATE_TILES).strength(4f, 6f))
	}

	val PERIWINKLE_FLOWER: RegistryObject<PinkPetalsBlock> = BLOCKS.register("periwinkle") {
		PinkPetalsBlock(Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.DESTROY))
	}
	val PERIWINKLE_FLOWER_ITEM: RegistryObject<Item> = ITEMS.register("periwinkle") {
		BlockItem(PERIWINKLE_FLOWER.get(), Item.Properties())
	}

	@JvmField
	val MEDIA_JAR_ITEM: RegistryObject<Item> = ITEMS.register("media_jar") { MediaJarItem() }
	val HEX_CANDLE_ITEM: RegistryObject<Item> = ITEMS.register("hex_candle") {
		BlockItem(HEX_CANDLE_BLOCK.get(), Item.Properties())
	}
	val SENTINEL_BED_ITEM: RegistryObject<Item> = ITEMS.register("sentinel_bed") {
		BlockItem(SENTINEL_BED_BLOCK.get(), Item.Properties())
	}

	val PEDESTAL_BLOCK: RegistryObject<PedestalBlock> = BLOCKS.register("pedestal") { PedestalBlock() }
	val PEDESTAL_ITEM: RegistryObject<Item> = ITEMS.register("pedestal") {
		BlockItem(PEDESTAL_BLOCK.get(), Item.Properties())
	}

  	val HEX_CANDLE_BLOCK_ENTITY: RegistryObject<BlockEntityType<HexCandleBlockEntity>> = 
		BLOCK_ENTITIES.register("hex_candle") {
			BlockEntityType.Builder.of(
				::HexCandleBlockEntity,
				HEX_CANDLE_BLOCK.get()
			).build(null)
		}
	val HEX_CANDLE_CAKE_BLOCK_ENTITY: RegistryObject<BlockEntityType<HexCandleCakeBlockEntity>> = 
		BLOCK_ENTITIES.register("hex_candle_cake") {
			BlockEntityType.Builder.of(
				::HexCandleCakeBlockEntity,
				HEX_CANDLE_CAKE_BLOCK.get()
			).build(null)
		}
	val MEDIA_JAR_BLOCK_ENTITY: RegistryObject<BlockEntityType<MediaJarBlockEntity>> = 
		BLOCK_ENTITIES.register("media_jar") {
			BlockEntityType.Builder.of(
				::MediaJarBlockEntity,
				MEDIA_JAR_BLOCK.get()
			).build(null)
		}
	val MAGE_BLOCK_ENTITY: RegistryObject<BlockEntityType<MageBlockEntity>> = 
		BLOCK_ENTITIES.register("mage_block") {
			BlockEntityType.Builder.of(
				::MageBlockEntity,
				MAGE_BLOCK.get()
			).build(null)
		}
	val PEDESTAL_BLOCK_ENTITY: RegistryObject<BlockEntityType<PedestalBlockEntity>> = 
		BLOCK_ENTITIES.register("pedestal") {
			BlockEntityType.Builder.of(
				::PedestalBlockEntity,
				PEDESTAL_BLOCK.get()
			).build(null)
		}

	fun init() {
		ITEMS.register("mage_block") {
			BlockItem(MAGE_BLOCK.get(), Item.Properties())
		}

		BLOCKS.register(MOD_BUS)
		ITEMS.register(MOD_BUS)
		BLOCK_ENTITIES.register(MOD_BUS)

		MOD_BUS.addListener(this::addCreative)
	}
	 
	@SubscribeEvent
	fun addCreative(event: BuildCreativeModeTabContentsEvent) {
		if (event.tabKey == HexicalItems.HEXICAL_GROUP.getKey()) {
			event.accept(MEDIA_JAR_ITEM)
			event.accept(HEX_CANDLE_ITEM)
			event.accept(CASTING_CARPET_ITEM)
			event.accept(SENTINEL_BED_ITEM)
			event.accept(PERIWINKLE_FLOWER_ITEM)
			event.accept(PEDESTAL_ITEM)
		}
	}

	fun clientInit() {
		// Set in the blockmodel json instead with "render_type": "minecraft:cutout"
		// BlockRenderLayerMap.INSTANCE.putBlock(PERIWINKLE_FLOWER, RenderLayer.getCutout())
	}
}