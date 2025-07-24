package miyucomics.hexical.blocks

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.registry.HexicalBlocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.entity.EntitySelector
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import java.util.*
import kotlin.math.min

class PedestalBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexicalBlocks.PEDESTAL_BLOCK_ENTITY.get(), pos, state), Container {
	private var heldItemStack: ItemStack = ItemStack.EMPTY
	private var heldItemEntity: ItemEntity? = null
	private var persistentUUID: UUID? = null
	private val normalVector: Vec3i = blockState.getValue(PedestalBlock.FACING).normal

	fun onBlockBreak() {
		if (heldItemEntity != null)
			heldItemEntity!!.discard()
		if (level !is ServerLevel)
			return
		if (!heldItemStack.isEmpty) {
			val heightOffset = HEIGHT - 0.5
			(level as ServerLevel).addFreshEntity(ItemEntity(level!!, worldPosition.x + 0.5 + heightOffset * normalVector.x, worldPosition.y + 0.5 + heightOffset * normalVector.y, worldPosition.z + 0.5 + heightOffset * normalVector.z, heldItemStack))
		}
		setChanged()
	}

	fun onUse(player: Player, hand: InteractionHand): InteractionResult {
		val heldStack = player.getItemInHand(hand)

		if (heldItemStack.isEmpty) {
			if (heldStack.isEmpty)
				return InteractionResult.PASS
			setItem(0, heldStack.copy())
			heldStack.shrink(heldStack.count)
			return InteractionResult.SUCCESS
		}

		if (ItemStack.isSameItemSameTags(heldItemStack, heldStack)) {
			if (!level!!.isClientSide) {
				val amount = min((heldItemStack.maxStackSize - heldItemStack.count), heldStack.count)
				heldItemStack.grow(amount)
				heldStack.shrink(amount)
				syncItemAndEntity(true)
			}
			return InteractionResult.SUCCESS
		}

		if (ItemStack.isSameItemSameTags(heldItemStack, player.getItemInHand(if (hand == InteractionHand.MAIN_HAND) InteractionHand.OFF_HAND else InteractionHand.MAIN_HAND)))
			return InteractionResult.PASS

		if (!level!!.isClientSide) {
			val stackToGive = removeItemNoUpdate(0)
			if (hand == InteractionHand.MAIN_HAND) {
				setItem(0, heldStack.copy())
				heldStack.shrink(heldStack.count)
			}

			if (!stackToGive.isEmpty) {
				if (player.mainHandItem.isEmpty)
					player.setItemInHand(InteractionHand.MAIN_HAND, stackToGive)
				else {
					val hasRemaining = player.inventory.add(stackToGive)
					if(hasRemaining) 
						player.drop(stackToGive, false)
				}
			}
		}

		return InteractionResult.SUCCESS
	}

	fun tick(world: Level, pos: BlockPos) {
		if (world.isClientSide())
			return

		if (heldItemEntity != null) {
			val xPos = pos.x + 0.5 + (HEIGHT - 0.2) * normalVector.x
			val yPos = pos.y + 0.5 + (HEIGHT - 0.2) * normalVector.y
			val zPos = pos.z + 0.5 + (HEIGHT - 0.2) * normalVector.z
			heldItemEntity!!.setPos(xPos, yPos, zPos)
			heldItemEntity!!.setDeltaMovement(0.0, 0.0, 0.0)
		}

		syncItemAndEntity(false)

		val inputtedItems = getInputItemEntities()
			.sortedWith { a: ItemEntity, b: ItemEntity -> (pos.distSqr(a.blockPosition()) - pos.distSqr(b.blockPosition())).toInt() }

		var wasItemUpdated = false

		for (newItemEntity in inputtedItems) {
			val newStack = newItemEntity.item

			if (heldItemStack.isEmpty) {
				heldItemStack = newStack.copy()
				newStack.shrink(newStack.count)
				wasItemUpdated = true
				break
			}

			if (ItemStack.isSameItemSameTags(heldItemStack, newStack)) {
				val amount = min((heldItemStack.maxStackSize - heldItemStack.count), newStack.count)
				heldItemStack.grow(amount)
				newStack.shrink(amount)
				wasItemUpdated = true
				break
			}
		}

		if (wasItemUpdated)
			syncItemAndEntity(true)
	}

	private fun getInputItemEntities() =
		level!!.getEntitiesOfClass(ItemEntity::class.java, AABB.of(BoundingBox(worldPosition))) { item -> item.uuid != persistentUUID && EntitySelector.NO_SPECTATORS.test(item) }

	fun modifyImage(image: CastingImage): CastingImage {
		val data = IXplatAbstractions.INSTANCE.findDataHolder(heldItemStack) ?: return image
		val iota = data.readIota(level as ServerLevel) ?: return image
		return if (image.parenCount == 0) {
			val stack = image.stack.toMutableList()
			stack.add(iota)
			image.copy(stack = stack)
		} else {
			val parenthesized = image.parenthesized.toMutableList()
			parenthesized.add(ParenthesizedIota(iota, false))
			image.copy(parenthesized = parenthesized)
		}
	}

