package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.graphics.Color
import ru.serdjuk.zxsna.app.resources.hexColor512

@ExperimentalUnsignedTypes
object PaletteData {

    var appTransparentColor = Color.CLEAR
    var transparentColorId = 455
    var transparentColorInt = hexColor512[455].int
    val transparentColorHex = hexColor512[455].hex
    val transparentColor = Color(hexColor512[455].int)

    var colorId = 0
    var intColor = 0
    var hexColor = "0"
    val color = Color(0)

    var space = 1
    var cellSize = 16
    private val width = 16
    fun tableWidth() = width * cellSize + space * cellSize + space
    fun tableHeight256() = tableWidth()
    fun tableHeight512() = tableWidth() * 2

    //  create palette textures and tables
    val paletteTables = PaletteTexturesCreator()

//    fun setColor(color: Int) {
//        colorInt = color
//        colorId = hexColor512.indexOfFirst { it.int == color }
//        colorHex = hexColor512[colorId].hex
//        this.color.set(colorInt)
//    }

    fun setColor(colorId: Int) {
        intColor = hexColor512[colorId].int
        this.colorId = colorId
        hexColor = hexColor512[colorId].hex
        this.color.set(intColor)
    }

}