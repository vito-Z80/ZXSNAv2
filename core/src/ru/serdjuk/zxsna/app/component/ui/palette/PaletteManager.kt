package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Container
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res

@ExperimentalUnsignedTypes
class PaletteManager : Container<Group>() {

    val skin = module.skin
    val atlas = res.atlas
    val atlasUtils = module.atlasUtils
    val group = Group().also { it.top;it.toFront() }
//    private val colorsNumber = hexColor512.size

    /*
    границы ректанглов относительно актора
     */
//    val actorBounds = Array<Rectangle>(colorsNumber) {
//        val x = PaletteData.space + (it and 15) * PaletteData.cellSize + PaletteData.space * (it and 15)
//        val y = PaletteData.space + (it and 496) + PaletteData.space * ((it and 496) / 16)
//        Rectangle(x.toFloat(), y.toFloat(), PaletteData.cellSize.toFloat(), PaletteData.cellSize.toFloat())
//    }



    val levitationCell = LevitationCell()
    @ExperimentalUnsignedTypes
    val userPaletteImage = UserPaletteImage()
    val mainPaletteImage = MainPaletteImage(levitationCell)
    val overlayImage = OverlayImage()


    /*
регион перемещаемой ячейки с установленным цветом
*/
//    val levitationCell = LevitationCell(this)

    val headPalette = HeadPalette(this)
//
//    /*
//    картинка пользовательской таблицы (палитры)
//     */

//    val userTablePane = DraggedPane(res.skin, content = userTable)
//    val overlayPane = OverlayPane(this)
//
//    /*
//    картинка глобальной таблицы (палитры)
//     */
//    val mainTable = DraggedPane(res.skin, content = MainPaletteTable(this))

//    val paletteSpinner = PaletteSpinner(this)

//    val infoTable = PaletteInfoTable(this)


//    val paletteOptions = PaletteOptions(this)

    init {


        group.addActor(headPalette)
        group.addActor(headPalette.content)
        group.addActor(userPaletteImage)
        group.addActor(mainPaletteImage)
        group.addActor(overlayImage)

        group.addActor(levitationCell)

//        group.addActor(headPalette.label)
//        group.addActor(headPalette.content)
//        group.addActor(userTable)
//        group.addActor(userTablePane)
//        userTable.toFront()
//        group.addActor(overlayPane)
//        group.addActor(mainTable)
//        group.addActor(mainTable.content)
//        group.addActor(levitationCell)
//        group.addActor(paletteOptions)
//        group.addActor(infoTable)
        group.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                group.toFront()
                return super.touchDown(event, x, y, pointer, button)
            }
        })
//        upgradeTexture()
        actor = group
    }


    /*
    find region contains XY. return -1 if not contains
    ректанглы ячеек на картинке - их размеры одинаковы на обоих картинках пилитр
     */
//    fun getCellId(x: Float, y: Float) = let {
//        var index: Int? = null
//        actorBounds.forEachIndexed regionId@{ id, rectangle ->
//            if (rectangle.contains(x, y)) {
//                index = id
//                return@regionId
//            }
//        }
//        index
//    }
//
//    fun getCellPosition(x: Float, y: Float): Vector2? {
//        actorBounds.forEachIndexed regionId@{ id, rectangle ->
//            if (rectangle.contains(x, y)) {
//                return Vector2(rectangle.x, rectangle.y)
//            }
//        }
//        return null
//    }

    /*
    draw rectangle around cell region
     */
//    fun drawAroundCell(x: Float, y: Float, color: Int, table: Array<PaletteCell>) {
//        val id = getCellId(x, y) ?: return
//        if (id > table.size - 1) return
//        val region = table[id].textureRegion
//        res.pixmap.setColor(color)
//        res.pixmap.drawRectangle(region.regionX - 1, region.regionY - 1, region.regionWidth + 2, region.regionHeight + 2)
////        texture.draw(bitmap, 0, 0)
//    }


//    fun showHexColorInTitle(x: Float, y: Float, paletteTable: Array<PaletteCell>) {
//        val id = getCellId(x, y) ?: return
//        if (id > paletteTable.size - 1) return
////        headPalette.label.setText("\t#${paletteTable[id].hexColor.dropLast(2)}/${paletteTable[id].colorId}/${id and 15}")
//    }

//    fun upgradeTexture() {
//
////        res.uploadTexture()
//
////        GeneralRender.texture.draw(GeneralRender.pixmap, 0, 0)
//    }

//    fun uploadData(byteArray: ByteArray) {
//        // если бит 9bpp включен то умножаем первый байт на 2 + 1
//        userTable.table.forEachIndexed { id, cell ->
//            val colorId = if (byteArray[id * 2 + 1].toUByte().toInt() == 0) {
//                byteArray[id * 2].toUByte().toInt() * 2
//            } else {
//                byteArray[id * 2].toUByte().toInt() * 2 + 1
//            }
//            cell.colorId = colorId
//            cell.hexColor = hexColor512[colorId]
//            userTable.drawCell(id, colorId)
//        }
//        upgradeTexture()
////        infoTable.changeTextData()
//    }

}

