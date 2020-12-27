package ru.serdjuk.zxsna.app.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.serdjuk.zxsna.app.palette.PaletteData
import ru.serdjuk.zxsna.app.windows.LayerMenuInfoWindow
import ru.serdjuk.zxsna.app.layers.AppLayersSystem
import ru.serdjuk.zxsna.app.system.system
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalUnsignedTypes
open class TextureLayerUtils {

    // если не null значит было заполнено спрайтами или тайлами по пользовательскому инструменту "Веделение"
    // если не null - можно сохранить на диск или в листы спрайтов/тайлов
    var collectResource: ByteArray? = null



    @ExperimentalUnsignedTypes
            /**
             * Создает коллекцию спрайтов либо тайлов в формате 4bpp.
             * Функция определяет обрабатывать спрайты или тайлы исходя из размеров пользовательского выделения.
             *
             * @param resourceSize размер прямоугольника по которому определяется дальнейшая обработка
             * как спрайт 16х16 или тайл 8х8
             * @param infoMenu меню процесса обработки пользовательского выделения
             */
    fun collect4bpp(resourceSize: Int, infoMenu: LayerMenuInfoWindow) {
        val userSelection = RectInt(
//                sensor.selectRectangle.x.toInt(),
//                sensor.selectRectangle.y.toInt(),
//                sensor.selectRectangle.width.toInt(),
//                sensor.selectRectangle.height.toInt()
        )
        val b = ArrayList<Byte>(1)
        val rectanglesNumber = (userSelection.width / resourceSize) * (userSelection.height / resourceSize)
        infoMenu.progressBar.maxRectangles = rectanglesNumber
        infoMenu.rectangleNumbersInfo.setText(rectanglesNumber)
        infoMenu.progressBar.isStart = true
        infoMenu.progressBar.sprite.setSize(infoMenu.prefWidth, 16f)
        infoMenu.progressBar.pack()
        var imageRectangle = 0
        var emptyRectangle = 0
        var counter = 0
//        println("User selected $rectCounter rectangles for processing.")
        CoroutineScope(Dispatchers.IO).launch {

            // FIXME move to suspend fun
            val rectangles = userSelection.slicingRectangles(resourceSize)
            rectangles.forEach { rect ->
//                println("Remaining ${rectCounter--} rectangles process.")
                val byteArray = get4bppData(rect)
                if (byteArray != null) {
                    b.addAll(byteArray.toTypedArray())
                    imageRectangle++
                    infoMenu.imageRectanglesInfo.setText(imageRectangle)
                } else {
                    emptyRectangle++
                    infoMenu.emptyRectanglesInfo.setText(emptyRectangle)
                }
                counter++
                infoMenu.progressBar.rectanglesCounter = counter
            }
//            println("Working rect = $imageRectangle")
//            println("Empty rect`s = ${rc - imageRectangle}")
            collectResource = b.toByteArray()
            infoMenu.add(infoMenu.createBottomButtons())
        }
    }


    /*
    возвращает данные спрайта/тайла в формате 4bpp, или null если обрабатываемая область пуста
 */
    @ExperimentalUnsignedTypes
    internal suspend fun get4bppData(selection: RectInt): ByteArray? {
        val size = selection.width * selection.height
        var isEmpty = true
        // палитра слоя
//        val paletteCells = Array<Int>(16) {
//            hexColor512[PaletteData.paletteTables.userTable[it + sensor.paletteOffset * 16].colorId].int
//        }
        // data залить прозрачным цветом ранее выбранным пользователем
        val data = IntArray(size / 2) { 0 }
//        val pixmap = res.layers[AppPaletteWindow.offset].pixmap
        val pixmap = system.get<AppLayersSystem>()?.getLayer()!!.pixmap
        repeat(selection.height) heightY@{ y ->
            repeat(selection.width) widthX@{ x ->
                val colorInt = pixmap.getPixel(selection.x + x, selection.y + y)
                val id = ((selection.height - 1 - y) * selection.width + x) / 2
                if (colorInt != PaletteData.appTransparentColor.toIntBits()) {
//                    val color4bpp = paletteCells.indexOfFirst { it == colorInt } and 255
//                    if (x % 2 == 0) {
//                        data[id] = color4bpp shl 4
//                    } else {
//                        data[id] = data[id] or color4bpp
//                    }
                    isEmpty = false
//                    delay(300)
                }
            }
        }
        val result = ByteArray(data.size) { data[it].toByte() }

        return suspendCoroutine {
            if (isEmpty) {
                it.resume(null)
            } else {
                it.resume(result)
            }
        }
    }


    /**
     * Создание спрайтов/тайлов для дальнейшей переброски в листы спрайтов/тайлов редактора уровней
     * @param resourceSize размер ресурса: 16 = спрайт, 8 = тайл
     */
    fun createForSheets(resourceSize: Int) {
//        sensor.transformSelectionRectangle()


    }

}