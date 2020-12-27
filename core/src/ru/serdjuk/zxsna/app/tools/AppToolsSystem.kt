package ru.serdjuk.zxsna.app.tools

import ru.serdjuk.zxsna.app.Packable
import ru.serdjuk.zxsna.app.system.ISystem

@ExperimentalUnsignedTypes
class AppToolsSystem : ISystem {
    companion object {
        var usedTool = 0
    }

    val tools: Array<ITools> = arrayOf(PenTool(), FillTool(), SelectTool())

    override var isVisible = false

    override fun update(delta: Float) {
        tools.forEach { it.update(delta) }
    }

    override fun draw() {

    }


}