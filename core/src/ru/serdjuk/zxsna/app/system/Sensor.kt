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



    fun resizeBounds() {
        // FIXME add world bounds changed
        screenBounds.set(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }

}

@ExperimentalUnsignedTypes
val sensor = lazy { Sensor() }.value