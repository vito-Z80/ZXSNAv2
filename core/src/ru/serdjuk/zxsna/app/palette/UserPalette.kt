package ru.serdjuk.zxsna.app.palette

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align
import ru.serdjuk.zxsna.app.Packable
import ru.serdjuk.zxsna.app.windows.CancelDialog
import ru.serdjuk.zxsna.app.resources.hexColor512
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.system.sensor
import ru.serdjuk.zxsna.app.utils.buttonOnce

/**
 * Класс хранит только данные и логику палитр
 * Данные:
 *  регион,ячейки,позицию
 * Логика:
 *  выбор ячейки (цвета)
 */
@ExperimentalUnsignedTypes
open class UserPalette(val showOverlayPanel: Boolean) : Packable {

    private val texture = res.texture
    private val pixmap = res.pixmap
    private var time = 0f

    /**
     * Изменяемость/заменяемость ячеек возможна либо не возможна.
     * Есла значние == null то ячейке можно назначить цвет, если == "String" то нельзя. (В тексте указать почему нельзя)
     */
    private val cellMutability = Array<String?>(256) {
        if ((it and 15) == 3) "This cell is occupied by a transparent color,\n" +
                "it cannot be replaced with another color."
        else null
    }

    /**
     * Смещение палитры по линиям. Каждая линия = 16 цветов.
     * Этот-же параметр подходит для определния слоя для конкретной 16-ти цветной палитры.
     */
    var offset = 0

    /**
     * Имя палитры, оно-же идет в скин и атлас.
     */
    val paletteName = StringBuilder()

    /**
     * индексы цветов смещенных палитр (0,16,32,48,64.....240)
     * Каждое значение коллекции устанавливает порядковый номер ячейки палитры
     * (изначально с левой тороны палитры)
     */
    val offsetIndexes = IntArray(16) { it * 16 }

    // границы ячеек палитры относительно актера.
    private val actorBounds = Array<Rectangle>(512) {
        val x = AppPaletteWindow.space + (it and 15) * AppPaletteWindow.cellSize + AppPaletteWindow.space * (it and 15)
        val y = AppPaletteWindow.space + (it and 496) + AppPaletteWindow.space * ((it and 496) / 16)
        Rectangle(x.toFloat(), y.toFloat(), AppPaletteWindow.cellSize.toFloat(), AppPaletteWindow.cellSize.toFloat())
    }

    // получить место под палитру в текстуре атласа и координаты региона в атласе
    private val position = module.atlasUtils.getFreePosition(tableWidth(), tableHeight256()).let {
        pixmap.setColor(Color.SLATE)
        pixmap.fillRectangle(it.x.toInt(), it.y.toInt(), tableWidth(), tableHeight256())
        it
    }

    // заполнить палитру ячейками, создать коллекцию ячеек данной палитры PaletteUnit
    val cells = Array<PaletteUnit>(256) {
        val x = position.x.toInt() + (it and 15) * PaletteUnit.size + AppPaletteWindow.space * (it and 15)
        val y = position.y.toInt() + (it and 240) + AppPaletteWindow.space * ((it and 240) / 16)
        val color = hexColor512[455].int
//            val color = hexColor512[MathUtils.random(userPaletteId * 8, userPaletteId * 16)].int
        val region = TextureRegion(texture, x + 1, y + 1, PaletteUnit.size, PaletteUnit.size)
        val cell = PaletteUnit(color, region)
        cell.upgrade(color)
        cell
    }

    // регион палитры
    val region = TextureRegion(texture, position.x.toInt(), position.y.toInt(), tableWidth(), tableHeight256())


    companion object {
        var userIntColor: Int? = null   // цвет выбранный юзером для рисования
        val storage = ArrayList<UserPalette>()
        var belong = 0

        inline fun <reified T> addPalette(name: String, palette: Class<T>) {
            if (!storage.any { it::class.java == palette }) {
                val palObj = palette.newInstance() as UserPalette
                palObj.paletteName.append(name)
                storage.add(palObj)
                // добавить регион и его имя в скин и атлас
                module.skin.add(name, palObj.region, TextureRegion::class.java)
                res.atlas.addRegion(name, palObj.region)
                val r = res.atlas.findRegion(name)
                println(r.name)
            }
        }
    }


    init {
        Packable.addComponent(this)
    }

