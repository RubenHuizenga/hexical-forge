package miyucomics.hexical.casting.patterns.block_mimicry

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.Container
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level

class OpHopperExtract : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val spawnPosition = args.getVec3(0, argc)
		env.assertVecInRange(spawnPosition)

		val inventoryPosition = args.getBlockPos(1, argc)
		env.assertPosInRange(inventoryPosition)

		val dirRaw = args.getBlockPos(2, argc)
		val direction = Direction.fromDelta(dirRaw.x, dirRaw.y, dirRaw.z) ?: throw MishapInvalidIota.of(args[2], 0, "axis_vector")

		return SpellAction.Result(Spell(spawnPosition, inventoryPosition, direction), MediaConstants.DUST_UNIT, listOf(ParticleSpray.burst(spawnPosition, 1.0), ParticleSpray.burst(inventoryPosition.getCenter(), 1.0)))
	}

	private data class Spell(val spawnPosition: Vec3, val inventoryPosition: BlockPos, val direction: Direction) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {}
		override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage {
			val stack = extract(env.world, inventoryPosition, direction)
			if (stack.isEmpty) {
				val newStack = image.stack.toMutableList()
				newStack.add(NullIota())
				return image.copy(stack = newStack)
			}

			val item = ItemEntity(env.world, spawnPosition.x, spawnPosition.y, spawnPosition.z, stack)
			env.world.addFreshEntity(item)
			val newStack = image.stack.toMutableList()
			newStack.add(EntityIota(item))
			return image.copy(stack = newStack)
		}
	}

	companion object {
		fun extract(world: Level, pos: BlockPos, fromDirection: Direction): ItemStack {
			val blockEntity = world.getBlockEntity(pos) ?: return ItemStack.EMPTY
			if (blockEntity !is Container)
				return ItemStack.EMPTY

			if (blockEntity is WorldlyContainer) {
				for (slot in blockEntity.getSlotsForFace(fromDirection)) {
					val stack = blockEntity.getItem(slot)
					if (!stack.isEmpty && blockEntity.canTakeItemThroughFace(slot, stack, fromDirection)) {
						val extracted = stack.copy()
						extracted.count = 1
						stack.shrink(1)
						blockEntity.setChanged()
						return extracted
					}
				}
			} else {
				for (slot in 0 until blockEntity.getContainerSize()) {
					val stack = blockEntity.getItem(slot)
					if (!stack.isEmpty) {
						val extracted = stack.copy()
						extracted.count = 1
						stack.shrink(1)
						blockEntity.setChanged()
						return extracted
					}
				}
			}

			return ItemStack.EMPTY
		}
	}
}