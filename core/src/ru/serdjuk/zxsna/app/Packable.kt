package ru.serdjuk.zxsna.app

import com.badlogic.gdx.Gdx
import ru.serdjuk.zxsna.app.palette.*
import ru.serdjuk.zxsna.app.windows.AppToolsWindow
import ru.serdjuk.zxsna.app.layers.AppLayersSystem
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.utils.WindowOutScreen
import ru.serdjuk.zxsna.app.utils.getBytes
import ru.serdjuk.zxsna.app.utils.getInt
import java.nio.charset.StandardCharsets.UTF_8

/**
 * Добавочный интерфейс ко всем компонентам системы которые требуют паковки/распаковки данных.
 */
interface Packable {


    @ExperimentalUnsignedTypes
    companion object {
        val types = HashMap<String, Class<*>>()
        inline fun <reified T> addType(java: Class<*>) {
            val packableClass = T::class.java
            if (packableClass.interfaces.find { it == Packable::class.java } != null) {
                types[T::class.java.name] = java
            } else {
                throw ClassCastException("Class \"$packableClass\" not have interface \"Packable\"")
            }
        }

        /**
         * Хранилище всех компонентов требующих паковки/распаковки
         */
        val storage = ArrayList<Packable>()

        /**
         * Получить компонент или null если компонент отсутствует.
         */
        inline fun <reified T> getComponent(): T? {
            storage.forEach {
                if (it is T) {
                    return it
                }
            }
            return null
        }

        /**
         * Получить все компоненты требуемого родителя.
         */
        inline fun <reified T> getRightComponents() = storage.filter { it is T }

        inline fun <reified T> getSize() = module.stage.root.children.filter { it is T }.size

        /**
         * Добавить компонент в хранилище если он не существует в нём.
         */
        inline fun <reified T> addComponentWithType(component: Packable) = if (storage.contains(component)) {
            true
        } else {
            addType<T>(component::class.java)
            storage.add(component)
        }

        fun addComponent(component: Packable) = if (storage.contains(component)) true else storage.add(component)


        inline fun <reified T> addComponent() {
            storage.find { it::class.java == T::class.java } ?: storage.add(T::class.java.newInstance() as Packable)
        }


        /**
         * Сбор и компрессия необходимых данных для сохранения проетка.
         * Порядок хранения:
         * 1) длинна имени класса       длинна в байтах = 1 байт
         * 2) длинна данных класса      длинна в байтах = 4 байта
         * 3) имя класса                длинна в байтах = (значение пункта 1)
         * 4) требуемые данные класса   длинна в байтах = (значение пункта 2)
         */
        fun packProject(): ByteArray {
            Gdx.app.log(this::class.java.name, "Pack process")
            val collectData = ArrayList<Byte>()
            storage.forEach {
                val data = it.collectData()
                val className = it::class.java.toString().toByteArray(UTF_8)
                collectData.add(className.size.toByte())    // FIXME обрабатываемая длинна имени класса не более 256 символов UTF_8
                collectData.addAll(getBytes(data.size).toTypedArray())
                collectData.addAll(className.toTypedArray())
                collectData.addAll(data.toTypedArray())
                Gdx.app.log(it::class.java.name, "packed.")
            }
            return collectData.toByteArray()
        }

        /**
         * Распаковка данных проекта и отправка данных по назначению.
         * Порядок хранения:
         * 1) длинна имени класса       длинна в байтах = 1 байт
         * 2) длинна данных класса      длинна в байтах = 4 байта
         * 3) имя класса                длинна в байтах = (значение пункта 1)
         * 4) требуемые данные класса   длинна в байтах = (значение пункта 2)
         */
        fun unpackProject(bytes: ByteArray) {
            addAllTypes()   // TODO ДОБАВИТЬ ВСЕХ ОТ Packable
            Gdx.app.log(this::class.java.name, "Unpack process")
            var counter = 0
            while (counter < bytes.size) {
                val classNameLength = bytes[counter++].toInt()
                val dataSize = getInt(bytes.copyOfRange(counter, counter + Int.SIZE_BYTES))
                counter += Int.SIZE_BYTES
                val className = bytes.copyOfRange(counter, counter + classNameLength).toString(UTF_8).split("class")[1].trimIndent()
                counter += classNameLength
                val data = bytes.copyOfRange(counter, counter + dataSize)
                counter += dataSize

                // Заносим данные в компонент если он был ранее создан иначе, сначала создаем компонент.
                val result = storage.find { it::class.java == types[className] }.also {
                    it?.parseData(data)
                    if (it is WindowOutScreen) {
                        it.toCenter()
                    }
                }
                        ?: (types[className]?.newInstance() as Packable).also {
                            addComponent(it)
                            it.parseData(data)
                            if (it is WindowOutScreen) {
                                it.toCenter()
                            }
                        }
                Gdx.app.log(result::class.java.name, "unpacked")
            }

        }

        private fun addAllTypes() {
            // TODO добавить AppPaletteWindow, но только что бы оно определяло видимость при распаковке, +установка координат если было видимо
//            types[AppPaletteWindow::class.java.name] = AppPaletteWindow::class.java
            types[AppToolsWindow::class.java.name] = AppToolsWindow::class.java
            types[AppLayersSystem::class.java.name] = AppLayersSystem::class.java
            types[UserPalette4bppSprites::class.java.name] = UserPalette4bppSprites::class.java
            types[UserPalette9bppSprites::class.java.name] = UserPalette9bppSprites::class.java
            types[UserPalette4bppTiles::class.java.name] = UserPalette4bppTiles::class.java
            types[UserPalette9bppTiles::class.java.name] = UserPalette9bppTiles::class.java
        }

    }


    /**
     * Собрать требуемые данные
     */
    fun collectData(): ByteArray

    /**
     * Разобрать данные на требуемые
     */
    fun parseData(bytes: ByteArray)

    /**
     * Отчистить данные (привести их в состояние instance)
     */
//    fun clear()


}
