package miyucomics.hexical.casting.patterns.block_mimicry

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.Container
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import kotlin.math.min

class OpHopperInsert : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val item = args.getItemEntity(0, argc)
		env.assertEntityInRange(item)

		val position = args.getBlockPos(1, argc)
		env.assertPosInRange(position)

		val dirRaw = args.getBlockPos(2, argc)
		val direction = Direction.fromDelta(dirRaw.x, dirRaw.y, dirRaw.z) ?: throw MishapInvalidIota.of(args[2], 0, "axis_vector")

		return SpellAction.Result(Spell(item, position, direction), MediaConstants.DUST_UNIT, listOf(ParticleSpray.burst(item.position(), 1.0), ParticleSpray.burst(position.getCenter(), 1.0)))
	}

	private data class Spell(val item: ItemEntity, val position: BlockPos, val direction: Direction) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val new = insert(env.world, position, direction, item.item)
			item.item = new
		}
	}

	companion object {
		fun insert(world: Level, targetPos: BlockPos, fromDirection: Direction, stack: ItemStack): ItemStack {
			var mutableStack = stack
			if (mutableStack.isEmpty)
				return mutableStack

			val blockEntity = world.getBlockEntity(targetPos)
			if (blockEntity !is Container)
				return mutableStack

			if (blockEntity is WorldlyContainer) {
				for (slot in blockEntity.getSlotsForFace(fromDirection)) {
					mutableStack = insertIntoSlot(blockEntity, slot, mutableStack)
					if (mutableStack.isEmpty)
						return ItemStack.EMPTY
				}
			} else {
				for (slot in 0 until blockEntity.getContainerSize()) {
					mutableStack = insertIntoSlot(blockEntity, slot, mutableStack)
					if (mutableStack.isEmpty)
						return ItemStack.EMPTY
				}
			}

			return mutableStack
		}

		private fun insertIntoSlot(inventory: Container, slot: Int, stack: ItemStack): ItemStack {
			val targetStack = inventory.getItem(slot)
			if (!inventory.canPlaceItem(slot, stack))
				return stack

			if (targetStack.isEmpty) {
				inventory.setItem(slot, stack.copy())
				stack.count = 0
				inventory.setChanged()
				return ItemStack.EMPTY
			}

			if (!ItemStack.isSameItemSameTags(stack, targetStack))
				return stack

			val maxInsert = min(stack.count, targetStack.maxStackSize - targetStack.count)
			if (maxInsert <= 0)
				return stack

			targetStack.grow(maxInsert)
			stack.shrink(maxInsert)
			inventory.setChanged()

			return stack
		}
	}
}