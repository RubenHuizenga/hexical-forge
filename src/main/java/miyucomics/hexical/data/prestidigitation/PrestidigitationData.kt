package miyucomics.hexical.data.prestidigitation

import at.petrak.hexcasting.api.block.circle.BlockAbstractImpetus
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.utils.CastingUtils
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BellBlockEntity
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus
import net.minecraft.world.level.block.entity.BeehiveBlockEntity
import net.minecraft.world.level.block.state.properties.BellAttachType
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.animal.Panda
import net.minecraft.world.entity.animal.Pufferfish
import net.minecraft.world.entity.animal.Squid
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.entity.Shearable
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.ShovelItem
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.core.MappedRegistry
import net.minecraft.tags.BlockTags
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.ComparatorMode
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject
import net.minecraftforge.registries.RegistryBuilder

@Suppress("DEPRECATION")
object PrestidigitationData {
 	val PRESTIDIGITATION_HANDLER_KEY: ResourceKey<Registry<PrestidigitationHandler>> = 
        ResourceKey.createRegistryKey(HexicalMain.id("prestidigitation_handler"))

    val DEFERRED_REGISTER = DeferredRegister.create(
        PRESTIDIGITATION_HANDLER_KEY,
        HexicalMain.MOD_ID
    )

	fun createRegistry() {
		DEFERRED_REGISTER.makeRegistry {
			RegistryBuilder<PrestidigitationHandler>().setName(HexicalMain.id("prestidigitation_handler"))
		}
	}
  	