    fun clearOffsets() {
        // обновить индексы смещения в исходные позиции (каждое смещение с 'левой' ячейки  (0,16,32,48....))
        offsetIndexes.onEachIndexed { id, _ -> offsetIndexes[id] = id * 16 }
        offset = 0  // сбросить смещение на 0
    }

    /**
     * Получить текущий порядковый номер ячейки.
     */
    fun geCellId() = offsetIndexes[offset]

    /**
     * Установить порядковый номер ячейки палитры.
     * (в случае выбора ячейки пользователем)
     */
    fun setCellId(cellId: Int) {
        offsetIndexes[offset] = cellId
    }


    private val tmpPosition = Vector2()
    private val bounds = Rectangle()

    private fun setHighlightCell(window: AppPaletteWindow, cellId: Int) {
        window.userHighlightCellImage.isVisible = true
        window.userHighlightCellImage.toFront()
        window.userHighlightCellImage.setPosition(
                actorBounds[cellId].x + window.userPaletteImage.x - 1,
                actorBounds[255 - cellId].y + window.userPaletteImage.y - 1
        )
        // colored highlight cell
        if (window.userHighlightCellImage.isVisible) {
            val c = if (time.toInt() % 2 == 0) 1f else 0f
            window.userHighlightCellImage.setColor(c, c, c, 1f)
        }
    }


    /**
     *
     */
    private fun isMouseInPaletteBounds(window: AppPaletteWindow): Boolean {
        tmpPosition.set(sensor.screenMouseYDown)
        window.screenToLocalCoordinates(tmpPosition)
        bounds.set(
                window.userPaletteImage.x,
                window.userPaletteImage.y,
                window.userPaletteImage.width,
                window.userPaletteImage.height
        )
        return bounds.contains(tmpPosition)
    }

    fun inPaletteCoordinates(window: AppPaletteWindow): Vector2? {
        return if (isMouseInPaletteBounds(window)) tmpPosition.set(
                tmpPosition.x - window.userPaletteImage.x,
                tmpPosition.y - window.userPaletteImage.y
        ) else null
    }

    private fun mouseMovedCellId(position: Vector2) = actorBounds.indexOfFirst { it.contains(position) }

    open fun action(window: AppPaletteWindow, delta: Float) {
        window.userHighlightCellImage.isVisible = false
        time += delta * 4
        val position = inPaletteCoordinates(window) ?: return
        position.y = window.userPaletteImage.height - position.y
        val cellId = mouseMovedCellId(position)

        if (paletteOffsetRange(cellId)) {
            setHighlightCell(window, cellId)
            if (buttonOnce(0)) {
                setCellId(cellId)
                setUserColor(window)
            }
        }

    }

    fun setUserColorMark(window: AppPaletteWindow) {
        val cellId = offsetIndexes[offset]
        userIntColor = pixmap.getPixel(
                (region.regionX + actorBounds[cellId].x).toInt(),
                (region.regionY + actorBounds[cellId].y).toInt()
        )
        window.highlightUserColorImage.color.set(userIntColor!!)
        window.highlightUserColorImage.setPosition(
                actorBounds[cellId].x + window.userPaletteImage.x,
                actorBounds[255 - cellId].y + window.userPaletteImage.y
        )
        window.highlightUserColorImage.toFront()
        window.highlightUserColorImage.isVisible = true
        window.highlightUserColorImage.setOrigin(Align.center)


        val c = (time / 2) % 1
        window.highlightUserColorImage.setScale(1f + c)
        window.highlightUserColorImage.color.a = 1f - c

    }

    fun setUserColor(window: AppPaletteWindow) {
        val cellId = offsetIndexes[offset]
        val cellRegion = cells[cellId].unitRegion
        userIntColor = pixmap.getPixel(cellRegion.regionX + 1, cellRegion.regionY + 1)
        cells[cellId].upgrade(userIntColor!!)
        window.userColorImage.color.set(userIntColor!!)
        window.userColorInfo.setText("#${hexColor512.find { it.int == userIntColor }?.hex?.dropLast(2)}")
    }

    /**
     * Возвращает истину если ячейка под курсором мыши входит в текущий диапазон палитры.
     * @param cellId ID ячейки под курсором мыши
     */
    open fun paletteOffsetRange(cellId: Int) = (offset * 16 <= cellId && offset * 16 + 16 > cellId)

