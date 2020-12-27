package ru.serdjuk.zxsna.app.keys

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

@ExperimentalUnsignedTypes
class AppKeys {

    object Type {
        const val ONCE = 0
        const val HOLD = 256
    }

    private val keysStorage = ArrayList<Combination>()

    init {
        install()
    }

    private fun install() {
        keysStorage.add(Combination(KEYS.CLEAR_SELECTION, Input.Keys.SHIFT_LEFT or Type.HOLD, Input.Keys.D))
        keysStorage.add(Combination(KEYS.ADD_NEW_GROUP, Input.Keys.S))
    }

    fun isPressed(name: Pair<Int, String>) = keysStorage.find { it.name.first == name.first }?.isPressed() ?: false
    fun redefine(name: Pair<Int, String>, vararg keys: Int) {
        keysStorage.find { it.name.first == name.first }?.set(keys)
    }

    inner class Combination(val name: Pair<Int, String>, vararg var keys: Int) {

        fun set(keys: IntArray) {
            this.keys = keys
        }

        private var id = 0
        fun isPressed(): Boolean {

            //FIXME не работает нажатие одной клавиши !!!
            while (id < keys.size) {
                val hold = keys[id] and 768 == 256
                val once = keys[id] and 767 != 0
                if (hold && keys[id] and 255 != 0) {
                    if (!Gdx.input.isKeyPressed(keys[id] and 255)) return false
                    id++
                    continue
                }
                if (once && keys[id] != 0) {
                    if (!Gdx.input.isKeyJustPressed(keys[id])) return false
                    id++
                    continue
                }
                id++
            }
            return true
        }
    }
}

@ExperimentalUnsignedTypes
val keys = AppKeys()

object KEYS {
    val CLEAR_SELECTION = Pair(1, "Clear selection")
    val ADD_NEW_GROUP = Pair(1, "Add new group")

}
