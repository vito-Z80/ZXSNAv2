package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ru.serdjuk.zxsna.app.resources.hexColor512
import ru.serdjuk.zxsna.app.system.res

@ExperimentalUnsignedTypes
class PaletteUnit(var intColor: Int, val unitRegion: TextureRegion) {

    companion object {
        val size = 16
    }

    private val hexBuffer = StringBuilder()

    fun colorId() = hexColor512.indexOfFirst { it.int == intColor }

    fun hex(): String {
        hexBuffer.clear()
        hexBuffer.append(intColor.toUInt().toString(16).toUpperCase())
        while (hexBuffer.length < 8) {
            hexBuffer.insert(0, "0")
        }
        return hexBuffer.toString()
    }

    fun color(): Color = Color(intColor)

    fun upgrade(intColor: Int) {
        this.intColor = intColor
        res.pixmap.setColor(intColor)
        res.pixmap.fillRectangle(
                unitRegion.regionX,
                unitRegion.regionY,
                unitRegion.regionWidth,
                unitRegion.regionHeight
        )
    }

    fun get9bit() = (colorId() / 2) or (colorId() % 2 shl 8)


}