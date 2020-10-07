package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.graphics.g2d.SpriteBatch

interface ISystem {


    // видимость систему на экране
    var isVisible: Boolean

    fun update(delta: Float)
    fun draw()

    // включить систему
    fun setSystem()

    // выключить систему
    fun resetSystem()
}