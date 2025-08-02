package miyucomics.hexical.features.curios.curios

import dev.kosmx.playerAnim.api.layered.ModifierLayer
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.resources.model.ModelResourceLocation

object HandbellCurioItemModel : InitHook() {
	@JvmField val heldHandbellModel: ModelResourceLocation = ModelResourceLocation("hexical", "held_curio_handbell", "inventory")
	@JvmField val handbellModel: ModelResourceLocation = ModelResourceLocation("hexical", "curio_handbell", "inventory")
	val clientReceiver = HexicalMain.id("handbell")

	override fun init() {
		// Done in the handle of HandbellCurioPacket in HandbellCurio
		// ClientPlayNetworking.registerGlobalReceiver(clientReceiver) { client, handler, buf, responseSender ->
		// 	val player = client.world!!.getPlayerByUuid(buf.readUuid()) ?: return@registerGlobalReceiver
		// 	val handbellAnimation = (PlayerAnimationAccess.getPlayerAssociatedData(player as AbstractClientPlayer).get(clientReceiver) as ModifierLayer<HandbellCurioPlayerModel>).animation
		// 	handbellAnimation!!.shakingBellTimer = 10
		// }
	}
}