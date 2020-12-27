package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.Gdx
import ru.serdjuk.zxsna.app.tools.AppToolsSystem

@ExperimentalUnsignedTypes
class AppSystem {

    val systems = ArrayList<ISystem>()


    fun update(delta: Float) {
        systems.forEach {
            if (it is AppToolsSystem) {
                it.isVisible = true
            }

            if (it.isVisible) {
                it.update(delta)
            }
        }
    }

    fun draw() {
        systems.forEach {
            if (it.isVisible) {
                it.draw()
            }
        }
    }

    /**
     * Инициализация отдельной системы, если не была инициализированна ранее
     * @return ISystem child
     */
    inline fun <reified T> activate(): T {
        systems.forEach {
            if (it is T) {
                // Return initialized system
                return it
            }
        }
        val newInstance = T::class.java.newInstance() as ISystem
        // Add system to storage
        systems.add(newInstance)
        // Return new instance
        Gdx.app.log("$newInstance", "System is activate.")
        return newInstance as T
    }

    inline fun <reified T> isActivate(): Boolean = systems.any { it is T }

    /**
     * Show T system.
     * @param T child of ISystem.
     * @return True if system is activate.
     */
    inline fun <reified T> show(): Boolean = systems.any { s -> let { s.isVisible = true;s is T } }


    /**
     * Hide T system.
     * @param T child of ISystem.
     * @return True if system is activate.
     */
    inline fun <reified T> hide(): Boolean = systems.any { s -> let { s.isVisible = false;s is T } }

    /**
     * Hide all systems.
     */
    fun hideAll() {
        systems.forEach { it.isVisible = false }
    }

    /**
     * Get T system or null.
     * @param T child of ISystem
     * @return ISystem child or NULL
     */
    inline fun <reified T> get() = systems.find { it is T }.let {
        if (it != null)
            it as T else null
    }

}

@ExperimentalUnsignedTypes
val system = AppSystem()