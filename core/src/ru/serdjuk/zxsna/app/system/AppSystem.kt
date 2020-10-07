package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.graphics.g2d.SpriteBatch

@ExperimentalUnsignedTypes
class AppSystem {

    val displays = arrayOf(ImageSystem(),SpriteSystem())


    fun update(delta: Float) {
        History.update()
        displays.forEach {
            if (it.isVisible) {
                it.update(delta)
            }
        }
    }

    fun draw() {
        displays.forEach {
            if (it.isVisible) {
                it.draw()
            }
        }
    }

    inline fun <reified T> display(isVisible: Boolean): T? {
        displays.forEach {
            if (it is T) {
                it.isVisible = isVisible
                return it
            }
        }
        return null
    }

    inline fun <reified T> get(): T? {
        displays.forEach {
            if (it is T) {
                return it
            }
        }
        return null
    }
}

@ExperimentalUnsignedTypes
val system = AppSystem()