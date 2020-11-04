package ru.serdjuk.zxsna.app.system

interface ISystem {


    // видимость систему на экране
    var isVisible: Boolean

    fun update(delta: Float)
    fun draw()

}