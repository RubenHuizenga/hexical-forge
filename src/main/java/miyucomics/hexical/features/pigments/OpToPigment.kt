package miyucomics.hexical.features.pigments

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.item.PigmentItem
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.features.dyes.DyeIota
import miyucomics.hexical.features.dyes.getTrueDye
import miyucomics.hexpose.iotas.IdentifierIota
import miyucomics.hexpose.iotas.getIdentifier
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistry

object OpToPigment : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env.castingEntity == null)
			throw MishapBadCaster()
		val caster = env.castingEntity as LivingEntity

		val colorizer = when (args[0]) {
			is DyeIota -> FrozenPigment(ItemStack(HexItems.DYE_PIGMENTS[args.getTrueDye(0, argc)]), caster.uuid)
			is EntityIota -> {
				when (val entity = args.getEntity(0, argc)) {
					is Player -> IXplatAbstractions.INSTANCE.getPigment(entity)
					is ItemEntity -> {
						val item = args.getItemEntity(0, argc)
						env.assertEntityInRange(item)
						val stack = item.item
						if (stack.item is PigmentItem)
							FrozenPigment(stack, caster.uuid)
						else
							null
					}
					else -> null
				}
			}
			is IdentifierIota -> {
				val item = ForgeRegistries.ITEMS.getValue(args.getIdentifier(0, argc))
				if (item is PigmentItem)
					FrozenPigment(ItemStack(item), caster.uuid)
				else
					null
			}
			else -> null
		}

		if (colorizer == null)
			throw MishapInvalidIota.of(args[0], 0, "to_pigment")

		return listOf(PigmentIota(colorizer))
	}
}