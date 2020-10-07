package ru.serdjuk.zxsna.app.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.serdjuk.zxsna.app.component.ui.palette.PaletteData
import ru.serdjuk.zxsna.app.resources.hexColor512
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.system.sensor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

val worldMouseDown = Vector2(-100f, -100f)

@ExperimentalUnsignedTypes
fun buttonHold(key: Int): Boolean {
    return Gdx.input.isButtonPressed(key)
}

@ExperimentalUnsignedTypes
fun buttonOnce(key: Int): Boolean {
    return Gdx.input.isButtonJustPressed(key)
}

@ExperimentalUnsignedTypes
fun buttonUp(button: Int): Boolean {
    if (buttonOnce(button)) {
        worldMouseDown.set(sensor.worldMouse)
    }
    if (!buttonHold(button) && (worldMouseDown.x == sensor.worldMouse.x &&
                    worldMouseDown.y == sensor.worldMouse.y)) {
        worldMouseDown.setZero()
        return true
    }
    return false
}

fun keyHold(key: Int) = Gdx.input.isKeyPressed(key)
fun keyOnce(key: Int) = Gdx.input.isKeyJustPressed(key)

@ExperimentalUnsignedTypes
inline fun <reified T> isActorExists() = module.stage.root.children?.any { it is T } ?: false

@ExperimentalUnsignedTypes
fun outActor() = module.stage.root.hit(Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat(), true) == null
fun isPointInPolygon(polygon: FloatArray, x: Float, y: Float) = Intersector.isPointInPolygon(polygon, 0, polygon.size, x, y)
fun Color.toInt(): Int {
    return (255 * r).toInt() shl 24 or ((255 * g).toInt() shl 16) or ((255 * b).toInt() shl 8) or (255 * a).toInt()
}

val currentBackgroundColor = Color(0.2f, 0.2f, 0.2f, 1f)


