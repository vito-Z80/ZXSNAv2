package ru.serdjuk.zxsna.app.component

import ru.serdjuk.zxsna.app.component.world.tools.AppToolsSystem

class Component {

//    val storage: Array<AppComponent> = arrayOf(AppToolsSystem())

    // используемый в данный момент сокмпонент (нужно ?)
    val used: AppComponent? = null

    fun update(delta: Float) {
//        storage.forEach { it.update(delta) }
    }


//    inline fun <reified T> get() = storage.find { it is T } as T

}

val component = Component()