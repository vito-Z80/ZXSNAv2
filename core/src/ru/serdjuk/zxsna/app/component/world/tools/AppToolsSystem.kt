package ru.serdjuk.zxsna.app.component.world.tools

import ru.serdjuk.zxsna.app.system.ISystem

@ExperimentalUnsignedTypes
class AppToolsSystem : ISystem {
    companion object {
        var usedTool = 0
    }

    override var isVisible = true

    val tools: Array<ITools> = arrayOf(PenTool(), FillTool(), SelectTool())

    override fun update(delta: Float) {
        tools.forEach { it.update(delta) }
    }

    override fun draw() {

    }


}