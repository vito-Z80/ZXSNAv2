package ru.serdjuk.zxsna.app.component.world.tools

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.IntArray
import ru.serdjuk.zxsna.app.utils.buttonHold
import ru.serdjuk.zxsna.app.utils.buttonOnce
import ru.serdjuk.zxsna.app.utils.isPointInPolygon
import ru.serdjuk.zxsna.app.utils.keyHold
import ru.serdjuk.zxsna.app.component.ui.palette.PaletteData
import ru.serdjuk.zxsna.app.resources.hexColor512
import ru.serdjuk.zxsna.app.system.*

@ExperimentalUnsignedTypes
class PenTool : ITools() {


    private var startDraw = false
    private val points = IntArray()
    private val colors = IntArray()

    override var toolName = ToolName.PEN
    override fun update(delta: Float) {
        if (ToolName.used != toolName) return

        if (buttonOnce(0)) {
            startDraw = true
            points.clear()
            colors.clear()
        }

        if (buttonHold(0)) {
            if (keyHold(Input.Keys.CONTROL_LEFT)) {
                // TODO delete (for test pipet)
//                if (res.uploadImagePixmap != null) {
//                    PaletteData.setColor(
//                            res.uploadImagePixmap!!.getPixel(sensor.worldMouse.x.toInt(), sensor.worldMouse.y.toInt())
//                    )
//                }
            } else {
                setPixel()
            }
        } else {
            if (startDraw && points.size > 0) {
                startDraw = false
                val data = URPointsData(sensor.paletteOffset, IntArray(colors), IntArray(points))
                History.push(data)
                println("COLORS COUNT: ${colors.size}")
                println("PIXELS COUNT: ${points.size}")
            }
        }
    }

    override fun undo(data: UR?) {
        if (data is URPointsData) {
            sensor.paletteOffset = data.layerId
            val bitmap = res.layers[sensor.paletteOffset].pixmap
            bitmap.blending = Pixmap.Blending.None
            val texture = res.layers[sensor.paletteOffset].texture
            while (data.points.size > 0) {
                val y = data.points.pop()
                val x = data.points.pop()
                val color = data.intColors.pop()
                bitmap.drawPixel(x, y, color)
            }
            texture.draw(bitmap, 0, 0)
        }
    }

    private fun setPixel() {
        val bitmap = res.layers[sensor.paletteOffset].pixmap
        bitmap.blending = Pixmap.Blending.None
        val texture = res.layers[sensor.paletteOffset].texture
        val x = 0f
        val y = 0f
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()
        val rect = floatArrayOf(
                x, y,
                x, y + h,
                x + w, y + h,
                x + w, y
        )
        if (isPointInPolygon(rect, sensor.worldMouse.x, sensor.worldMouse.y)) {
            val mouseX = sensor.worldMouse.x.toInt()
            val mouseY = sensor.worldMouse.y.toInt()
//            val bitmapColor = Color(bitmap.getPixel(mouseX, mouseY))
            val bitmapColor = Color()
            Color.rgba8888ToColor(bitmapColor, bitmap.getPixel(mouseX, mouseY))
            if (bitmap.getPixel(mouseX, mouseY) != PaletteData.intColor) {
                println(PaletteData.intColor)
                colors.add(bitmap.getPixel(mouseX, mouseY))
                points.add(mouseX, mouseY)
                bitmap.setColor(hexColor512[PaletteData.colorId].int)
                bitmap.drawPixel(mouseX, mouseY)
                texture.draw(bitmap, 0, 0)
            }
        } else {
            println("out of rect")
        }
    }

    override fun draw(batch: SpriteBatch) {


    }

    override fun draw(shape: ShapeRenderer) {
    }


}