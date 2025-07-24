package miyucomics.hexical.registry

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.mediaBarColor
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.mojang.blaze3d.systems.RenderSystem
import miyucomics.hexical.casting.components.LedgerRecordComponent
import miyucomics.hexical.casting.components.SentinelBedComponent
import miyucomics.hexical.client.ClientStorage
import miyucomics.hexical.client.ShaderRenderer
import miyucomics.hexical.data.EvokeState
import miyucomics.hexical.data.KeybindData
import miyucomics.hexical.data.LesserSentinelState
import miyucomics.hexical.interfaces.PlayerEntityMinterface
import miyucomics.hexical.utils.CharmedItemUtilities.getMaxMedia
import miyucomics.hexical.utils.CharmedItemUtilities.getMedia
import miyucomics.hexical.utils.CharmedItemUtilities.isStackCharmed
import miyucomics.hexical.utils.RenderUtils
import miyucomics.hexical.HexicalMain
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.vertex.*
import com.mojang.blaze3d.vertex.*
import com.mojang.blaze3d.vertex.*
import com.mojang.blaze3d.vertex.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.ChatFormatting
import com.mojang.math.Axis
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.api.distmarker.Dist
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.sin

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, value = [Dist.DEDICATED_SERVER], bus = Mod.EventBusSubscriber.Bus.FORGE)
object HexicalServerEvents {
	fun init() {
		CastingEnvironment.addCreateEventListener { env: CastingEnvironment, _: CompoundTag ->
			env.addExtension(SentinelBedComponent(env))
			if (env is PlayerBasedCastEnv)
				env.addExtension(LedgerRecordComponent(env))
		}
	}

	@SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        val oldPlayer = event.original  
        val newPlayer = event.entity    

        // Check if this was a death respawn (not just End return)
        if (event.isWasDeath) {
            ShaderRenderer.setEffect(null)
        }

        if (newPlayer is PlayerEntityMinterface && oldPlayer is PlayerEntityMinterface) {
            newPlayer.setLesserSentinels(oldPlayer.getLesserSentinels())
            newPlayer.setEvocation(oldPlayer.getEvocation())
            newPlayer.setWristpocket(oldPlayer.getWristpocket())
        }
    }

	@SubscribeEvent
    fun onPlayerLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        val player = event.entity.uuid
        
        EvokeState.active[player] = false
        
        KeybindData.active[player]?.let { activeMap ->
            for (key in activeMap.keys) {
                activeMap[key] = false
                KeybindData.duration[player]?.set(key, 0)
            }
        }
    }
	
	@SubscribeEvent
    fun onServerTick(event: TickEvent.ServerTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            EvokeState.active.keys.forEach { player ->
                if (EvokeState.active[player] == true) {
                    EvokeState.duration[player] = EvokeState.duration[player]?.minus(1) ?: 0
                }
            }
            
            KeybindData.duration.keys.forEach { player ->
                KeybindData.active[player]?.let { activeMap ->
                    activeMap.keys.forEach { key ->
                        if (activeMap.getOrDefault(key, false)) {
                            KeybindData.duration[player]?.set(key, 
                                (KeybindData.duration[player]?.get(key) ?: 0) + 1)
                        }
                    }
                }
            }
        }
    }
}

@Mod.EventBusSubscriber(modid = HexicalMain.MOD_ID, value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.FORGE)
object HexicalClientEvents {
	val CHARMED_COLOR: TextColor = TextColor.fromRgb(0xe83d72)

