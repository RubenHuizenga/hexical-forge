package miyucomics.hexical.casting.patterns.colors

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.*
import miyucomics.hexical.casting.iotas.DyeIota
import miyucomics.hexical.data.DyeData
import miyucomics.hexpose.iotas.IdentifierIota
import miyucomics.hexpose.iotas.getIdentifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SignBlock
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Shulker
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.item.BlockItem
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistry
import net.minecraft.server.level.ServerLevel
import net.minecraft.core.BlockPos

class OpGetDye : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment) = listOf(
		when (args[0]) {
			is EntityIota -> {
				val entity = args.getEntity(0, argc)
				env.assertEntityInRange(entity)
				processEntity(entity)
			}
			is IdentifierIota -> {
				when (val item = ForgeRegistries.ITEMS.getValue(args.getIdentifier(0, argc))) {
					is BlockItem -> getDyeFromBlock(item.block)
					else -> {
						if (DyeData.getDye(item!!) != null)
							DyeIota(DyeData.getDye(item)!!)
						else
							NullIota()
					}
				}
			}
			is Vec3Iota -> {
				val position = args.getBlockPos(0, argc)
				env.assertPosInRange(position)
				processVec3(position, env.world)
			}
			else -> NullIota()
		}
	)

	private fun processEntity(entity: Entity): Iota {
		return when (entity) {
			is Cat -> DyeIota(entity.collarColor.name)
			is ItemEntity -> {
				when (val item = entity.item.item) {
					is BlockItem -> getDyeFromBlock(item.block)
					else -> {
						if (DyeData.getDye(item) != null)
							DyeIota(DyeData.getDye(item)!!)
						else
							NullIota()
					}
				}
			}
			is Sheep -> DyeIota(entity.color.name)
			is Shulker -> {
				if (entity.color == null)
					NullIota()
				else
					DyeIota(entity.color!!.name)
			}
			is Wolf -> DyeIota(entity.collarColor.name)
			else -> NullIota()
		}
	}

	private fun processVec3(position: BlockPos, world: ServerLevel): Iota {
		val state = world.getBlockState(position)
		if (state.block is SignBlock) {
			val sign = world.getBlockEntity(position) as SignBlockEntity
			return ListIota(listOf(DyeIota(sign.frontText.color.name), DyeIota(sign.backText.color.name)))
		}
		return getDyeFromBlock(world.getBlockState(position).block)
	}

	private fun getDyeFromBlock(block: Block): Iota {
		val dye = DyeData.getDye(block) ?: return NullIota()
		return DyeIota(dye)
	}
}