	override fun getContainerSize(): Int = 1
	override fun isEmpty() = heldItemStack.isEmpty

	override fun getItem(slot: Int): ItemStack {
		if (slot == 0)
			return heldItemStack
		return ItemStack.EMPTY
	}

	override fun setItem(slot: Int, stack: ItemStack) {
		if (slot == 0) {
			heldItemStack = stack
			syncItemAndEntity(true)
			setChanged()
		}
	}

	override fun removeItemNoUpdate(slot: Int): ItemStack {
		if (slot == 0) {
			val temp = heldItemStack
			heldItemStack = ItemStack.EMPTY
			syncItemAndEntity(true)
			setChanged()
			return temp
		}
		return ItemStack.EMPTY
	}

	override fun removeItem(slot: Int, amount: Int): ItemStack {
		if (slot == 0) {
			val newSplit = heldItemStack.split(amount)
			syncItemAndEntity(true)
			setChanged()
			return newSplit
		}
		return ItemStack.EMPTY
	}

	override fun stillValid(player: Player): Boolean = false

	override fun clearContent() {
		heldItemStack = ItemStack.EMPTY
		syncItemAndEntity(true)
		setChanged()
	}

	override fun setChanged() {
		if (level !is ServerLevel)
			return
		(level as ServerLevel).chunkSource.blockChanged(worldPosition)
		super.setChanged()
	}

	private fun generateUniqueUUID(): UUID {
		val world = getLevel()
		var newUUID = UUID.randomUUID()
		if (world !is ServerLevel)
			return newUUID
		while (world.getEntity(newUUID) != null)
			newUUID = UUID.randomUUID()
		return newUUID
	}

	private fun populateHeldItemEntity() {
		val serverWorld = level as? ServerLevel ?: return

		if (persistentUUID == null)
			persistentUUID = generateUniqueUUID()

		if (heldItemStack.isEmpty)
			return

		heldItemEntity?.discard()
		heldItemEntity = null

		val possibleOverItem = serverWorld.getEntity(persistentUUID!!)
		if (possibleOverItem is ItemEntity) {
			heldItemEntity = possibleOverItem
			heldItemEntity!!.item = heldItemStack
		} else {
			val xPos = worldPosition.x + 0.5 + (HEIGHT - 0.2) * normalVector.x
			val yPos = worldPosition.y + 0.5 + (HEIGHT - 0.2) * normalVector.y
			val zPos = worldPosition.z + 0.5 + (HEIGHT - 0.2) * normalVector.z
			heldItemEntity = ItemEntity(serverWorld, xPos, yPos, zPos, heldItemStack, 0.0, 0.0, 0.0)
			heldItemEntity!!.uuid = persistentUUID!!
			heldItemEntity!!.setNoGravity(true)
			heldItemEntity!!.noPhysics = true
			heldItemEntity!!.setNoPickUpDelay() 
			heldItemEntity!!.setUnlimitedLifetime()
			heldItemEntity!!.isInvulnerable = true
			serverWorld.addFreshEntity(heldItemEntity!!)
		}
	}

	private fun syncItemAndEntity(changeItemEntity: Boolean) {
		if (level!!.isClientSide) return

		// item stack is gone
		if (heldItemStack.isEmpty) {
			heldItemEntity?.let {
				it.item = ItemStack.EMPTY
				heldItemEntity = null
				setChanged()
			}
			return
		}

		// item entity is gone for whatever reason
		if (heldItemEntity == null || heldItemEntity!!.isRemoved) {
			if (heldItemEntity != null && (heldItemEntity!!.removalReason == Entity.RemovalReason.DISCARDED || (heldItemEntity!!.removalReason == Entity.RemovalReason.KILLED && (heldItemEntity!!.item == null || heldItemEntity!!.item.isEmpty))) && !changeItemEntity) {
				heldItemStack = ItemStack.EMPTY
				setChanged()
				return
			}
			populateHeldItemEntity()
			return
		}

		// item entity or item stack are out of sync
		if (heldItemStack != heldItemEntity!!.item) {
			if (changeItemEntity) {
				heldItemEntity!!.item = heldItemStack
			} else {
				heldItemStack = heldItemEntity!!.item
				setChanged()
			}
		}
	}

	override fun load(nbt: CompoundTag) {
		super.load(nbt)
		this.heldItemStack = ItemStack.of(nbt.getCompound("item"))
		if (nbt.hasUUID("persistent_uuid"))
			this.persistentUUID = nbt.getUUID("persistent_uuid")
	}

	override fun saveAdditional(nbt: CompoundTag) {
		super.saveAdditional(nbt)
		nbt.putCompound("item", heldItemStack.save(CompoundTag()))
		if (persistentUUID != null)
			nbt.putUUID("persistent_uuid", persistentUUID!!)
	}

	override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)
	override fun getUpdateTag(): CompoundTag {
		val tag = CompoundTag()
		this.saveAdditional(tag)
		return tag
	}

	companion object {
		const val HEIGHT = 0.75f
	}
}