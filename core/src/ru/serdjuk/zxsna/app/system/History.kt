package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.Input
import ru.serdjuk.zxsna.app.tools.AppToolsSystem
import ru.serdjuk.zxsna.app.utils.keyHold
import ru.serdjuk.zxsna.app.utils.keyOnce
import java.util.*

@ExperimentalUnsignedTypes
object History {

    val hp = Stack<UR>()
    var position = 0

    //    val tools = component.get<AppToolsSystem>().tools
    val tools = system.set<AppToolsSystem>(true).tools


    fun update() {
        if (keyHold(Input.Keys.CONTROL_LEFT) && keyOnce(Input.Keys.Z) && hp.size > 0) {
            val data = undo()
            tools.forEach { it.undo(data) }
        }
    }


//    fun update(tools: Array<ITools>) {
//        if (keyHold(Input.Keys.CONTROL_LEFT) && keyOnce(Input.Keys.Z) && hp.size > 0) {
//            val data = undo()
//            tools.forEach { it.undo(data) }
//        }
//    }

    // add history
    fun push(ur: UR) {
        hp.push(ur)
        position++
    }

    private fun undo(): UR? {
        if (position < 1) return null
        position--
        return hp.pop()
    }

//    fun redo(): URD? {
//        if (position >= hp.size + 1) return null
//        position++
//        window.pop()
//        return hp[position]
//    }


}