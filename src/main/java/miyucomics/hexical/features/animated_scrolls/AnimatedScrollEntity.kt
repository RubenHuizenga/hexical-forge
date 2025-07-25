package miyucomics.hexical.features.animated_scrolls

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexical.inits.HexicalEntities
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.RenderUtils
import net.minecraft.world.level.block.DiodeBlock
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.EntityType
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.world.entity.decoration.HangingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level

class AnimatedScrollEntity(entityType: EntityType<AnimatedScrollEntity>, world: Level) : HangingEntity(entityType, world), ADIotaHolder {
	var patterns: List<CompoundTag> = listOf()
	var cachedVerts: List<Vec2> = listOf()
	lateinit var scroll: ItemStack

	constructor(world: Level) : this(HexicalEntities.ANIMATED_SCROLL_ENTITY.get(), world)

	constructor(world: Level, position: BlockPos, dir: Direction, size: Int, patterns: List<CompoundTag>, scroll: ItemStack) : this(world) {
		this.pos = position
		this.patterns = patterns
		this.entityData.set(sizeDataTracker, size)
		this.scroll = scroll
		setDirection(dir)
		if (!world.isClientSide)
			updateRender()
	}

	override fun tick() {
		super.tick()
		if (!level().isClientSide && (level().gameTime % 20).toInt() == 0)
			updateRender()
	}

	override fun survives(): Boolean {
		if (!level().noCollision(this))
			return false
		val blockPos = pos.relative(direction.opposite)
		val direction = direction.getCounterClockWise()
		val mutable = BlockPos.MutableBlockPos()
		val size = entityData.get(sizeDataTracker)
		for (i in 0 until size) {
			for (j in 0 until size) {
				val m = (size - 1) / -2
				val n = (size - 1) / -2
				mutable.set(blockPos).move(direction, i + m).move(Direction.UP, j + n)
				val blockState = level().getBlockState(mutable)
				if (blockState.isSolidRender(level(), mutable) || DiodeBlock.isDiode(blockState)) continue
				return false
			}
		}
		return level().getEntities(this, this.boundingBox.contract(0.95, 0.95, 0.95), HANGING_ENTITY).isEmpty()
	}

	public override fun setDirection(facing: Direction) {
		this.direction = facing
		if (facing.axis.isHorizontal) {
			this.xRot = 0.0f
			this.yRot = (this.direction.get2DDataValue() * 90).toFloat()
		} else {
			this.xRot = (-90 * facing.axisDirection.step).toFloat()
			this.yRot = 0.0f
		}
		this.xRotO = this.xRot
		this.yRotO = this.yRot
		recalculateBoundingBox()
	}

	fun updateRender() {
		if (this.patterns.isNotEmpty())
			this.entityData.set(patternDataTracker, patterns[((level().gameTime / 20).toInt() % patterns.size)])
		else {
			val compound = CompoundTag()
			compound.putBoolean("empty", true)
			this.entityData.set(patternDataTracker, compound)
		}
	}

	override fun addAdditionalSaveData(nbt: CompoundTag) {
		nbt.putInt("direction", direction.get3DDataValue())
		nbt.putInt("state", this.entityData.get(stateDataTracker))
		nbt.putInt("color", this.entityData.get(colorDataTracker))
		nbt.putBoolean("glow", this.entityData.get(glowDataTracker))
		nbt.putInt("size", this.entityData.get(sizeDataTracker))
		nbt.putCompound("scroll", this.scroll.serializeToNBT())

		val data = ListTag()
		for (pattern in this.patterns)
			data.add(pattern)
		nbt.put("patterns", data)

		super.addAdditionalSaveData(nbt)
	}

	override fun readAdditionalSaveData(nbt: CompoundTag) {
		this.direction = Direction.from3DDataValue(nbt.getInt("direction"))
		this.entityData.set(stateDataTracker, nbt.getInt("state"))
		this.entityData.set(glowDataTracker, nbt.getBoolean("glow"))
		this.entityData.set(colorDataTracker, nbt.getInt("color"))
		this.entityData.set(sizeDataTracker, nbt.getInt("size"))
		this.scroll = ItemStack.of(nbt.getCompound("scroll"))
		setDirection(this.direction)
		recalculateBoundingBox()

		this.patterns = nbt.getList("patterns", Tag.TAG_COMPOUND.toInt()).map { it.asCompound }
		updateRender()

		super.readAdditionalSaveData(nbt)
	}

