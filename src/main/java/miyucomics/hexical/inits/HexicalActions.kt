package miyucomics.hexical.inits

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.vecFromNBT
import at.petrak.hexcasting.common.casting.actions.selectors.OpGetEntitiesBy
import at.petrak.hexcasting.common.lib.hex.HexActions
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.akashic.OpClearAkashicShelf
import miyucomics.hexical.features.akashic.OpKeyAkashicShelf
import miyucomics.hexical.features.akashic.OpReadAkashicShelf
import miyucomics.hexical.features.akashic.OpWriteAkashicShelf
import miyucomics.hexical.features.animated_scrolls.OpAlterScroll
import miyucomics.hexical.features.animated_scrolls.OpColorScroll
import miyucomics.hexical.features.autographs.OpAutograph
import miyucomics.hexical.features.autographs.OpHasAutograph
import miyucomics.hexical.features.autographs.OpUnautograph
import miyucomics.hexical.features.block_mimicry.OpCook
import miyucomics.hexical.features.block_mimicry.OpDispense
import miyucomics.hexical.features.block_mimicry.OpStonecut
import miyucomics.hexical.features.breaking.OpBreakFortune
import miyucomics.hexical.features.breaking.OpBreakSilk
import miyucomics.hexical.features.charms.*
import miyucomics.hexical.features.circle.OpAbsorbArm
import miyucomics.hexical.features.circle.OpCreateDust
import miyucomics.hexical.features.circle.OpDisplace
import miyucomics.hexical.features.confection.OpConjureGummy
import miyucomics.hexical.features.confection.OpConjureHexburst
import miyucomics.hexical.features.confection.OpConjureHextito
import miyucomics.hexical.features.conjure.OpConjureEntity
import miyucomics.hexical.features.conjure.OpConjureFlower
import miyucomics.hexical.features.conjure.OpConjureLight
import miyucomics.hexical.features.conjure.OpConjureSpike
import miyucomics.hexical.features.cracked_items.OpCrackDevice
import miyucomics.hexical.features.dyes.OpDye
import miyucomics.hexical.features.dyes.OpGetDye
import miyucomics.hexical.features.dyes.OpTranslateDye
import miyucomics.hexical.features.evocation.OpGetEvocation
import miyucomics.hexical.features.evocation.OpSetEvocation
import miyucomics.hexical.features.grimoires.OpGrimoireErase
import miyucomics.hexical.features.grimoires.OpGrimoireIndex
import miyucomics.hexical.features.grimoires.OpGrimoireWrite
import miyucomics.hexical.features.hopper.OpHopper
import miyucomics.hexical.features.hopper.OpIndexHopper
import miyucomics.hexical.features.hotbar.OpGetHotbar
import miyucomics.hexical.features.hotbar.OpSetHotbar
import miyucomics.hexical.features.lamps.*
import miyucomics.hexical.features.lesser_sentinels.OpLesserSentinelGet
import miyucomics.hexical.features.lesser_sentinels.OpLesserSentinelSet
import miyucomics.hexical.features.lore.OpItemLore
import miyucomics.hexical.features.lore.OpItemName
import miyucomics.hexical.features.mage_blocks.OpConjureMageBlock
import miyucomics.hexical.features.mage_blocks.OpModifyMageBlock
import miyucomics.hexical.features.magic_missile.OpMagicMissile
import miyucomics.hexical.features.misc_actions.*
import miyucomics.hexical.features.particles.OpConfetti
import miyucomics.hexical.features.particles.OpSparkle
import miyucomics.hexical.features.pattern_manipulation.*
import miyucomics.hexical.features.peripherals.OpGetKeybind
import miyucomics.hexical.features.peripherals.OpGetScroll
import miyucomics.hexical.features.periwinkle.OpCompelSniffer
import miyucomics.hexical.features.pigments.OpSamplePigment
import miyucomics.hexical.features.pigments.OpTakeOnPigment
import miyucomics.hexical.features.pigments.OpToPigment
import miyucomics.hexical.features.prestidigitation.OpPrestidigitation
import miyucomics.hexical.features.pyrotechnics.OpConjureFirework
import miyucomics.hexical.features.pyrotechnics.OpSimulateFirework
import miyucomics.hexical.features.rotate.OpRotateBlock
import miyucomics.hexical.features.rotate.OpRotateEntity
import miyucomics.hexical.features.shaders.OpShader
import miyucomics.hexical.features.specklikes.OpKillSpecklike
import miyucomics.hexical.features.specklikes.OpSpecklikeProperty
import miyucomics.hexical.features.specklikes.Specklike
import miyucomics.hexical.features.specklikes.mesh.OpConjureMesh
import miyucomics.hexical.features.specklikes.mesh.OpReadMesh
import miyucomics.hexical.features.specklikes.mesh.OpWeaveMesh
import miyucomics.hexical.features.specklikes.speck.OpConjureSpeck
import miyucomics.hexical.features.specklikes.speck.OpIotaSpeck
import miyucomics.hexical.features.telepathy.OpHallucinateSound
import miyucomics.hexical.features.telepathy.OpSendTelepathy
import miyucomics.hexical.features.telepathy.OpShoutTelepathy
import miyucomics.hexical.features.wristpocket.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.LargeFireball
import net.minecraft.world.entity.projectile.LlamaSpit
import net.minecraft.world.entity.projectile.ThrownEgg
import net.minecraft.world.entity.projectile.Snowball
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import net.minecraft.core.Registry
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.phys.Vec3

