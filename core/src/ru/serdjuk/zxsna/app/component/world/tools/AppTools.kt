package ru.serdjuk.zxsna.app.component.world.tools

import ru.serdjuk.zxsna.app.component.AppComponent

@ExperimentalUnsignedTypes
class AppTools : AppComponent {


    val tools: Array<ITools> = arrayOf(PenTool(), FillTool(), SelectTool())

    override val name = this::class.java

    override fun update(delta: Float) {
        tools.forEach { it.update(delta) }
    }

}