	override fun playPlacementSound() = playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F)
	override fun dropItem(entity: Entity?) {
		this.playSound(SoundEvents.PAINTING_BREAK, 1.0f, 1.0f)
		if (this.level().gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS))
			this.spawnAtLocation(this.scroll)
	}

	override fun getPickResult() = ItemStack(
		when (this.entityData.get(sizeDataTracker)) {
			1 -> HexicalItems.SMALL_ANIMATED_SCROLL_ITEM.get()
			2 -> HexicalItems.MEDIUM_ANIMATED_SCROLL_ITEM.get()
			3 -> HexicalItems.LARGE_ANIMATED_SCROLL_ITEM.get()
			else -> throw IllegalStateException("Invalid size")
		}
	)

	override fun trackingPosition(): Vec3 = Vec3.atLowerCornerOf(this.pos)
	override fun getWidth() = 16 * this.entityData.get(sizeDataTracker)
	override fun getHeight() = 16 * this.entityData.get(sizeDataTracker)
	override fun getEyeHeight(pose: Pose, dimensions: EntityDimensions) = 0f
	override fun moveTo(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) = this.setPos(x, y, z)
	override fun lerpTo(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, interpolationSteps: Int, interpolate: Boolean) = this.setPos(x, y, z)

	override fun getAddEntityPacket() = ClientboundAddEntityPacket(this, direction.get3DDataValue(), this.pos)
	override fun recreateFromPacket(packet: ClientboundAddEntityPacket) {
		super.recreateFromPacket(packet)
		this.setDirection(Direction.from3DDataValue(packet.data))
	}

	fun setState(state: Int) {
		scroll.orCreateTag.putInt("state", state)
		this.entityData.set(stateDataTracker, state)
	}
	fun setColor(color: Int) {
		scroll.orCreateTag.putInt("color", color)
		this.entityData.set(colorDataTracker, color)
	}
	fun toggleGlow() {
		this.entityData.set(glowDataTracker, !this.entityData.get(glowDataTracker))
		scroll.orCreateTag.putBoolean("glow", this.entityData.get(glowDataTracker))
	}

	override fun defineSynchedData() {
		this.entityData.define(colorDataTracker, (0xff_000000).toInt())
		this.entityData.define(glowDataTracker, false)
		this.entityData.define(stateDataTracker, 0)
		this.entityData.define(sizeDataTracker, 1)
		this.entityData.define(patternDataTracker, CompoundTag())
	}

	override fun onSyncedDataUpdated(data: EntityDataAccessor<*>) {
		when (data) {
			sizeDataTracker -> this.recalculateBoundingBox()
			patternDataTracker -> {
				val nbt = entityData.get(patternDataTracker)
				this.cachedVerts = if (nbt.contains("empty")) listOf() else RenderUtils.getNormalizedStrokes(HexPattern.fromNBT(nbt), true)
			}
			else -> {}
		}
	}

	companion object {
		private val patternDataTracker: EntityDataAccessor<CompoundTag> = SynchedEntityData.defineId(AnimatedScrollEntity::class.java, EntityDataSerializers.COMPOUND_TAG)
		val glowDataTracker: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(AnimatedScrollEntity::class.java, EntityDataSerializers.BOOLEAN)
		val colorDataTracker: EntityDataAccessor<Int> = SynchedEntityData.defineId(AnimatedScrollEntity::class.java, EntityDataSerializers.INT)
		val sizeDataTracker: EntityDataAccessor<Int> = SynchedEntityData.defineId(AnimatedScrollEntity::class.java, EntityDataSerializers.INT)
		val stateDataTracker: EntityDataAccessor<Int> = SynchedEntityData.defineId(AnimatedScrollEntity::class.java, EntityDataSerializers.INT)
	}

	override fun readIotaTag(): CompoundTag? {
		val constructed = mutableListOf<PatternIota>()
		for (pattern in this.patterns)
			constructed.add(PatternIota(HexPattern.fromNBT(pattern)))
		return IotaType.serialize(ListIota(constructed.toList()))
	}

	override fun writeIota(iota: Iota?, simulate: Boolean): Boolean {
		if (iota == null) {
			this.patterns = mutableListOf()
			this.updateRender()
			return true
		} else if (iota.type == HexIotaTypes.PATTERN) {
			this.patterns = mutableListOf((iota as PatternIota).pattern.serializeToNBT())
			this.updateRender()
			return true
		} else if (iota.type == HexIotaTypes.LIST) {
			val new = mutableListOf<CompoundTag>()
			(iota as ListIota).list.forEach {
				if (it.type != HexIotaTypes.PATTERN)
					return false
				new.add((it as PatternIota).pattern.serializeToNBT())
			}
			this.patterns = new
			this.updateRender()
			return true
		}
		return false
	}

	override fun writeable() = true
}