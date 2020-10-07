package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ru.serdjuk.zxsna.app.utils.buttonOnce
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.resources.hexColor512
import ru.serdjuk.zxsna.app.system.file
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.system.sensor


@ExperimentalUnsignedTypes
class UserPaletteImage : Container<Image>() {

    private val region = module.skin.getRegion(UI.USER_PALETTE)
    private val selectedCell = Rectangle()
    private val point = Vector2()
    private val menu = PopupMenu(UI.DEFAULT)

    init {
        background = module.skin.getDrawable(UI.DRAGGED_PANE)
        actor = Image(region)
        actor.addListener(listener())
        actor.pack()
        pack()
        addMenuItems()

    }

    @ExperimentalUnsignedTypes
    private fun addMenuItems() {
        val saveText = ": Save"
        val loadText = ": Load"
        val save = MenuItem(saveText.drop(2))
        val load = MenuItem(loadText.drop(2))
        load.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                file.loadPalette4bpp()
                res.textureUpdate()
            }
        })
        save.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (!save.isChecked) {
                    save.text = saveText.drop(2)
                } else {
                    save.text = saveText
                }
                file.savePalette4bpp()
            }
        })

        save.pack()
        load.pack()
        menu.addItem(load)
        menu.addItem(save)
        menu.pack()
    }

    private fun listener() = object : DragListener() {


        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            if (button == 0) {
                val cellId = PaletteUtils.getCellId(x, actor.height - y)
                if (cellId > -1 && !PaletteUtils.actorBounds[cellId].overlaps(selectedCell)) {
                    val line = cellId / 16
                    val layer = res.layers[line]
                    if (layer.lastCellId != null) {
                        drawCellRectangle(PaletteUtils.actorBounds[layer.lastCellId!!], getCellIntColor(layer.lastCellId!!))
                    }
                    PaletteData.setColor(PaletteData.paletteTables.userTable[cellId].colorId)
                    selectedCell.set(PaletteUtils.actorBounds[cellId])
                    drawCellRectangle(selectedCell, hexColor512[0].int)
                    layer.lastCellId = cellId
                    res.textureUpdate()
                }
            }
            return super.touchDown(event, x, y, pointer, -1)
        }

        override fun handle(e: Event?): Boolean {
            // TODO убрать нахой отсюдО
            if (buttonOnce(1)) {
                menu.showMenu(module.stage, sensor.screenMouse.x, sensor.screenMouse.y)
                menu.toFront()
            }
            return super.handle(e)
        }


    }

    override fun act(delta: Float) {
        if (sensor.landingCell.colorId > -1) {
            drawSentColor(sensor.landingCell.colorId)
            sensor.landingCell.colorId = -1
        }
        super.act(delta)
    }

    private fun drawSentColor(colorId: Int) {
        // TODO вычислить координаты и если присланный цвет попал в ячейку палитры = отрисовать присланный цвет
        point.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        point.set(actor.screenToLocalCoordinates(point))
        point.y = actor.height - point.y
        val cell = PaletteUtils.getCellId(point.x, point.y)
        if (cell > -1) {
            val r = PaletteUtils.actorBounds[cell]
            drawCellFill(r, hexColor512[colorId].int)
            PaletteData.setColor(colorId)
            PaletteData.paletteTables.userTable[cell].colorId = colorId
            PaletteData.paletteTables.userTable[cell].hexColor = hexColor512[colorId].hex
//            PaletteData.paletteTables.userTable[cell].textureRegion.setRegion(r.x.toInt(), r.y.toInt(), r.width.toInt(), r.height.toInt())
            res.textureUpdate()
        }
    }

    private fun getCellIntColor(cellId: Int) = res.pixmap.getPixel(
            (PaletteUtils.actorBounds[cellId].x + 1 + region.regionX).toInt(),
            (PaletteUtils.actorBounds[cellId].y + 1 + region.regionY).toInt()
    )

    private fun drawCellFill(r: Rectangle, color: Int) {
        res.pixmap.setColor(color)
        res.pixmap.fillRectangle(
                r.x.toInt() + region.regionX,
                r.y.toInt() + region.regionY,
                r.width.toInt(),
                r.height.toInt()
        )
    }

    private fun drawCellRectangle(r: Rectangle, color: Int) {
        res.pixmap.setColor(color)
        res.pixmap.drawRectangle(
                r.x.toInt() + region.regionX,
                r.y.toInt() + region.regionY,
                r.width.toInt(),
                r.height.toInt()
        )
    }


}