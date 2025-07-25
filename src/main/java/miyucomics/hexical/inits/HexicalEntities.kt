package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.animated_scrolls.AnimatedScrollEntity
import miyucomics.hexical.features.animated_scrolls.AnimatedScrollRenderer
import miyucomics.hexical.features.magic_missile.MagicMissileEntity
import miyucomics.hexical.features.magic_missile.MagicMissileRenderer
import miyucomics.hexical.features.specklikes.mesh.MeshEntity
import miyucomics.hexical.features.specklikes.mesh.MeshRenderer
import miyucomics.hexical.features.specklikes.speck.SpeckEntity
import miyucomics.hexical.features.specklikes.speck.SpeckRenderer
import miyucomics.hexical.features.spike.SpikeEntity
import miyucomics.hexical.features.spike.SpikeRenderer
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object HexicalEntities {
    private val ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HexicalMain.MOD_ID)

    val ANIMATED_SCROLL_ENTITY: RegistryObject<EntityType<AnimatedScrollEntity>> = 
        ENTITY_TYPES.register("animated_scroll") {
            EntityType.Builder.of(::AnimatedScrollEntity, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .clientTrackingRange(10)
                .updateInterval(1)
                .build(HexicalMain.MOD_ID + ":animated_scroll")
        }

    val MAGIC_MISSILE_ENTITY: RegistryObject<EntityType<MagicMissileEntity>> = 
        ENTITY_TYPES.register("magic_missile") {
            EntityType.Builder.of(::MagicMissileEntity, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .clientTrackingRange(4)
                .updateInterval(20)
                .build(HexicalMain.MOD_ID + ":magic_missile")
        }

    val SPIKE_ENTITY: RegistryObject<EntityType<SpikeEntity>> = 
        ENTITY_TYPES.register("spike") {
            EntityType.Builder.of(::SpikeEntity, MobCategory.MISC)
                .sized(1f, 1f)
                .clientTrackingRange(10)
                .updateInterval(1)
                .build(HexicalMain.MOD_ID + ":spike")
        }

    val SPECK_ENTITY: RegistryObject<EntityType<SpeckEntity>> = 
        ENTITY_TYPES.register("speck") {
            EntityType.Builder.of(::SpeckEntity, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .clientTrackingRange(32)
                .updateInterval(1)
                .build(HexicalMain.MOD_ID + ":speck")
        }

    val MESH_ENTITY: RegistryObject<EntityType<MeshEntity>> = 
        ENTITY_TYPES.register("mesh") {
            EntityType.Builder.of(::MeshEntity, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .clientTrackingRange(32)
                .updateInterval(1)
                .build(HexicalMain.MOD_ID + ":mesh")
        }

	fun init() {
		ENTITY_TYPES.register(MOD_BUS)
	}

    @SubscribeEvent
	fun clientInit(event: EntityRenderersEvent.RegisterRenderers) {
		event.registerEntityRenderer(ANIMATED_SCROLL_ENTITY.get(), ::AnimatedScrollRenderer)
		event.registerEntityRenderer(MAGIC_MISSILE_ENTITY.get(), ::MagicMissileRenderer)
		event.registerEntityRenderer(SPIKE_ENTITY.get(), ::SpikeRenderer)
		event.registerEntityRenderer(SPECK_ENTITY.get(), ::SpeckRenderer)
		event.registerEntityRenderer(MESH_ENTITY.get(), ::MeshRenderer)
	}
}