object HexicalActions {
	fun init(event: RegisterEvent) {
		register(event, "normalize_scroll", "wqwawqwqawawa", HexDir.SOUTH_WEST, OpAlterScroll { it.setState(0) })
		register(event, "age_scroll", "wqwawqwqawwwdwdwwwa", HexDir.SOUTH_WEST, OpAlterScroll { it.setState(1) })
		register(event, "vanish_scroll", "wqwawqwqaqqa", HexDir.SOUTH_WEST, OpAlterScroll { it.setState(2) })
		register(event, "color_scroll", "wqwawqwqawawaedd", HexDir.SOUTH_WEST, OpColorScroll)
		register(event, "glow_scroll", "wqwawqwqawawaewdwdw", HexDir.SOUTH_WEST, OpAlterScroll { it.toggleGlow() })

		register(event, "write_grimoire", "aqwqaeaqa", HexDir.WEST, OpGrimoireWrite)
		register(event, "erase_grimoire", "aqwqaqded", HexDir.WEST, OpGrimoireErase)
		register(event, "index_grimoire", "aqaeaqwqa", HexDir.SOUTH_EAST, OpGrimoireIndex)

		register(event, "periwinkle", "wwwaqdadaadadqqqeaeq", HexDir.EAST, OpCompelSniffer)

		register(event, "wish", "eweweweweweewedeaqqqd", HexDir.NORTH_WEST, OpWish)
		register(event, "recharge_lamp", "qaqwawqwqqwqwqwqwqwqq", HexDir.EAST, OpRechargeLamp)
		register(event, "promote_lamp", "qweedeqeedeqdqdwewewwewewwewe", HexDir.WEST, OpPromoteLamp)
		register(event, "get_hand_lamp_position", "qwddedqdd", HexDir.SOUTH_WEST, OpGetHandLampData { _, nbt -> vecFromNBT(nbt.getCompound("position")).asActionResult })
		register(event, "get_hand_lamp_rotation", "qwddedadw", HexDir.SOUTH_WEST, OpGetHandLampData { _, nbt -> vecFromNBT(nbt.getCompound("rotation")).asActionResult })
		register(event, "get_hand_lamp_velocity", "qwddedqew", HexDir.SOUTH_WEST, OpGetHandLampData { _, nbt -> vecFromNBT(nbt.getCompound("velocity")).asActionResult })
		register(event, "get_hand_lamp_use_time", "qwddedqwddwa", HexDir.SOUTH_WEST, OpGetHandLampData { env, nbt -> (env.world.gameTime - (nbt.getDouble("start_time") + 1.0)).asActionResult })
		register(event, "get_hand_lamp_media", "qwddedaeeeee", HexDir.SOUTH_WEST, OpGetHandLampData { env, _ -> ((env.castingEntity!!.useItem.item as HandLampItem).getMedia(env.castingEntity!!.useItem).toDouble() / MediaConstants.DUST_UNIT).asActionResult })
		register(event, "get_hand_lamp_storage", "qwddedqwaqqqqq", HexDir.SOUTH_WEST, OpGetHandLampData { env, nbt -> listOf(IotaType.deserialize(nbt.getCompound("storage"), env.world)) })
		register(event, "set_hand_lamp_storage", "qwddedqedeeeee", HexDir.SOUTH_WEST, OpSetHandLampStorage)
		register(event, "get_arch_lamp_position", "qaqwddedqdd", HexDir.NORTH_EAST, OpGetArchLampData { _, data -> data.position.asActionResult })
		register(event, "get_arch_lamp_rotation", "qaqwddedadw", HexDir.NORTH_EAST, OpGetArchLampData { _, data -> data.rotation.asActionResult })
		register(event, "get_arch_lamp_velocity", "qaqwddedqew", HexDir.NORTH_EAST, OpGetArchLampData { _, data -> data.velocity.asActionResult })
		register(event, "get_arch_lamp_use_time", "qaqwddedqwddwa", HexDir.NORTH_EAST, OpGetArchLampData { ctx, data -> (ctx.world.gameTime - (data.time + 1)).asActionResult })
		register(event, "get_arch_lamp_storage", "qaqwddedqwaqqqqq", HexDir.NORTH_EAST, OpGetArchLampData { ctx, data -> listOf(IotaType.deserialize(data.storage, ctx.world)) })
		register(event, "set_arch_lamp_storage", "qaqwddedqedeeeee", HexDir.NORTH_EAST, OpSetArchLampStorage)
		register(event, "get_arch_lamp_media", "qaqwddedaeeeee", HexDir.NORTH_EAST, OpGetArchLampMedia)
		register(event, "has_arch_lamp", "qaqwddedqeed", HexDir.NORTH_EAST, OpIsUsingArchLamp)
		register(event, "lamp_finale", "aaddaddad", HexDir.EAST, OpGetFinale)

		register(event, "rotate_block", "edeeeeeweewadeeed", HexDir.EAST, OpRotateBlock)
		register(event, "rotate_entity", "qqqdaqqqa", HexDir.EAST, OpRotateEntity)

		register(event, "shuffle_pattern", "aqqqdae", HexDir.NORTH_EAST, OpShufflePattern)
		register(event, "congruent", "aaqd", HexDir.EAST, OpCongruentPattern)
		register(event, "similar", "aedd", HexDir.EAST, OpSimilarPattern)
		register(event, "serialize_pattern", "wqaedeqd", HexDir.EAST, OpSerializePattern)
		register(event, "deserialize_pattern", "wqqqaqwd", HexDir.EAST, OpDeserializePattern)
		register(event, "draw_pattern", "eadqqqa", HexDir.NORTH_EAST, OpDrawPattern)

		register(event, "get_telepathy", "wqqadaw", HexDir.EAST, OpGetKeybind("key.hexical.telepathy"))
		register(event, "send_telepathy", "qqqqwaqa", HexDir.EAST, OpSendTelepathy)
		register(event, "shout_telepathy", "daqqqqwa", HexDir.EAST, OpShoutTelepathy)
		register(event, "pling", "eqqqada", HexDir.NORTH_EAST, OpHallucinateSound(ForgeRegistries.SOUND_EVENTS.getHolder(SoundEvents.PLAYER_LEVELUP).orElseThrow()))
		register(event, "click", "eqqadaq", HexDir.NORTH_EAST, OpHallucinateSound(SoundEvents.UI_BUTTON_CLICK))
		register(event, "left_click", "qadee", HexDir.NORTH_EAST, OpGetKeybind("key.attack"))
		register(event, "right_click", "edaqq", HexDir.NORTH_WEST, OpGetKeybind("key.use"))
		register(event, "moving_up", "aqaddq", HexDir.SOUTH_EAST, OpGetKeybind("key.forward"))
		register(event, "moving_down", "dedwdq", HexDir.SOUTH_WEST, OpGetKeybind("key.back"))
		register(event, "moving_left", "edead", HexDir.SOUTH_EAST, OpGetKeybind("key.left"))
		register(event, "moving_right", "qaqda", HexDir.SOUTH_WEST, OpGetKeybind("key.right"))
		register(event, "jumping", "qaqdaqqa", HexDir.SOUTH_WEST, OpGetKeybind("key.jump"))
		register(event, "sneaking", "wede", HexDir.NORTH_WEST, OpGetKeybind("key.sneak"))
		register(event, "scroll", "qadeeee", HexDir.NORTH_EAST, OpGetScroll)

		register(event, "key_shelf", "qaqqadaq", HexDir.EAST, OpKeyAkashicShelf)
		register(event, "read_shelf", "qaqqqada", HexDir.EAST, OpReadAkashicShelf)
		register(event, "write_shelf", "edeeedad", HexDir.SOUTH_WEST, OpWriteAkashicShelf)
		register(event, "clear_shelf", "edeedade", HexDir.SOUTH_WEST, OpClearAkashicShelf)

		register(event, "conjure_mage_block", "dee", HexDir.NORTH_WEST, OpConjureMageBlock)
		register(event, "modify_block_bouncy", "deeqa", HexDir.NORTH_WEST, OpModifyMageBlock("bouncy"))
		register(event, "modify_block_energized", "deewad", HexDir.NORTH_WEST, OpModifyMageBlock("energized", 1))
		register(event, "modify_block_ephemeral", "deewwaawd", HexDir.NORTH_WEST, OpModifyMageBlock("ephemeral", 1))
		register(event, "modify_block_invisible", "deeqedeaqqqwqqq", HexDir.NORTH_WEST, OpModifyMageBlock("invisible"))
		register(event, "modify_block_replaceable", "deewqaqqqqq", HexDir.NORTH_WEST, OpModifyMageBlock("replaceable"))
		register(event, "modify_block_volatile", "deewedeeeee", HexDir.NORTH_WEST, OpModifyMageBlock("volatile"))

		register(event, "autograph", "eeeeeww", HexDir.WEST, OpAutograph)
		register(event, "unautograph", "wwqqqqq", HexDir.NORTH_EAST, OpUnautograph)
		register(event, "has_autograph", "wwqqqqqaw", HexDir.NORTH_EAST, OpHasAutograph)

		register(event, "get_dye", "weedwa", HexDir.NORTH_EAST, OpGetDye)
		register(event, "dye", "dwaqqw", HexDir.NORTH_WEST, OpDye)
		register(event, "translate_dye", "wdwwaawwewdwwewwdwwe", HexDir.EAST, OpTranslateDye)

		register(event, "magic_missile", "qaqww", HexDir.WEST, OpMagicMissile)

		register(event, "to_pigment", "aqwedeweeeewweeew", HexDir.NORTH_WEST, OpToPigment)
		register(event, "sample_pigment", "edewqaqqqqqwqqq", HexDir.SOUTH_EAST, OpSamplePigment)
		register(event, "take_on_pigment", "weeeweeqeeeewqaqweeee", HexDir.EAST, OpTakeOnPigment)

		register(event, "prestidigitation", "wedewedew", HexDir.NORTH_EAST, OpPrestidigitation)

		register(event, "wristpocket", "aaqqa", HexDir.WEST, OpWristpocket)
		register(event, "wristpocket_item", "aaqqada", HexDir.WEST, OpGetWristpocket)
		register(event, "sleight", "aaqqadeeeq", HexDir.WEST, OpSleight)
		register(event, "mage_hand", "aaqqaeea", HexDir.WEST, OpMageHand)
		register(event, "mage_mouth", "aaqqadaa", HexDir.WEST, OpMageMouth)

		register(event, "conjure_speck", "ade", HexDir.SOUTH_WEST, OpConjureSpeck)
		register(event, "iota_speck", "adeeaqa", HexDir.SOUTH_WEST, OpIotaSpeck)
		register(event, "kill_specklike", "adeaqde", HexDir.SOUTH_WEST, OpKillSpecklike)
		register(event, "move_specklike", "adeqaa", HexDir.SOUTH_WEST, OpSpecklikeProperty(0))
		register(event, "rotate_specklike", "adeaw", HexDir.SOUTH_WEST, OpSpecklikeProperty(1))
		register(event, "roll_specklike", "adeqqqqq", HexDir.SOUTH_WEST, OpSpecklikeProperty(2))
		register(event, "size_specklike", "adeeqed", HexDir.SOUTH_WEST, OpSpecklikeProperty(3))
		register(event, "thickness_specklike", "adeeqw", HexDir.SOUTH_WEST, OpSpecklikeProperty(4))
		register(event, "lifetime_specklike", "adeqqaawdd", HexDir.SOUTH_WEST, OpSpecklikeProperty(5))
		register(event, "pigment_specklike", "adeqqaq", HexDir.SOUTH_WEST, OpSpecklikeProperty(6))
		register(event, "zone_specklike", "qqqqqwdeddwqde", HexDir.SOUTH_EAST, OpGetEntitiesBy({ entity -> entity is Specklike }, false))

		register(event, "egg", "qqqwaqaaqeeewdedde", HexDir.SOUTH_EAST, OpConjureEntity(MediaConstants.DUST_UNIT * 2) { world, position, caster ->
			val egg = ThrownEgg(world, position.x, position.y, position.z)
			egg.owner = caster
			return@OpConjureEntity egg
		})
		register(event, "llama_spit", "dwqaqw", HexDir.EAST, OpConjureEntity(MediaConstants.DUST_UNIT / 4) { world, position, caster ->
			val spit = LlamaSpit(EntityType.LLAMA_SPIT, world)
			spit.setPos(position)
			spit.owner = caster
			return@OpConjureEntity spit
		})
		register(event, "snowball", "ddeeeeewd", HexDir.NORTH_EAST, OpConjureEntity(MediaConstants.DUST_UNIT / 2) { world, position, caster ->
			val snowball = Snowball(world, position.x, position.y, position.z)
			snowball.owner = caster
			return@OpConjureEntity snowball
		})
		register(event, "ghast_fireball", "wqqqqqwaeaeaeaeae", HexDir.SOUTH_EAST, OpConjureEntity(MediaConstants.DUST_UNIT * 3) { world, position, caster ->
			val fireball = LargeFireball(world, caster, 0.0, 0.0, 0.0, 1)
			fireball.setPos(position)
			return@OpConjureEntity fireball
		})

		register(event, "confetti", "awddeqaedd", HexDir.EAST, OpConfetti)
		register(event, "vibration", "wwawawwd", HexDir.EAST, OpVibrate)
		register(event, "sparkle", "dqa", HexDir.NORTH_EAST, OpSparkle)
		register(event, "crack_device", "wwaqqqqqeqdedwqeaeqwdedwqeaeq", HexDir.EAST, OpCrackDevice)
		register(event, "flower", "weqqqqqwaeaeaeaeaea", HexDir.NORTH_EAST, OpConjureFlower)
		register(event, "light", "aeaeaeaeaeawqqqqq", HexDir.SOUTH_EAST, OpConjureLight)
		register(event, "gasp", "aweeeeewaweeeee", HexDir.NORTH_WEST, OpGasp)
		register(event, "parrot", "wweedadw", HexDir.NORTH_EAST, OpImitateParrot)

		register(event, "break_fortune", "qaqqqqqdeeeqeee", HexDir.EAST, OpBreakFortune)
		register(event, "break_silk", "aqaeaqdeeweweedq", HexDir.EAST, OpBreakSilk)

		register(event, "conjure_gummy", "eeewdw", HexDir.SOUTH_WEST, OpConjureGummy)
		register(event, "conjure_hexburst", "aadaadqaq", HexDir.EAST, OpConjureHexburst)
		register(event, "conjure_hextito", "qaqdqaqdwawaw", HexDir.EAST, OpConjureHextito)

		register(event, "spike", "qdqdqdqdww", HexDir.NORTH_EAST, OpConjureSpike)

		register(event, "dispense", "wqwawqwddaeeead", HexDir.SOUTH_WEST, OpDispense)
		register(event, "smelt", "qwqqadadadewewewe", HexDir.SOUTH_EAST, OpCook(RecipeType.SMELTING, "target.smelting"))
		register(event, "roast", "aqqwwqqawdadedad", HexDir.NORTH_WEST, OpCook(RecipeType.CAMPFIRE_COOKING, "target.roasting"))
		register(event, "smoke", "qwqqadadadewdqqdwe", HexDir.SOUTH_EAST, OpCook(RecipeType.SMOKING, "target.smoking"))
		register(event, "blast", "qwqqadadadewweewwe", HexDir.SOUTH_EAST, OpCook(RecipeType.BLASTING, "target.blasting"))
		register(event, "stonecut", "qqqqqwaeaeaeaeaeadawa", HexDir.EAST, OpStonecut)

		register(event, "displace", "qaqqqqeedaqqqa", HexDir.NORTH_EAST, OpDisplace)
		register(event, "absorb_arm", "aaqqadaqwqa", HexDir.WEST, OpAbsorbArm)
		register(event, "create_dust", "eaqwedqdqddqqwae", HexDir.SOUTH_WEST, OpCreateDust)

		register(event, "get_evocation", "wwdeeeeeqeaqawwewewwaqawwewew", HexDir.EAST, OpGetEvocation)
		register(event, "set_evocation", "wwaqqqqqeqdedwwqwqwwdedwwqwqw", HexDir.EAST, OpSetEvocation)
		register(event, "is_evoking", "wwaqqqqqeeaqawwewewwaqawwewew", HexDir.EAST, OpGetKeybind("key.hexical.evoke"))

		register(event, "conjure_firework", "dedwaqwwawwqa", HexDir.SOUTH_WEST, OpConjureFirework)
		register(event, "simulate_firework", "dedwaqwqqwqa", HexDir.SOUTH_WEST, OpSimulateFirework)

		register(event, "get_hotbar", "qwawqwa", HexDir.EAST, OpGetHotbar)
		register(event, "set_hotbar", "dwewdwe", HexDir.WEST, OpSetHotbar)

		register(event, "set_lesser_sentinels", "aeaae", HexDir.EAST, OpLesserSentinelSet)
		register(event, "get_lesser_sentinels", "dqddq", HexDir.WEST, OpLesserSentinelGet)

		register(event, "item_name", "qwawqwaadwa", HexDir.SOUTH_EAST, OpItemName)
		register(event, "item_lore", "dwewdweedwa", HexDir.NORTH_WEST, OpItemLore)

		register(event, "shader_clear", "eeeeeqaqeeeee", HexDir.WEST, OpShader(null))
		register(event, "shader_owl", "edewawede", HexDir.WEST, OpShader(HexicalMain.id("shaders/post/night_vision.json")))
		register(event, "shader_lines", "eedwwawwdee", HexDir.WEST, OpShader(HexicalMain.id("shaders/post/outlines_only.json")))
		register(event, "shader_tv", "wewdwewwawwewdwew", HexDir.WEST, OpShader(HexicalMain.id("shaders/post/television.json")))
		register(event, "shader_media", "eewdweqaqewdwee", HexDir.WEST, OpShader(HexicalMain.id("shaders/post/media.json")))
		register(event, "shader_spider", "qaqdedaedqqdedaqaedeqd", HexDir.NORTH_EAST, OpShader(HexicalMain.id("shaders/post/spider.json")))
		// color shift - edqdeqaqedqde

		register(event, "hopper", "qwawqwaeqqq", HexDir.SOUTH_EAST, OpHopper)
		register(event, "index_hopper", "qqqeawqwawq", HexDir.SOUTH_WEST, OpIndexHopper)

		register(event, "horrible", "wedqawqeewdeaqeewdeaqqedqawqqedqawqeedqawqqewdeaqeedqawqeewdeaqqewdeaqeewdeaqeedqawqqedqawqqewdeaqeedqawqeewdeaqqewdeaqeewdeaqeedqawqqedqawqqewdeaqqedqawqeewdeaqeewdeaqqedqawqqedqawqeedqawqqewdeaqqedqawqeewdeaqeewdeaqqedqawqqedqawqeedqawqqewdeaqeedqawqeewdeaqeewdeaqqedqawqqedqawqeedqawqqewdeaqqedqawqeewdeaqqewdeaqeewdeaqeedqawqqedqawqqewdeaqe", HexDir.EAST, OpHorrible)

		register(event, "charm", "edeeeeeqaaqeeeadweeqeeqdqeeqeeqde", HexDir.SOUTH_EAST, OpCharmItem)
		register(event, "write_charmed", "waqqqqqedeqdqdqdqdqe", HexDir.NORTH_EAST, OpWriteCharmed)
		register(event, "read_charmed", "waqqqqqeaqeaeaeaeaeq", HexDir.NORTH_EAST, OpReadCharmed)
		register(event, "write_charmed_proxy", "edewqaqqdeeeee", HexDir.SOUTH_EAST, OpProxyWriteCharmed)
		register(event, "read_charmed_proxy", "qaqwedeeaqqqqq", HexDir.NORTH_EAST, OpProxyReadCharmed)
		register(event, "discharm", "qaqwddaaeawaea", HexDir.NORTH_EAST, OpDischarmItem)

		register(event, "greater_blink", "wqawawaqwqwqawawaqw", HexDir.SOUTH_WEST, OpGreaterBlink)

		register(event, "conjure_mesh", "qaqqqqqwqqqdeeweweeaeewewee", HexDir.EAST, OpConjureMesh)
		register(event, "weave_mesh", "qaqqqqqwqqqdeewewee", HexDir.EAST, OpWeaveMesh)
		register(event, "read_mesh", "edeeeeeweeeaqqwqwqq", HexDir.SOUTH_WEST, OpReadMesh)
	}

	private fun register(event: RegisterEvent, name: String, signature: String, startDir: HexDir, action: Action) =
		event.register(HexActions.REGISTRY.key(), HexicalMain.id(name)) { ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action) }
}
