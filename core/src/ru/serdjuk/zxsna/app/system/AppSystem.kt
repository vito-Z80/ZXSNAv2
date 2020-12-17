package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.Gdx

@ExperimentalUnsignedTypes
class AppSystem {

    val systems = ArrayList<ISystem>()


    fun update(delta: Float) {
        systems.forEach {
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
     * Инициализация отдельной системы и установка ее видимости, если не была инициализированна ранее
     * Если система была инициализирована ранее - опредлеяем ее визуальную видимость
     * @param isVisible визуальное представление системы
     * @return ISystem child
     */
    inline fun <reified T> set(isVisible: Boolean = false): T {
        systems.forEach {
            if (it is T) {
                it.isVisible = isVisible
                // Return initialized system
                return it
            }
        }
        val newInstance = T::class.java.newInstance() as ISystem
        newInstance.isVisible = isVisible
        // Add system to storage
        systems.add(newInstance)
        // Return new instance
        return newInstance as T
    }


//    inline fun <reified T> get(): T? {
//        systems.forEach {
//            if (it is T) {
//                return it
//            }
//        }
//        Gdx.app.log("${this::toString}", "System absent.")
//        return null
//    }
}

@ExperimentalUnsignedTypes
val system = AppSystem()