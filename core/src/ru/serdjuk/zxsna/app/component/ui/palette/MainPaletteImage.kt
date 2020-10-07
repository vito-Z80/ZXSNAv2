package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res

@ExperimentalUnsignedTypes
class MainPaletteImage(private val levitationCell: LevitationCell) : Container<Image>() {

    private val region = module.skin.getRegion(UI.MAIN_PALETTE)
    private val cell = Rectangle()
    private val point = Vector2()

    init {
        background = module.skin.getDrawable(UI.DRAGGED_PANE)
        actor = Image(region)
        actor.addListener(listener())
        actor.pack()
        pack()
    }

    private fun listener() = object : InputListener() {
        override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
            val cellId = getCellId(x, actor.height - y)
            if (cellId > -1) {
                if (!PaletteUtils.actorBounds[cellId].overlaps(cell)) {
                    drawCell(cell, Color.SLATE)
                }
                cell.set(PaletteUtils.actorBounds[cellId])
                drawCell(cell, Color.BLACK)
                res.textureUpdate()
            }
            return super.mouseMoved(event, x, y)
        }

        override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
            drawCell(cell, Color.SLATE)
            res.textureUpdate()
            super.exit(event, x, y, pointer, toActor)
        }

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            drawCell(cell, Color.SLATE)
            res.textureUpdate()

            val colorId = PaletteUtils.actorBounds.indexOfFirst { it.overlaps(cell) }

//            val cellColor = res.pixmap.getPixel(
//                    (cell.x + 1 + region.regionX).toInt(),
//                    (cell.y + 1 + region.regionY).toInt()
//            )

            point.set(cell.x, actor.height - cell.y - PaletteData.cellSize)
            actor.localToStageCoordinates(point)
            levitationCell.show(colorId, point)
            return super.touchDown(event, x, y, pointer, button)
        }
    }

    private fun getCellId(x: Float, y: Float): Int {
        for (c in PaletteUtils.actorBounds.indices) {
            if (PaletteUtils.actorBounds[c].contains(x, y)) {
                return c
            }
        }
        return -1
    }

    private fun drawCell(r: Rectangle, color: Color) {
        res.pixmap.setColor(color)
        res.pixmap.drawRectangle(
                r.x.toInt() + region.regionX - 1,
                r.y.toInt() + region.regionY - 1,
                r.width.toInt() + 2,
                r.height.toInt() + 2
        )
    }
}