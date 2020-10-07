package ru.serdjuk.zxsna.app.component

import ru.serdjuk.zxsna.app.component.world.tools.AppTools

interface AppComponent {

    val name: Class<out AppComponent>

    fun update(delta: Float)
}