package ru.serdjuk.zxsna.app.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor


class CameraControl() {

    val camera = module.worldCamera
    private val MAX_SCALE = 128f
    private val MIN_SCALE = 0.2f

    var amount = 0f
    var scale = 1f
    private val touchPoint = Vector2()
    private val upPoint = Vector2()
    private var isDrag = false

    fun install() {
        val listener = object : ClickListener() {
            override fun scrolled(event: InputEvent?, x: Float, y: Float, amount: Int): Boolean {
                sensor.mouseWheel = amount
                this@CameraControl.amount = MathUtils.clamp(this@CameraControl.amount + amount, -10f, 10f)
                return super.scrolled(event, x, y, amount)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                touchPoint.set(sensor.worldMouse)
                return super.touchDown(event, x, y, pointer, button)
            }


        }
        module.stage.addListener(listener)
    }


    fun update(delta: Float) {
        isDrag = Gdx.input.isButtonPressed(1)
        if (Gdx.input.isTouched) upPoint.set(sensor.worldMouse)

        if (amount > 0f) {
            amount = if (scale == MAX_SCALE) {
                0f
            } else {
                MathUtils.clamp(amount - 0.025f, 0f, 10f)
            }
        }
        if (amount < 0f) {
            amount = if (scale == MIN_SCALE) {
                0f
            } else {
                MathUtils.clamp(amount + 0.025f, -10f, 0f)
            }
        }

        upgrade(delta)
    }

    fun upgrade(delta: Float) {
        scale = MathUtils.clamp(scale + (delta * (amount * scale)), MIN_SCALE, MAX_SCALE)

        camera.zoom = scale
        camera.update()
        val x = sensor.worldMouse.x
        val y = sensor.worldMouse.y
        sensor.mouseRefresh()
        if (isDrag)
            camera.translate(touchPoint.x - sensor.worldMouse.x, touchPoint.y - sensor.worldMouse.y)
        camera.translate(x - sensor.worldMouse.x, y - sensor.worldMouse.y)
        camera.update()
    }
}

val cameraControl = lazy { CameraControl() }.value.also { it.install() }