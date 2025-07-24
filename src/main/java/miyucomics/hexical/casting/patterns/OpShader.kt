package miyucomics.hexical.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexical.registry.HexicalNetworking
import net.minecraft.world.entity.player.Player
import net.minecraft.server.level.ServerPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.network.FriendlyByteBuf
import io.netty.buffer.Unpooled

class OpShader(private val shader: ResourceLocation?) : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env.castingEntity !is Player)
			throw MishapBadCaster()
		val packet = HexicalNetworking.ShaderPacket(shader)
		HexicalNetworking.sendToPlayer(env.castingEntity as ServerPlayer, packet)
		return listOf()
	}
}