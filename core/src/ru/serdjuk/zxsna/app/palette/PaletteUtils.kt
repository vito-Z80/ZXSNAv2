package ru.serdjuk.zxsna.app.palette

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import ru.serdjuk.zxsna.app.resources.hexColor256
import ru.serdjuk.zxsna.app.resources.hexColor512
import ru.serdjuk.zxsna.app.system.res
import kotlin.math.abs
import kotlin.math.min

@ExperimentalUnsignedTypes
object PaletteUtils {


//    fun convertPaletteTo(): ByteArray {
//        val result = com.badlogic.gdx.utils.ByteArray()
//        val ba = com.badlogic.gdx.utils.ByteArray()
//        Array<String>(16) {
//            val str = StringBuilder()
//            ba.clear()
//            repeat(16) { index ->
//                val c = convertTo9bpp(PaletteData.paletteTables.userTable[it * 16 + index].colorId)
//                val bit9 = c and 256 shr 8
//                val byte = c and 255
//                // TODO add hex variation
//                str.append("$byte, $bit9, ")
//                ba.add(byte.toByte(), bit9.toByte())
//            }
//            result.addAll(ba)
//            str.toString()
//        }
//        return result.toArray()
//    }
//
//    fun convertPaletteFrom(byteArray: ByteArray) {
////        PaletteData.paletteTables.userRegion.flip(false,true)
//        // если бит 9bpp включен то умножаем первый байт на 2 + 1
//        PaletteData.paletteTables.userTable.forEachIndexed { id, cell ->
//            val colorId = if (byteArray[id * 2 + 1].toUByte().toInt() == 0) {
//                byteArray[id * 2].toUByte().toInt() * 2
//            } else {
//                byteArray[id * 2].toUByte().toInt() * 2 + 1
//            }
//            cell.colorId = colorId
//            cell.hexColor = hexColor512[colorId].hex
//            drawCellFill(PaletteData.paletteTables.userTable[id], hexColor512[colorId].int)
//        }
////        PaletteData.paletteTables.userRegion.flip(false,true)
//
//        res.textureUpdate()
//    }

    fun convertImage(encodedData: ByteArray, colorsNumber: Int) {
        res.disposeUploadImage()
        val pixmap = Pixmap(encodedData, 0, encodedData.size)
        // FIXME при Blending.NONE получаю пустые пиксели (дохуя, как на реале)
        // походу с альфой залупа
//        pixmap.blending = Pixmap.Blending.None
        res.uploadImagePixmap = pixmap
        lazy { convertToNextColors(res.uploadImagePixmap!!, colorsNumber) }.value
        lazy { res.uploadImageTexture = Texture(res.uploadImagePixmap) }.value
    }

    private fun averageByte(int: Int): Int {
        var m = 255
        var r = 255
        for (i in allBytes.indices) {
            if (min(abs(allBytes[i] - int), m) < m) {
                m = min(abs(allBytes[i] - int), m)
                r = i
            }
        }
        return allBytes[r]
    }

    private fun averageBlueByte(int: Int): Int {
        var m = 255
        var r = 255
        for (i in blueBytes.indices) {
            if (min(abs(blueBytes[i] - int), m) < m) {
                m = min(abs(blueBytes[i] - int), m)
                r = i
            }
        }
        return blueBytes[r]
    }


    // для 256 цветов синий состоит только из 4-х значений
    private val blueBytes = intArrayOf(0x00, 0x6d, 0xb6, 0xff)

    // для 512 цветов rgb имеют по 8 значений
    private val allBytes = intArrayOf(0x00, 0x24, 0x49, 0x6d, 0x92, 0xb6, 0xdb, 0xff)

     var readyForSaveImage: ByteArray? = null
    private fun convertToNextColors(pixmap: Pixmap, colorsNumber: Int) {
        readyForSaveImage = ByteArray(pixmap.width * pixmap.height)
        var byteCounter = 0
        repeat(pixmap.height) { y ->
            repeat(pixmap.width) { x ->
                val color = pixmap.getPixel(x, y)
                // TODO добавить Флойда – Стейнберга
                // перемножаем цвета с альфаканалом
                val aC = ((color and 255)) / 255f
                val rC = (color ushr 24 and 255) / 255f * aC
                val gC = (color ushr 16 and 255) / 255f * aC
                val bC = (color ushr 8 and 255) / 255f * aC
                // ищем усредненный цвет для каждого канала r,g,b
                val r = averageByte((rC * 255f).toInt()) and 255 shl 24
                val g = averageByte((gC * 255).toInt()) and 255 shl 16
                val b = if (colorsNumber == 512) averageByte((bC * 255).toInt()) and 255 shl 8 else
                    averageBlueByte((bC * 255).toInt()) and 255 shl 8
                val hex = StringBuilder((r or g or b or 255).toUInt().toString(16))
                while (hex.length < 8) {
                    hex.insert(0, "0")
                }
                readyForSaveImage!![byteCounter++] = hexColor256.indexOfFirst { it.hex == hex.toString() }.toByte()
//                println("HEX =: $hex")
//                val h = hexColor256.find { it.hex == hex.toString() }
//                if (h == null) {
//                    println("${r or g or b or 255}")
//                    println((r or g or b or 255).toString(16))
//
//                    throw Exception("ИДИ НА ХОЙ ЦВЕТ !!!!")
//                }


                // если цвет получился полностью прозрачным то стриаем пиксель
                if (r or g or b == 0) {
                    pixmap.drawPixel(x, y, Color.CLEAR.toIntBits())
                } else {
                    pixmap.drawPixel(x, y, hex.toString().toLong(16).toInt())
                }
            }
        }
    }



    // индекс цвета делится на 2 и если есть остаток то включить бит для 9bpp
    private fun convertTo9bpp(colorId: Int) = (colorId / 2) or (colorId % 2 shl 8)


    // not used, be removed
    fun convertImageArea(bounds: Rectangle, colorsNumber: Int): ByteArray {

        val byteArray = ByteArray((bounds.height * bounds.width).toInt())
        var counter = 0
        repeat(bounds.height.toInt()) { h ->
            repeat(bounds.width.toInt()) { w ->
//                val hex = res.uploadImagePixmap?.getPixel((w + bounds.x).toInt(), (bounds.height - 1 - h + bounds.y).toInt())?.toUInt()?.toString(16)!!
//                val hexId = if (colorsNumber == 512) hexColor512.indexOfFirst { it.hex.contains(hex) }
//                else hexColor256.indexOfFirst { it.hex.contains(hex) }


                // FIXME некоторые цвета хуйню порят

                val int = res.uploadImagePixmap?.getPixel((w + bounds.x).toInt(), (h + bounds.y).toInt())!!

                val hex = StringBuilder(int.toUInt().toString(16))
                while (hex.length < 8) {
                    hex.insert(0, "0")
                }

                val intId = if (colorsNumber == 512) hexColor512.indexOfFirst { it.int == int }
                else hexColor256.indexOfFirst { it.hex == hex.toString() }
                if (intId < 0) {
                    println(hex)

                }
//                println(res.uploadImagePixmap?.getPixel((w + bounds.x).toInt(), (h + bounds.y).toInt())!!.toString(16))

//                val palId = Agent.data.paletteData.indexOf(hexId.toByte())
//                println(intId)
//                fColor.set(layer.pixmap.getPixel(w + x, height - 1 - h + y))
//                byteArray[counter++] = AppColor.rgbaColors.indexOf(fColor).toByte()
//                byteArray[counter++] = hexId.toByte().toUByte().toByte()
                byteArray[counter++] = intId.toByte()
            }
        }
        return byteArray
    }

}


