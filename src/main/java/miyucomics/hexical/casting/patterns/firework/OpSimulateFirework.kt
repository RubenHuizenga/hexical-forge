package miyucomics.hexical.casting.patterns.firework

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getIntBetween
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.FireworkRocketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.phys.Vec3

class OpSimulateFirework : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getVec3(0, argc)
		val duration = args.getIntBetween(2, 1, 3, argc)
		env.assertVecInRange(position)

		val fireworkStar = env.getHeldItemToOperateOn { it.`is`(Items.FIREWORK_STAR) }
		if (fireworkStar == null)
			throw MishapBadOffhandItem.of(null, "firework_star")

		return SpellAction.Result(Spell(position, args.getVec3(1, argc), duration, fireworkStar.stack.orCreateTag.getCompound(FireworkRocketItem.TAG_EXPLOSION)), MediaConstants.SHARD_UNIT, listOf(ParticleSpray.burst(position, 1.0)))
	}

	private data class Spell(val position: Vec3, val velocity: Vec3, val duration: Int, val template: CompoundTag) :
		RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val star = CompoundTag()
			star.putInt(FireworkRocketItem.TAG_EXPLOSION_TYPE, template.getInt(FireworkRocketItem.TAG_EXPLOSION_TYPE))
			star.putByte(FireworkRocketItem.TAG_EXPLOSION_FLICKER, template.getByte(FireworkRocketItem.TAG_EXPLOSION_FLICKER))
			star.putByte(FireworkRocketItem.TAG_EXPLOSION_TRAIL, template.getByte(FireworkRocketItem.TAG_EXPLOSION_TRAIL))
			star.putIntArray(FireworkRocketItem.TAG_EXPLOSION_COLORS, template.getIntArray(FireworkRocketItem.TAG_EXPLOSION_COLORS))
			star.putIntArray(FireworkRocketItem.TAG_EXPLOSION_FADECOLORS, template.getIntArray(FireworkRocketItem.TAG_EXPLOSION_FADECOLORS))

			val explosions = ListTag()
			explosions.add(star)

			val main = CompoundTag()
			main.put(FireworkRocketItem.TAG_EXPLOSIONS, explosions)
			main.putByte(FireworkRocketItem.TAG_FLIGHT, duration.toByte())

			val stack = ItemStack(Items.FIREWORK_ROCKET)
			stack.orCreateTag.put(FireworkRocketItem.TAG_FIREWORKS, main)

			val firework = FireworkRocketEntity(env.world, stack, position.x, position.y, position.z, true)
			firework.setDeltaMovement(velocity.x, velocity.y, velocity.z)
			env.world.addFreshEntity(firework)
		}
	}
}