    /**
     * устанавливаем перетаскиваемую ячейку цвета в пользовательскую палитру
     * если перетаскиваемая ячейка попадает в одну из ячеек пользовательской палитры
     */
    fun setAcceptedColor(window: AppPaletteWindow): Boolean {
        if (!window.userMovableImage.isVisible) return false
        val position = inPaletteCoordinates(window) ?: return false
        position.y = window.userPaletteImage.height - position.y
        val cellId = mouseMovedCellId(position)
        if (paletteOffsetRange(cellId)) {
            val dialogMessage = cellMutability[cellId]
            if (dialogMessage != null) {
                CancelDialog(dialogMessage)
                return false
            }
            val acceptedRegion = window.movableCellRegion
            val acceptedColor = pixmap.getPixel(acceptedRegion.regionX + 1, acceptedRegion.regionY + 1)
            cells[cellId].upgrade(acceptedColor)
            return true
        }
        return false
    }


    fun getShortName() = paletteName.split(" ").joinToString(separator = "") { it.first().toUpperCase().toString() }

    private fun tableWidth() = 16 * PaletteUnit.size + AppPaletteWindow.space * PaletteUnit.size + AppPaletteWindow.space
    private fun tableHeight256() = tableWidth()
    private fun tableHeight512() = tableWidth() * 2 - 1


    /**
     * CONVERTER
     */
     fun convertPaletteTo(): ByteArray {
        // for save palette
//        val paletteBytes = ArrayList<Byte>()
//
//        val line = ByteArray(32)
//        repeat(16) { row ->
//            repeat(16) { cell ->
//                val color = userPaletteUnits[row * 16 + cell].get9bit()
//                val bit9 = color and 256 shr 8
//                val byte = color and 255
//                line[cell * 2] = byte.toByte()
//                line[cell * 2 + 1] = bit9.toByte()
//            }
//            paletteBytes.addAll(0, line.toList())
//        }


        val result = com.badlogic.gdx.utils.ByteArray()
        val ba = com.badlogic.gdx.utils.ByteArray()
        Array<String>(16) {
            val str = StringBuilder()
            ba.clear()
            repeat(16) { index ->
                val c = convertTo9bpp(cells[it * 16 + index].colorId())
                val bit9 = c and 256 shr 8
                val byte = c and 255
                // TODO add hex variation
                str.append("$byte, $bit9, ")
                ba.add(byte.toByte(), bit9.toByte())
            }
            result.addAll(ba)
            str.toString()
        }
        return result.toArray()
    }

    // индекс цвета делится на 2 и если есть остаток то включить бит 9
    private fun convertTo9bpp(colorId: Int) = (colorId / 2) or (colorId % 2 shl 8)

     fun convertPaletteFrom(byteArray: ByteArray) {
        // for load palette
        // если бит 9 включен то умножаем первый байт на 2 + 1
        cells.forEachIndexed { id, cell ->
            val colorId = if (byteArray[id * 2 + 1].toUByte().toInt() == 0) {
                byteArray[id * 2].toUByte().toInt() * 2
            } else {
                byteArray[id * 2].toUByte().toInt() * 2 + 1
            }
            cell.upgrade(hexColor512[colorId].int)
        }
    }


    /**
     * FOR COMPRESS
     *
     *
     * склееваем в один массив следущее:
     * offset
     * offsetIndexed
     * color ID`s from general palette
     *
     * палитру (PaletteUnit.intColor), текущее смещение (offset), массив смещений (offsetIndexed)
     * TODO добавить позицию окна палитры относительно размера окна приложения в %
     * TODO видимость основной палитры и общая видимость окна палитры
     */
    override fun collectData(): ByteArray {
        val bytes = ArrayList<Byte>()
        // offset byte (1 byte)
        bytes.add(offset.toByte())
        // offset indexes (16 bytes)
        bytes.addAll(offsetIndexes.map { (it and 15).toByte() })

        // palette colors id (512 bytes)
        bytes.addAll(convertPaletteTo().toTypedArray())
        return bytes.toByteArray()
    }

    /**
     * распихиваем из массива в том же порядке что и пихали
     */
    override fun parseData(bytes: ByteArray) {
        // offset byte (1 byte)
        offset = bytes[0].toInt()
        // offset indexes (16 bytes)
        for (id in 0..15) {
            // +1 offset from start data
            offsetIndexes[id] = bytes[id + 1].toInt() + (id * 16)
        }
        // palette colors id (512 bytes)
        convertPaletteFrom(bytes.copyOfRange(17, 17 + 512))
        res.textureUpdate() // FIXME remove from here if parse with coroutine
    }


}




