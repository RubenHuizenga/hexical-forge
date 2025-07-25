package miyucomics.hexical.features.specklikes

import at.petrak.hexcasting.api.pigment.FrozenPigment

interface Specklike {
	fun setRoll(rotation: Float)
	fun setSize(size: Float)
	fun setThickness(thickness: Float)
	fun setLifespan(lifespan: Int)
	fun setPigment(pigment: FrozenPigment)
}