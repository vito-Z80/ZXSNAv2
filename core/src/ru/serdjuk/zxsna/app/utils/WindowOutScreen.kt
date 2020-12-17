package ru.serdjuk.zxsna.app.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Window
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor
import kotlin.math.round

@ExperimentalUnsignedTypes
open class WindowOutScreen(name: String) : Window(name, module.skin) {

    val lastBounds = Rectangle()


    override fun pack() {
        this.name = this::class.java.name
        setKeepWithinStage(false)
        super.pack()
//        toCenter()
        module.stage.addActor(this)
        lastBounds.set(x, y, width, height)
        roundPosition()
    }

    fun toCenter() {
        isVisible = true
        toFront()
        addAction(Actions.moveTo(
                (Gdx.graphics.width / 2 - width / 2).toFloat(),
                (Gdx.graphics.height / 2 - height / 2).toFloat(),
                0.25f,
                Interpolation.swingOut
        ))
        roundPosition()
    }

//    private fun collectContent() {
//        add(Image(module.skin.getDrawable(UI.USER_PALETTE)))
//    }

    // TODO при изменении разрешения экрана изменять последние границы lastBounds в процентах относительно
    // TODO сделать если будет юзаться функция toLastPosition()
    fun reboundLastPosition() {

    }

    override fun act(delta: Float) {
        if (actions.size > 0) {
            isVisible = true
        } else {
            if (!buttonHold(0) && !sensor.screenBounds.overlaps(lastBounds)) {
                isVisible = false
                return
            }
            // окно корежит после остановки action.moveTo если не округлить координаты
           roundPosition()
        }
        lastBounds.set(x, y, width, height)
        super.act(delta)
    }

    fun roundPosition() {
        x = round(x)
        y = round(y)
    }

}