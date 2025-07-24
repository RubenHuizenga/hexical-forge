package miyucomics.hexical.entities

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.registry.HexicalDamageTypes
import miyucomics.hexical.registry.HexicalEntities
import net.minecraft.world.entity.EntityEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.level.Level
import net.minecraft.world.damagesource.DamageSource

class MagicMissileEntity(entityType: EntityType<out MagicMissileEntity?>, world: Level) : AbstractArrow(entityType, world) {
	constructor(world: Level) : this(HexicalEntities.MAGIC_MISSILE_ENTITY.get(), world)

	override fun handleEntityEvent(status: Byte) {
		if (status == EntityEvent.DEATH) {
			level().playSound(null, this.blockPosition(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.NEUTRAL, 0.25f, 1.5f)
			for (i in 0..7)
				level().addParticle(ItemParticleOption(ParticleTypes.ITEM, ItemStack(Items.AMETHYST_BLOCK, 1)), this.x, this.y, this.z, HexicalMain.RANDOM.nextGaussian() / 20f, HexicalMain.RANDOM.nextGaussian() / 20f, HexicalMain.RANDOM.nextGaussian() / 20f)
		}
	}

	override fun tick() {
		super.tick()
		if (!level().isClientSide && (this.inGround || this.tickCount > 200))
			shatter()
	}

	override fun onHitBlock(blockHitResult: BlockHitResult) {
		shatter()
	}

	override fun onHitEntity(entityHitResult: EntityHitResult) {
		val target = entityHitResult.entity
		target.hurt(DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(HexicalDamageTypes.MAGIC_MISSILE), this, owner), 2f)
		target.deltaMovement = this.deltaMovement.multiply(1.0, 0.0, 1.0).normalize().scale(0.6).add(0.0, 0.1, 0.0)
		shatter()
	}

	private fun shatter() {
		level().broadcastEntityEvent(this, EntityEvent.DEATH)
		discard()
	}

	override fun getPickupItem() = null
	override fun isNoGravity() = true
	override fun getWaterInertia() = 1f
	override fun tryPickup(player: Player) = false
	override fun getDefaultHitGroundSoundEvent(): SoundEvent = SoundEvents.AMETHYST_BLOCK_BREAK
}