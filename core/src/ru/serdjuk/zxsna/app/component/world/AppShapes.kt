package ru.serdjuk.zxsna.app.component.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.system.sensor
import ru.serdjuk.zxsna.app.utils.keyHold
import ru.serdjuk.zxsna.app.utils.keyOnce

@ExperimentalUnsignedTypes
class AppShapes {

    //    val camera = Agent.module.render.camera
    val shape = ShapeRenderer().also { it.setAutoShapeType(true) }

    //    private val t = res.data.workLayers[res.data.sensor.plSpinner].region
    private val t = res.layers[sensor.paletteOffset].region

    val color01 = Color(1f, 1f, 1f, 0.1f)
    val color02 = Color(1f, 1f, 1f, 0.2f)
    val color03 = Color(1f, 1f, 1f, 0.3f)

    var isGrid = true

    fun update() {
        shape.projectionMatrix = module.worldCamera.combined
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        shape.begin()

        if (keyHold(Input.Keys.CONTROL_LEFT) && keyOnce(Input.Keys.G)) isGrid = !isGrid

        if (isGrid) {
            squareBounds(8, color02)
            squareBounds(16, color03)
            square8x8(color01)
        }

        layerBorder()
        selector()

        shape.end()
        Gdx.gl20.glDisable(GL20.GL_BLEND)
    }

    private val lightGreen = Color(0f, 1f, 0f, 0.4f)
    private fun selector() {
        if (sensor.selectRectangle.width == 0f || sensor.selectRectangle.height == 0f) return
        shape.set(ShapeRenderer.ShapeType.Line)
        val multiple16 = sensor.selectRectangle.width % 16f == 0f && sensor.selectRectangle.height % 16f == 0f
        val multiple8 = sensor.selectRectangle.width % 8f == 0f && sensor.selectRectangle.height % 8f == 0f
        shape.color = if (!multiple16 && !multiple8) Color.WHITE else if (multiple16) Color.GREEN else lightGreen
        shape.rect(
                sensor.selectRectangle.x,
                sensor.selectRectangle.y,
                sensor.selectRectangle.width,
                sensor.selectRectangle.height
        )
    }

    private fun layerBorder() {
        shape.color = Color.LIGHT_GRAY
        shape.rect(t.regionX.toFloat(), t.regionY.toFloat(), t.regionWidth.toFloat(), -t.regionHeight.toFloat())
    }

    private fun squareBounds(size: Int, color: Color) {
        shape.color = color
        shape.set(ShapeRenderer.ShapeType.Line)
//        val width = t.regionWidth.toFloat()
        val height = t.regionHeight.toFloat()
        val y = t.regionY.toFloat() - height
        val x = t.regionX.toFloat()
        repeat(t.regionHeight / size) { h ->
            shape.line(x, y + h * size, x + height, y + h * size)
        }
        repeat(t.regionWidth / size) { w ->
            shape.line(x + w * size, y, x + w * size, y + height)
        }
    }

    private fun square8x8(color: Color) {
        shape.color = color
        val x = (sensor.worldMouse.x.toInt() and (t.regionWidth - 8)).toFloat()
        val y = (sensor.worldMouse.y.toInt() and (t.regionHeight - 8)).toFloat()

        repeat(9) {
            shape.line(x + it, y, x + it, y + 8f)
        }

        repeat(9) {
            shape.line(x, y + it, x + 8f, y + it)
        }

    }

}

val shapes = lazy { AppShapes() }.value