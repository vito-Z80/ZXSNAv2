package ru.serdjuk.zxsna.app.tools

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.IntArray
import ru.serdjuk.zxsna.app.utils.buttonOnce
import ru.serdjuk.zxsna.app.utils.toInt
import ru.serdjuk.zxsna.app.palette.AppPaletteWindow
import ru.serdjuk.zxsna.app.palette.UserPalette
import ru.serdjuk.zxsna.app.layers.AppLayersSystem
import ru.serdjuk.zxsna.app.system.*

@ExperimentalUnsignedTypes
class FillTool : ITools() {

    // допроверить работу undo (от и до)


    private var stackCounter = 0

    private val stack = IntArray(1000)
    override var toolName = ToolName.FILL

    override fun update(delta: Float) {

        if (AppToolsSystem.usedTool == toolName && buttonOnce(0) && UserPalette.userIntColor != null) {
            // TODO add to coroutine
            val mX = sensor.worldMouse.x.toInt()
            val mY = sensor.worldMouse.y.toInt()
            val replaceableColor = fill(mX, mY, Color(UserPalette.userIntColor!!))
            if (replaceableColor != null) {
                val data = URFillData(UserPalette.storage[AppPaletteWindow.belong].offset, replaceableColor, mX, mY)
                History.push(data)
            }
        }
    }


    override fun undo(data: UR?) {
        if (data is URFillData) {
//            AppColor.currentUserColor.set(data.color)
            UserPalette.storage[AppPaletteWindow.belong].offset = data.layerId
            fill(data.x, data.y, Color(data.intColor))
        }
    }

    override fun draw(batch: SpriteBatch) {

    }

    override fun draw(shape: ShapeRenderer) {

    }

    private fun fill(startX: Int, startY: Int, userColor: Color): Int? {
        val layer = system.set<AppLayersSystem>(true).getLayer()
        if (layer != null) {

            val bitmap = layer.pixmap
            val replaceableColor = bitmap.getPixel(startX, startY)
            if (replaceableColor == Color.rgba8888(userColor)) return null


            val texture = layer.texture
            val width = bitmap.width
            val height = bitmap.height
            stack.add(startX, startY)
            stackCounter = 0
            var x: Int
            var y: Int
            while (stack.size > 0) {
                y = stack.pop()
                x = stack.pop()
                bitmap.drawPixel(x, y, userColor.toInt())

                if (bitmap.getPixel(x, y) == replaceableColor) continue
                fillProcess(bitmap, replaceableColor, x, y, width, height, userColor.toInt())
            }
            texture.draw(bitmap, 0, 0)
            return replaceableColor
        }
        return null
    }

    private fun fillProcess(bitmap: Pixmap, replaceableColor: Int, x: Int, y: Int, width: Int, height: Int, userColor: Int) {

        if (intoBitmap(x - 1, y, width, height) && bitmap.getPixel(x - 1, y) == replaceableColor) {
            stack.add(x - 1, y)
            bitmap.drawPixel(x - 1, y, userColor)
            stackCounter++
        }
        if (intoBitmap(x, y - 1, width, height) && bitmap.getPixel(x, y - 1) == replaceableColor) {
            stack.add(x, y - 1)
            bitmap.drawPixel(x, y - 1, userColor)
            stackCounter++
        }
        if (intoBitmap(x + 1, y, width, height) && bitmap.getPixel(x + 1, y) == replaceableColor) {
            stack.add(x + 1, y)
            bitmap.drawPixel(x + 1, y, userColor)
            stackCounter++
        }

        if (intoBitmap(x, y + 1, width, height) && bitmap.getPixel(x, y + 1) == replaceableColor) {
            stack.add(x, y + 1)
            bitmap.drawPixel(x, y + 1, userColor)
            stackCounter++
        }
    }

    private fun intoBitmap(x: Int, y: Int, width: Int, height: Int) = x >= 0 && y >= 0 && x < width && y < height


}