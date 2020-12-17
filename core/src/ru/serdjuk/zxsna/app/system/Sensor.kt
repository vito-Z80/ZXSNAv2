package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import kotlin.math.abs

@ExperimentalUnsignedTypes
class Sensor {

    val screenBounds = Rectangle()
    val worldBounds = Rectangle()

    val screenMouseYUp = Vector2()
    val screenMouseYDown = Vector2()
    private val mouseJob = Vector2()
    val preMouse = Vector2()
    val worldMouse = Vector2()
    fun mouseRefresh() {
        worldMouse.set(module.worldViewport.unproject(mouseJob.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())))
    }

    var mouseWheel = 0
    var paletteOffset = 0
    var progressBarIndicator = 0

    val selectRectangle = Rectangle()

    // позиция доставленной ячейки из главной палитры в пользовательскую
//    val landingCell = PaletteCell(-1, "", PaletteData.paletteTables.mainRegion)

    /**
     * преобразует выделение в правильный прямоугольник
     * X,Y = bottomLeft & WIDTH,HEIGHT = to up, to right
     */
    fun transformSelectionRectangle() {
        val x = if (sensor.selectRectangle.width < 0) (sensor.selectRectangle.x + sensor.selectRectangle.width) else sensor.selectRectangle.x
        val y = if (sensor.selectRectangle.height < 0) (sensor.selectRectangle.y + sensor.selectRectangle.height) else sensor.selectRectangle.y
        val width = abs(sensor.selectRectangle.width)
        val height = abs(sensor.selectRectangle.height)
        selectRectangle.set(x, y, width, height)
    }

    fun resizeBounds() {
        // FIXME add world bounds changed
        screenBounds.set(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }

}

@ExperimentalUnsignedTypes
val sensor = lazy { Sensor() }.value