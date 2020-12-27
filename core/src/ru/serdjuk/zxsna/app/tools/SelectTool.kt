package ru.serdjuk.zxsna.app.tools

import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.keys.KEYS
import ru.serdjuk.zxsna.app.keys.keys
import ru.serdjuk.zxsna.app.layers.AppLayersSystem
import ru.serdjuk.zxsna.app.menus.appMenu
import ru.serdjuk.zxsna.app.system.*
import ru.serdjuk.zxsna.app.utils.buttonHold
import ru.serdjuk.zxsna.app.utils.buttonOnce
import kotlin.math.abs

@ExperimentalUnsignedTypes
class SelectTool : ITools() {


    // TODO display label (position info) be correctly
    private val boundsInfo = Label("000\n000", module.skin, UI.LABEL_BN_LIGHT_BLACK).also {
        it.pack()
        it.touchable = Touchable.disabled
        it.isVisible = false
    }


    override var toolName = ToolName.SELECT
    override fun undo(data: UR?) {

    }

    init {
        module.stage.addActor(boundsInfo) //        createMenu()
    }


    override fun update(delta: Float) {


        val area = system.get<AppLayersSystem>()?.getLayer()?.selectionArea
        if (area != null) {

            if (toolName == AppToolsSystem.usedTool) {
                select(area)
                showMenu(area)
            }

            // clear selection if pressed
            if (keys.isPressed(KEYS.CLEAR_SELECTION)) {
                area.set(0f, 0f, 0f, 0f)
                boundsInfo.isVisible = false
            }
        }


    }

    private fun showMenu(area: Rectangle) {
        if (buttonOnce(1)) {
            val x =
                if (area.width < 0) (area.x + area.width).toInt()
                else area.x.toInt()
            val y =
                if (area.height < 0) (area.y + area.height).toInt()
                else area.y.toInt()
            val width = abs(area.width).toInt()
            val height = abs(area.height).toInt()
            if (Rectangle.tmp.set(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
                    .contains(sensor.worldMouse)
            ) {
                appMenu.highlightedArea.show()
            }
        }
    }

    override fun draw(batch: SpriteBatch) {

    }


    override fun draw(shape: ShapeRenderer) {

    }

    private var firstX = 0f
    private var firstY = 0f

    private fun select(area: Rectangle) {
        if (buttonOnce(0)) {
            firstX = sensor.worldMouse.x.toInt().toFloat()
            firstY = sensor.worldMouse.y.toInt().toFloat()

            area.set(firstX, firstY, 0f, 0f)
            boundsInfo.setPosition(sensor.screenMouseYUp.x, sensor.screenMouseYUp.y)
            boundsInfo.isVisible = true
        }
        if (buttonHold(0)) {
            cursor.setSystem(Cursor.SystemCursor.Crosshair)
            boundsInfo.setPosition(sensor.screenMouseYUp.x, sensor.screenMouseYUp.y)
            boundsInfo.setText("W:${abs(area.width).toInt()}\nH:${abs(area.height).toInt()}")
            val n = 1

            if (sensor.worldMouse.x > area.x) {
                area.width = sensor.worldMouse.x.toInt().toFloat() - area.x + n
                area.x = firstX
            } else {
                area.width = sensor.worldMouse.x.toInt().toFloat() - area.x
                area.x = firstX + n
            }
            if (sensor.worldMouse.y > area.y) {
                area.height = sensor.worldMouse.y.toInt().toFloat() - area.y + n
                area.y = firstY
            } else {
                area.height = sensor.worldMouse.y.toInt().toFloat() - area.y
                area.y = firstY + n
            }
        }
    }

    /**
     * преобразует выделение в правильный прямоугольник
     * X,Y = bottomLeft & WIDTH,HEIGHT = to up, to right
     */
    fun transformSelectionRectangle(area: Rectangle) {
        val x =
            if (area.width < 0) (area.x + area.width) else area.x
        val y =
            if (area.height < 0) (area.y + area.height) else area.y
        area.set(x, y, abs(area.width), abs(area.height))
    }

    //---------------------------------------------------------------------------


}