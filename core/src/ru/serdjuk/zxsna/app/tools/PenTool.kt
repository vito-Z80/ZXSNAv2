package ru.serdjuk.zxsna.app.tools

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.IntArray
import ru.serdjuk.zxsna.app.palette.AppPaletteWindow
import ru.serdjuk.zxsna.app.palette.UserPalette
import ru.serdjuk.zxsna.app.layers.AppLayersSystem
import ru.serdjuk.zxsna.app.layers.TextureLayer
import ru.serdjuk.zxsna.app.system.*
import ru.serdjuk.zxsna.app.utils.buttonHold
import ru.serdjuk.zxsna.app.utils.buttonOnce
import ru.serdjuk.zxsna.app.utils.isPointInPolygon
import ru.serdjuk.zxsna.app.utils.keyHold

@ExperimentalUnsignedTypes
class PenTool : ITools() {


    private var startDraw = false
    private val points = IntArray()
    private val colors = IntArray()
    private var layer: TextureLayer? = null
    private val clearColor = Color.CLEAR.toIntBits()

    override var toolName = ToolName.PEN
    override fun update(delta: Float) {
        if (AppToolsSystem.usedTool != toolName) return
        if (buttonOnce(0)) {
            layer = system.get<AppLayersSystem>()?.getLayer()
            startDraw = true
            points.clear()
            colors.clear()
        }

        if (buttonHold(0)) {
            if (keyHold(Input.Keys.CONTROL_LEFT)) {
                setPixel(clearColor)
            } else {
                setPixel(UserPalette.userIntColor)
            }
        } else {
            if (startDraw && points.size > 0) {
                startDraw = false
                val data = URPointsData(UserPalette.storage[AppPaletteWindow.belong].offset, IntArray(colors), IntArray(points))
                History.push(data)
            }
        }
    }

    override fun undo(data: UR?) {
        if (data is URPointsData && layer != null) {
            UserPalette.storage[AppPaletteWindow.belong].offset = data.layerId
            val bitmap = layer!!.pixmap
            bitmap.blending = Pixmap.Blending.None
            val texture = layer!!.texture
            while (data.points.size > 0) {
                val y = data.points.pop()
                val x = data.points.pop()
                val color = data.intColors.pop()
                bitmap.drawPixel(x, y, color)
            }
            texture.draw(bitmap, 0, 0)
        }
    }

    private fun setPixel(userIntColor: Int?) {
        if (layer == null) return
        val bitmap = layer!!.pixmap
        bitmap.blending = Pixmap.Blending.None
        val texture = layer!!.texture
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
            if (userIntColor != null && bitmap.getPixel(mouseX, mouseY) != userIntColor) {
                colors.add(bitmap.getPixel(mouseX, mouseY))
                points.add(mouseX, mouseY)
//                bitmap.setColor(hexColor512[PaletteData.colorId].int)
                bitmap.setColor(userIntColor)
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