 	@SubscribeEvent
    fun onTooltip(event: ItemTooltipEvent) {
        val stack = event.itemStack
        val lines = event.toolTip

        if (isStackCharmed(stack)) {
            val media = getMedia(stack)
            val maxMedia = getMaxMedia(stack)
			lines.add(Component.translatable("hexical.charmed").withStyle { style -> style.withColor(CHARMED_COLOR) })
			lines.add(Component.translatable("hexcasting.tooltip.media_amount.advanced",
				Component.literal(RenderUtils.DUST_AMOUNT.format((media / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { style -> style.withColor(ItemMediaHolder.HEX_COLOR) },
				Component.translatable("hexcasting.tooltip.media", RenderUtils.DUST_AMOUNT.format((maxMedia / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { style -> style.withColor(ItemMediaHolder.HEX_COLOR) },
				Component.literal(RenderUtils.PERCENTAGE.format((100f * media / maxMedia).toDouble()) + "%").withStyle { style -> style.withColor(TextColor.fromRgb(mediaBarColor(media, maxMedia))) }
			))
        }

		if (stack.item is ItemPackagedHex && stack.hasTag() && stack.tag?.getBoolean("cracked") == true) {
			val nbt = stack.tag ?: return
			if (stack.item !is ItemPackagedHex || !nbt.getBoolean("cracked"))
				return

			lines.add(Component.translatable("hexical.cracked.cracked").withStyle(ChatFormatting.GOLD))
			if (nbt.contains(ItemPackagedHex.TAG_PROGRAM)) {
				val text = Component.empty()
				val entries = nbt.getList(ItemPackagedHex.TAG_PROGRAM, Tag.TAG_COMPOUND.toInt())
				entries.forEach { text.append(IotaType.getDisplay(it as CompoundTag)) }
				lines.add(Component.translatable("hexical.cracked.program").append(text))
			}
        }

        stack.tag?.getList("autographs", 10)?.let { autographs ->
            val nbt = stack.tag ?: return
			if (!nbt.contains("autographs"))
				return

			lines.add(Component.translatable("hexical.autograph.header").withStyle { style -> style.withColor(ChatFormatting.GRAY) })

			nbt.getList("autographs", CompoundTag.TAG_COMPOUND.toInt()).forEach(Consumer { element: Tag? ->
				val compound = element as CompoundTag
				val name = compound.getString("name")
				val pigment = FrozenPigment.fromNBT(compound.getCompound("pigment")).colorProvider
				val output = Component.literal("")
				for (i in 0 until name.length)
					output.append(Component.literal(name[i].toString()).withStyle { style -> style.withColor(pigment.getColor((ClientStorage.ticks * 3).toFloat(), Vec3(0.0, i.toDouble(), 0.0))) })
				lines.add(output)
			})
        }
    }

 	@SubscribeEvent
    fun onDisconnect(event: ClientPlayerNetworkEvent.LoggingOut) {
        ShaderRenderer.setEffect(null)
    }
 	
	@SubscribeEvent
	fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            ClientStorage.ticks += 1
        }
    }

	@SubscribeEvent
    fun onWorldRender(event: RenderLevelStageEvent) {
        if (event.stage == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            val ctx = event.levelRenderer
            val matrices = event.poseStack
            val camera = Minecraft.getInstance().gameRenderer.mainCamera
            
            ClientStorage.lesserSentinels.forEach { pos ->
				val camPos = camera.position

				matrices.pushPose()
				matrices.translate(pos.x - camPos.x, pos.y - camPos.y, pos.z - camPos.z)

				matrices.mulPose(Axis.YP.rotationDegrees(-camera.yRot))
				matrices.mulPose(Axis.XP.rotationDegrees(camera.xRot))

				val tessellator = Tesselator.getInstance()
				val bufferBuilder = tessellator.builder

				RenderSystem.disableDepthTest()
				RenderSystem.enableBlend()
				RenderSystem.defaultBlendFunc()
				RenderSystem.disableCull()

				bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)

				val points = mutableListOf<Vec2>()
				for (i in 0..6) {
					val angle = (i % 6) * (Math.PI / 3)
					points.add(Vec2(cos(angle).toFloat(), sin(angle).toFloat()).scale(0.25f))
				}

				val pigment = IXplatAbstractions.INSTANCE.getPigment(Minecraft.getInstance().player!!).colorProvider
				fun makeVertex(offset: Vec2) = bufferBuilder.vertex(matrices.last().pose(), offset.x, offset.y, 0f)
					.color(pigment.getColor(ClientStorage.ticks.toFloat(), pos.add(offset.x.toDouble() * 2, offset.y.toDouble() * 2, 0.0)))
					.endVertex()
				RenderUtils.quadifyLines(::makeVertex, 0.05f, points)

				tessellator.end()

				RenderSystem.enableCull()
				RenderSystem.disableBlend()
				RenderSystem.enableDepthTest()

				matrices.popPose()
			}
        }
    }

	@SubscribeEvent
    fun onMouseScroll(event: InputEvent.MouseScrollingEvent) {
        if (HexicalKeybinds.TELEPATHY_KEYBIND.isDown()) {
            val delta = event.scrollDelta.toInt()
            val packet = HexicalNetworking.ScrollPacket(delta)
            HexicalNetworking.sendToServer(packet)
            event.isCanceled = true
        }
    }
}