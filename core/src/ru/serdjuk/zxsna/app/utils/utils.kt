package ru.serdjuk.zxsna.app.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ru.serdjuk.zxsna.app.system.compression.Compression
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor
import java.util.*
import javax.xml.crypto.Data
import kotlin.math.abs
import kotlin.math.min
import kotlin.system.measureTimeMillis

private val worldMouseDown = Vector2(-100f, -100f)
val tmp2Position = Vector2()

@ExperimentalUnsignedTypes
fun buttonHold(key: Int): Boolean {
    return Gdx.input.isButtonPressed(key)
}

@ExperimentalUnsignedTypes
fun buttonOnce(key: Int): Boolean {
    return Gdx.input.isButtonJustPressed(key)
}


private var dcTimer = 0L

@ExperimentalUnsignedTypes
fun doubleClick(millisInterval: Long = 250L): Boolean {
    if (buttonOnce(0)) {
        if (dcTimer == 0L) {
            dcTimer = System.currentTimeMillis()
        } else {
            if ((System.currentTimeMillis() - dcTimer) <= millisInterval) {
                dcTimer = 0L
                return true
            } else {
                dcTimer = System.currentTimeMillis()
            }
        }
    }
    return false
}


@ExperimentalUnsignedTypes
fun buttonUp(button: Int): Boolean {
    if (buttonOnce(button)) {
        worldMouseDown.set(sensor.worldMouse)
    }
    if (!buttonHold(button) && (worldMouseDown.x == sensor.worldMouse.x && worldMouseDown.y == sensor.worldMouse.y)) {
        worldMouseDown.setZero()
        return true
    }
    return false
}

fun keyHold(key: Int) = Gdx.input.isKeyPressed(key)
fun keyOnce(key: Int) = Gdx.input.isKeyJustPressed(key)

private val amountPosition = Vector2()
fun calculateAmount(position: Vector2, previousPosition: Vector2) =
    amountPosition.set(previousPosition.x - position.x, previousPosition.y - position.y)

fun calculateAmount(positionX: Float, positionY: Float, previousPositionX: Float, previousPositionY: Float) =
    amountPosition.set(previousPositionX - positionX, previousPositionY - positionY)


@ExperimentalUnsignedTypes
inline fun <reified T> isActorExists() = module.stage.root.children?.any { it is T } ?: false

inline fun <reified T> getCompressionClass(compression: Compression): T = compression as T
inline fun <reified T> isCompressionClass(compression: Compression): Boolean = compression is T


@ExperimentalUnsignedTypes
fun outActor() = module.stage.root.hit(Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat(), true) == null

fun isPointInPolygon(polygon: FloatArray, x: Float, y: Float) = Intersector.isPointInPolygon(polygon, 0, polygon.size, x, y)

fun Color.toInt(): Int {
    return (255 * r).toInt() shl 24 or ((255 * g).toInt() shl 16) or ((255 * b).toInt() shl 8) or (255 * a).toInt()
}

val currentBackgroundColor = Color(0.2f, 0.2f, 0.2f, 1f)

// получить бафты от инта
fun getBytes(other: Int) = ByteArray(4) { (other ushr (it * 8) and 255).toByte() }.also { it.reverse() }

// получить ИНТ из 4-х байт
@ExperimentalUnsignedTypes
fun getInt(bytes: ByteArray) =
    (bytes[0].toUByte().toInt() shl 24) or (bytes[1].toUByte().toInt() shl 16) or (bytes[2].toUByte().toInt() shl 8) or (bytes[3].toUByte().toInt())

@ExperimentalUnsignedTypes
fun toIntArray(byteArray: ByteArray) = IntArray(byteArray.size / Int.SIZE_BYTES) {
    getInt(byteArray.copyOfRange(it * Int.SIZE_BYTES, it * Int.SIZE_BYTES + Int.SIZE_BYTES))
}




// обновить соординаты ректангла путем прибавления
fun Rectangle.addPosition(position: Vector2) {
    this.x += position.x
    this.y += position.y
}

fun Rectangle.addPosition(x: Float, y: Float) {
    this.x += x
    this.y += y
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


