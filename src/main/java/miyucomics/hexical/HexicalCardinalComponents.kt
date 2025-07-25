package miyucomics.hexical

import at.petrak.hexcasting.api.addldata.ADMediaHolder
import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder
import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.getList
import at.petrak.hexcasting.forge.cap.ForgeCapabilityHandler
import at.petrak.hexcasting.forge.cap.adimpl.CapEntityIotaHolder
import at.petrak.hexcasting.forge.cap.adimpl.CapStaticMediaHolder
import at.petrak.hexcasting.forge.cap.HexCapabilities
import miyucomics.hexical.features.animated_scrolls.AnimatedScrollEntity
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.core.Direction
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.NonNullSupplier
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.util.function.BooleanSupplier


object HexicalForgeCapabilityHandler {
    fun attachItemCaps(event: AttachCapabilitiesEvent<ItemStack>) {
        val stack = event.`object`
        
        if (stack.`is`(HexicalItems.HEX_GUMMY.get())) {
            event.addCapability(
                ResourceLocation("hexical", "media_item"),
                ForgeCapabilityHandler.provide(stack, HexCapabilities.MEDIA) {
                    CapStaticMediaHolder({ MediaConstants.DUST_UNIT / 10 }, ADMediaHolder.AMETHYST_DUST_PRIORITY, stack)
                }
            )
        }
    }

    fun attachEntityCaps(event: AttachCapabilitiesEvent<Entity>) {
        val entity = event.`object`
        
        if (entity is AnimatedScrollEntity) {
            event.addCapability(
                ResourceLocation("hexical", "iota_holder"),
                wrapItemEntityDelegate(entity) { scrollEntity ->
                    AnimatedScrollReader(scrollEntity)
                }
            )
        }
    }

	data class SimpleProvider<CAP>(
		val invalidated: BooleanSupplier,
		val capability: Capability<CAP>,
		val instance: LazyOptional<CAP>
	) : ICapabilityProvider {
		override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
			return if (invalidated.asBoolean) {
				LazyOptional.empty()
			} else {
				if (cap == capability) instance.cast() else LazyOptional.empty()
			}
		}
	}

	private fun <E : Entity> wrapItemEntityDelegate(
		entity: E,
		make: (E) -> ItemDelegatingEntityIotaHolder
	): SimpleProvider<ADIotaHolder> {
		return provide(entity, HexCapabilities.IOTA) {
			CapEntityIotaHolder.Wrapper(make(entity))
		}
	}

	private fun <CAP> provide(
		entity: Entity,
		capability: Capability<CAP>,
		supplier: NonNullSupplier<CAP>
	): SimpleProvider<CAP> {
		return provide(entity::isRemoved, capability, supplier)
	}

	private fun <CAP> provide(
		invalidated: BooleanSupplier,
		capability: Capability<CAP>,
		supplier: NonNullSupplier<CAP>
	): SimpleProvider<CAP> {
		return SimpleProvider(invalidated, capability, LazyOptional.of(supplier))
	}
}

class AnimatedScrollReader(scrollEntity: AnimatedScrollEntity) : ItemDelegatingEntityIotaHolder({ scrollEntity.scroll.copy() }, { stack ->
    scrollEntity.scroll = stack
    scrollEntity.patterns = stack.getList("patterns", Tag.TAG_COMPOUND.toInt())!!.map { it.asCompound }
    scrollEntity.updateRender()
})