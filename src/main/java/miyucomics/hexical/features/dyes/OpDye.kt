package miyucomics.hexical.features.dyes

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import miyucomics.hexical.casting.iotas.getDye
import miyucomics.hexical.casting.iotas.getTrueDye
import miyucomics.hexical.casting.mishaps.DyeableMishap
import miyucomics.hexical.data.DyeData
import miyucomics.hexical.features.specklikes.Specklike
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Shulker
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.DyeColor
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import java.util.*
import net.minecraft.world.level.block.state.BlockState

object OpDye : SpellAction {
	override val argc = 2
	private val cost = MediaConstants.DUST_UNIT / 8
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val dye = args.getDye(1, argc)
		when (args[0]) {
			is EntityIota -> {
				val entity = args.getEntity(0, argc)
				env.assertEntityInRange(entity)
				return when (entity) {
					is Cat -> {
						val trueDye = args.getTrueDye(1, argc)
						SpellAction.Result(CatSpell(entity, trueDye), cost, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
					}
					is Sheep -> {
						val trueDye = args.getTrueDye(1, argc)
						SpellAction.Result(SheepSpell(entity, trueDye), cost, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
					}
					is Shulker -> {
						val trueDye = args.getTrueDye(1, argc)
						SpellAction.Result(ShulkerSpell(entity, trueDye), cost, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
					}
					is Specklike -> {
						val trueDye = args.getTrueDye(1, argc)
						SpellAction.Result(SpecklikeSpell(entity, trueDye), cost, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
					}
					is Wolf -> {
						val trueDye = args.getTrueDye(1, argc)
						SpellAction.Result(WolfSpell(entity, trueDye), cost, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
					}
					is ItemEntity -> {
						when (val item = entity.item.item) {
							is BlockItem -> {
								if (DyeDataHook.isDyeable(item.block))
									SpellAction.Result(BlockItemSpell(entity, item.block, dye), cost, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
								else
									throw DyeableMishap(entity.position())
							}
							else -> {
								if (DyeDataHook.isDyeable(item))
									SpellAction.Result(ItemSpell(entity, item, dye), cost, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
								else
									throw DyeableMishap(entity.position())
							}
						}
					}
					else -> throw DyeableMishap(entity.position())
				}
			}
			is Vec3Iota -> {
				val position = args.getBlockPos(0, argc)
				env.assertPosInRange(position)
				val state = env.world.getBlockState(position)
				if (!DyeDataHook.isDyeable(state.block))
					throw DyeableMishap(position.getCenter())
				return SpellAction.Result(BlockSpell(position, state, dye), cost, listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0)))
			}
			else -> throw MishapInvalidIota.of(args[0], 1, "entity_or_vector")
		}
	}

	private data class BlockSpell(val position: BlockPos, val state: BlockState, val dye: String) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			when (state.block) {
				is CandleBlock -> env.world.setBlockAndUpdate(
					position,
					DyeDataHook.getNewBlock(state.block, dye)
						.setValue(CandleBlock.LIT, state.getValue(CandleBlock.LIT))
						.setValue(CandleBlock.CANDLES, state.getValue(CandleBlock.CANDLES))
				)
				is CandleCakeBlock -> env.world.setBlockAndUpdate(
					position,
					DyeDataHook.getNewBlock(state.block, dye)
						.setValue(CandleCakeBlock.LIT, state.getValue(CandleCakeBlock.LIT))
				)
				is GlazedTerracottaBlock -> env.world.setBlockAndUpdate(
					position,
					DyeDataHook.getNewBlock(state.block, dye)
						.setValue(GlazedTerracottaBlock.FACING, state.getValue(GlazedTerracottaBlock.FACING))
				)
				is SlabBlock -> env.world.setBlockAndUpdate(
					position,
					DyeDataHook.getNewBlock(state.block, dye)
						.setValue(SlabBlock.TYPE, state.getValue(SlabBlock.TYPE))
						.setValue(SlabBlock.WATERLOGGED, state.getValue(SlabBlock.WATERLOGGED))
				)
				is StairBlock -> env.world.setBlockAndUpdate(
					position,
					DyeDataHook.getNewBlock(state.block, dye)
						.setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
						.setValue(StairBlock.HALF, state.getValue(StairBlock.HALF))
						.setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE))
						.setValue(StairBlock.WATERLOGGED, state.getValue(StairBlock.WATERLOGGED))
				)
				is WallBlock -> env.world.setBlockAndUpdate(
					position,
					DyeDataHook.getNewBlock(state.block, dye)
						.setValue(WallBlock.NORTH_WALL, state.getValue(WallBlock.NORTH_WALL))
						.setValue(WallBlock.EAST_WALL, state.getValue(WallBlock.EAST_WALL))
						.setValue(WallBlock.SOUTH_WALL, state.getValue(WallBlock.SOUTH_WALL))
						.setValue(WallBlock.WEST_WALL, state.getValue(WallBlock.WEST_WALL))
						.setValue(WallBlock.WATERLOGGED, state.getValue(WallBlock.WATERLOGGED))
						.setValue(WallBlock.UP, state.getValue(WallBlock.UP))
				)
				is ShulkerBoxBlock -> {
					val blockEntity = env.world.getBlockEntity(position)!! as ShulkerBoxBlockEntity
					val nbt = blockEntity.saveWithoutMetadata()
					env.world.setBlockAndUpdate(
						position,
						DyeDataHook.getNewBlock(state.block, dye)
							.setValue(ShulkerBoxBlock.FACING, state.getValue(ShulkerBoxBlock.FACING))
					)
					(env.world.getBlockEntity(position)!! as ShulkerBoxBlockEntity).load(nbt)
				}
				is StainedGlassPaneBlock -> env.world.setBlockAndUpdate(
					position,
					DyeDataHook.getNewBlock(state.block, dye)
						.setValue(StainedGlassPaneBlock.NORTH, state.getValue(StainedGlassPaneBlock.NORTH))
						.setValue(StainedGlassPaneBlock.EAST, state.getValue(StainedGlassPaneBlock.EAST))
						.setValue(StainedGlassPaneBlock.SOUTH, state.getValue(StainedGlassPaneBlock.SOUTH))
						.setValue(StainedGlassPaneBlock.WEST, state.getValue(StainedGlassPaneBlock.WEST))
						.setValue(StainedGlassPaneBlock.WATERLOGGED, state.getValue(StainedGlassPaneBlock.WATERLOGGED))
				)
				else -> env.world.setBlockAndUpdate(position, DyeData.getNewBlock(state.block, dye))
			}
		}
	}