 	val TOGGLE_COMPARATOR : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("toggle_comparator") {
		object : PrestidigitationHandler {
			override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
				val state = env.world.getBlockState(position)
				if (!state.`is`(Blocks.COMPARATOR))
					return false
				when (state.getValue(BlockStateProperties.MODE_COMPARATOR)!!) {
					ComparatorMode.COMPARE -> env.world.setBlockAndUpdate(position,	state.setValue(BlockStateProperties.MODE_COMPARATOR, ComparatorMode.SUBTRACT))
					ComparatorMode.SUBTRACT -> env.world.setBlockAndUpdate(position, state.setValue(BlockStateProperties.MODE_COMPARATOR, ComparatorMode.COMPARE))
				}
				return true
			}
		}
	}

	val CARVE_PUMPKIN : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("carve_pumpkin") {
object : PrestidigitationHandler {
		override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
			val state = env.world.getBlockState(position)
			if (!state.`is`(Blocks.PUMPKIN))
				return false
			env.world.setBlockAndUpdate(position, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HORIZONTAL_FACING.getPossibleValues().random()))
			return true
		}
	}}

	val AXEING : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("axeing") {
object : PrestidigitationHandler {
		override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
			val state = env.world.getBlockState(position)
			if (!AxeItem.STRIPPABLES.containsKey(state.block))
				return false
			env.world.setBlockAndUpdate(position, AxeItem.STRIPPABLES[state.block]!!.defaultBlockState())
			return true
		}
	}}

	val PATHING : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("pathing") {
object : PrestidigitationHandler {
		override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
			val state = env.world.getBlockState(position)
			if (!ShovelItem.FLATTENABLES.containsKey(state.block))
				return false
			env.world.setBlockAndUpdate(position, ShovelItem.FLATTENABLES[state.block]!!)
			return true
		}
	}}

	val PRESS_BUTTONS : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("press_buttons") {
object : PrestidigitationHandler {
		override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
			val state = env.world.getBlockState(position)
			if (!state.`is`(BlockTags.BUTTONS))
				return false
			(state.block as ButtonBlock).press(state, env.world, position)
			(state.block as ButtonBlock).playSound(null, env.world, position, true)
			env.world.gameEvent(null, GameEvent.BLOCK_ACTIVATE, position)
			return true
		}
	}}

	val EXTINGUISH_FIRES : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("extinguish_fires") {
object : PrestidigitationHandler {
		override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
			val state = env.world.getBlockState(position)
			if (!state.`is`(BlockTags.FIRE))
				return false
			env.world.removeBlock(position, false)
			return true
		}
	}}

	val CREATE_SOUL_FIRE : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("create_soul_fire") {
object : PrestidigitationHandler {
		override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
			val state = env.world.getBlockState(position)
			if (!state.`is`(BlockTags.SOUL_FIRE_BASE_BLOCKS))
				return false
			if (!env.world.getBlockState(position.above()).isAir)
				return false
			env.world.setBlockAndUpdate(position.above(), Blocks.SOUL_FIRE.defaultBlockState())
			return true
		}
	}}

	val PRESSURE_PRESSURE_PLATES : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("pressure_pressure_plates") {
object : PrestidigitationHandler {
		override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
			val state = env.world.getBlockState(position)
			if (!state.`is`(BlockTags.PRESSURE_PLATES))
				return false
			env.world.setBlockAndUpdate(position, state.setValue(BlockStateProperties.POWERED, !state.getValue(BlockStateProperties.POWERED)))
			return true
		}
	}}

	val DRAIN_CAULDRONS : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("drain_cauldrons") {
object : PrestidigitationHandler {
		override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
			val state = env.world.getBlockState(position)
			if (!state.`is`(BlockTags.CAULDRONS))
				return false
			env.world.setBlockAndUpdate(position, Blocks.CAULDRON.defaultBlockState())
			return true
		}
	}}

	val LIGHT_CANDLE : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("light_candle") {
object : PrestidigitationHandler {
		override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
			val state = env.world.getBlockState(position)
			if (!state.`is`(BlockTags.CANDLES) && !state.`is`(BlockTags.CANDLE_CAKES) && !state.`is`(BlockTags.CAMPFIRES))
				return false
			env.world.setBlockAndUpdate(position, state.setValue(BlockStateProperties.LIT, !state.getValue(BlockStateProperties.LIT)))
			return true
		}
	}}

	val OPEN_DOORS : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("open_doors") {
object : PrestidigitationHandler {
		override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
			val state = env.world.getBlockState(position)
			if (!state.`is`(BlockTags.DOORS) && !state.`is`(BlockTags.TRAPDOORS) && !state.`is`(BlockTags.FENCE_GATES))
				return false
			env.world.setBlockAndUpdate(position, state.setValue(BlockStateProperties.OPEN, !state.getValue(BlockStateProperties.OPEN)))
			return true
		}
	}}

	val STEAL_HONEY : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("steal_honey") {
		object : PrestidigitationHandler {
			override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
				val blockState = env.world.getBlockState(position)
				if (!blockState.`is`(BlockTags.BEEHIVES) || blockState.getValue(BeehiveBlock.HONEY_LEVEL) < 5)
					return false
				env.world.playSound(null, position, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1f, 1f)
				BeehiveBlock.dropHoneycomb(env.world, position)
				(blockState.block as BeehiveBlock).releaseBeesAndResetHoneyLevel(env.world, blockState, position, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED)
				env.world.gameEvent(null, GameEvent.SHEAR, position)
				return true
			}
		}
	}

	val PLAY_NOTE : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("play_note") {
		object : PrestidigitationHandler {
			override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
				val state = env.world.getBlockState(position)
				if (state.block !is NoteBlock)
					return false
				env.world.blockEvent(position, Blocks.NOTE_BLOCK, 0, 0)
				env.world.gameEvent(null, GameEvent.NOTE_BLOCK_PLAY, position)
				return true
			}
		}
	}

	val RING_BELL : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("ring_bell") {
		object : PrestidigitationHandler {
			override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
				val state = env.world.getBlockState(position)
				if (state.block !is BellBlock)
					return false

				val facing = state.getValue(BellBlock.FACING)
				val ringDirection = when (state.getValue(BellBlock.ATTACHMENT)) {
					BellAttachType.SINGLE_WALL -> facing.getClockWise()
					BellAttachType.DOUBLE_WALL -> facing.getClockWise()
					else -> facing
				}
				(env.world.getBlockEntity(position) as BellBlockEntity).onHit(ringDirection)
				env.world.playSound(null, position, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0f, 1.0f)
				env.world.gameEvent(env.castingEntity, GameEvent.BLOCK_CHANGE, position)
				return true
			}
		}
	}

	val DISPENSE : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("dispense") {
		object : PrestidigitationHandler {
			override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
				val state = env.world.getBlockState(position)
				if (state.block !is DispenserBlock)
					return false
				(state.block as DispenserBlock).tick(state, env.world as ServerLevel, position, null)
				return true
			}
		}
	}

	val PRIME_TNT : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("prime_tnt") {
		object : PrestidigitationHandler {
			override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
				val state = env.world.getBlockState(position)
				if (state.block !is TntBlock)
					return false
				TntBlock.explode(env.world, position)
				env.world.removeBlock(position, false)
				return true
			}
		}
	}

	val LEARN_AKASHIC : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("learn_akashic") {
		object : PrestidigitationHandler {
			override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
				if (env.castingEntity !is ServerPlayer)
					return false
				val shelf = env.world.getBlockEntity(position)
				if (shelf !is BlockEntityAkashicBookshelf)
					return false

				val caster = env.castingEntity as ServerPlayer
				val nbt = shelf.iotaTag ?: return false
				CastingUtils.giveIota(caster, IotaType.deserialize(nbt, caster.serverLevel()))

				return true
			}
		}
	}

	val TRIGGER_IMPETUS : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("trigger_impetus") {
		object : PrestidigitationHandler {
			override fun tryHandleBlock(env: CastingEnvironment, position: BlockPos): Boolean {
				if (env.castingEntity !is ServerPlayer)
					return false
				if (env.world.getBlockState(position).block !is BlockAbstractImpetus)
					return false
				(env.world.getBlockEntity(position) as BlockEntityAbstractImpetus).startExecution(env.castingEntity as ServerPlayer)
				return true
			}
		}
	}

	val ARM_ARMOR_STANDS : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("arm_armor_stands") {
		object : PrestidigitationHandler {
			override fun tryHandleEntity(env: CastingEnvironment, entity: Entity): Boolean {
				if (entity !is ArmorStand)
					return false
				entity.setShowArms(!entity.isShowArms())
				entity.playSound(SoundEvents.ARMOR_STAND_PLACE, 1f, 1f)
				return true
			}
		}
	}

	val DEPRIME_TNT : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("deprime_tnt") {
		object : PrestidigitationHandler {
			override fun tryHandleEntity(env: CastingEnvironment, entity: Entity): Boolean {
				if (entity !is PrimedTnt)
					return false
				if (entity.level().getBlockState(entity.blockPosition()).canBeReplaced())
					entity.level().setBlockAndUpdate(entity.blockPosition(), Blocks.TNT.defaultBlockState())
				entity.discard()
				return true
			}
		}
	}

	val SHEAR : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("shear") {
		object : PrestidigitationHandler {
			override fun tryHandleEntity(env: CastingEnvironment, entity: Entity): Boolean {
				if (entity !is Shearable)
					return false
				entity.shear(SoundSource.MASTER)
				return true
			}
		}
	}

	val MILK_SQUIDS : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("milk_squids") {
object : PrestidigitationHandler {
		override fun tryHandleEntity(env: CastingEnvironment, entity: Entity): Boolean {
			if (entity !is Squid)
				return false
			entity.spawnInk()
			return true
		}
	}}

	val PANDAS_SNEEZE : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("pandas_sneeze") {
		object : PrestidigitationHandler {
			override fun tryHandleEntity(env: CastingEnvironment, entity: Entity): Boolean {
				if (entity !is Panda)
					return false
				entity.sneeze(true)
				return true
			}
		}
	}

	val TEASE_CREEPERS : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("tease_creepers") {
object : PrestidigitationHandler {
		override fun tryHandleEntity(env: CastingEnvironment, entity: Entity): Boolean {
			if (entity !is Creeper)
				return false
			if (entity.isIgnited)
				entity.entityData.set(Creeper.DATA_IS_IGNITED, false)
			else
				entity.ignite()
			return true
		}
	}}

	val PUFF_PUFFERFISH : RegistryObject<PrestidigitationHandler> = DEFERRED_REGISTER.register("puff_pufferfish") {
		object : PrestidigitationHandler {
			override fun tryHandleEntity(env: CastingEnvironment, entity: Entity): Boolean {
				if (entity !is Pufferfish)
					return false
				if (entity.puffState != 2) {
					entity.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, 1f, 1f)
					entity.inflateCounter = 0
					entity.deflateTimer = 0
					entity.puffState = 2
				}
				return true
			}
		}
	}
}