	private data class BlockItemSpell(val item: ItemEntity, val block: Block, val dye: String) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val newStack = ItemStack(DyeDataHook.getNewBlock(block, dye).block.asItem(), item.item.count)
			newStack.tag = item.item.tag
			item.item = newStack
		}
	}

	private data class CatSpell(val cat: Cat, val dye: DyeColor) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			cat.collarColor = dye
		}
	}

	private data class ItemSpell(val entity: ItemEntity, val item: Item, val dye: String) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val newStack = ItemStack(DyeDataHook.getNewItem(item, dye), entity.item.count)
			newStack.tag = entity.item.tag
			entity.item = newStack
		}
	}

	private data class SheepSpell(val sheep: Sheep, val dye: DyeColor) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			sheep.color = dye
		}
	}

	private data class ShulkerSpell(val shulker: Shulker, val dye: DyeColor) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			shulker.variant = Optional.of(dye)
		}
	}

	private data class SpecklikeSpell(val speck: Specklike, val dye: DyeColor) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			speck.setPigment(FrozenPigment(ItemStack(HexItems.DYE_PIGMENTS[dye]!!), env.castingEntity!!.uuid))
		}
	}

	private data class WolfSpell(val wolf: Wolf, val dye: DyeColor) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			wolf.collarColor = dye
		}
	}
}