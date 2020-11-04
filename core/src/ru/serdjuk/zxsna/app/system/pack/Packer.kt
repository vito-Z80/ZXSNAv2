package ru.serdjuk.zxsna.app.system.pack

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.utils.getBytes
import ru.serdjuk.zxsna.app.utils.getInt
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets.UTF_8
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

@ExperimentalUnsignedTypes
class Packer {

    private val gson = Gson()
    private val builder = GsonBuilder().create()
    private val values = ArrayList<PackBytes>()
    private val data = ArrayList<Byte>()
    private var position = 0

    private fun add(name: String, byteArray: ByteArray, width: Int = 0, height: Int = 0) {
        data.addAll(byteArray.toTypedArray())
        values.add(PackBytes(name, position, data.size, width, height))
        position = data.size
    }

    fun compress(): ByteArray {
        clear()
        res.layers.forEachIndexed { id, data ->
            data.pixmap.pixels.clear()
            val ba = ByteArray(data.pixmap.pixels.remaining())
            data.pixmap.pixels.get(ba)
            data.pixmap.pixels.clear()
            add("${PackName.SPRITE_LAYER}$id", ba, 1024, 1024)
        }

        // FIXME вернуть когда разъе6емся с палитрой
//        add(PackName.SPRITE_PALETTE, PaletteUtils.convertPaletteTo())


        // add the values
        val g = gson.toJson(values)
        data.addAll(g.toByteArray(UTF_8).toTypedArray())
        // add the start position of the values
        data.addAll(getBytes(position).toTypedArray())
        println(data.size)
        return gzip(data.toByteArray())
    }

    fun decompress(content: ByteArray) {
        val data = unGzip(content)
        val textPosition = getInt(data.copyOfRange(data.size - 4, data.size))

        val type = object : TypeToken<ArrayList<PackBytes>>() {}.type
        val t = data.copyOfRange(textPosition, data.size - 4).toString(UTF_8)

        values.clear()
        values.addAll(gson.fromJson(t, type))

        values.forEach {
            if (it.type.contains(PackName.SPRITE_LAYER)) {
                val id = it.type.split("_").let { l -> l[l.size - 1].toInt() }
//                res.layers[id].pixmap.pixels.position(0)
                res.layers[id].pixmap.pixels.put(data.copyOfRange(it.startPosition, it.endPosition))
                res.layers[id].pixmap.pixels.clear()    // падла
                //FIXME убрать отсюда перерисовку текстуры в случае обертывания в корутину !!!!
                res.layers[id].redrawTexture()
            }

            when (it.type) {
                PackName.SPRITE_PALETTE -> {
                    println(it.type)
                    val paletteData = data.copyOfRange(it.startPosition, it.endPosition)
                    // FIXME вернуть когда разъе6емся с палитрой
//                    PaletteUtils.convertPaletteFrom(paletteData)
                }
                else -> Unit
            }
        }


        System.gc()

//        println(text)

//        values.addAll(gson.fromJson(g, arrayTutorialType))
    }

    private fun gzip(content: ByteArray): ByteArray {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).buffered(1024).use { it.write(content) }
        return bos.toByteArray()
    }

    private fun unGzip(content: ByteArray): ByteArray = GZIPInputStream(content.inputStream()).buffered(1024).use { it.readBytes() }

//    init {
//
////        val type = GAdapter(PackData(0,0,0,0))
////        builder.registerTypeAdapter(PackData::class.java, GAdapter(PackData(0,0,0,0)))
//
//
//        res.layers.forEachIndexed { id, data ->
//            val ba = ByteArray(data.pixmap.pixels.remaining())
//            data.pixmap.pixels.get(ba)
//            add("${PackName.SPRITE_LAYER}$id", ba, 1024, 1024)
////            this.data.addAll(ba.toTypedArray())
////            values.add(PackBytes("${PackName.SPRITE_LAYER}$id", position, this.data.size, 1024, 1024))
////            position = this.data.size
//        }
//
//        add(PackName.SPRITE_PALETTE, PaletteUtils.convertPaletteTo())
////        data.addAll(PaletteUtils.convertPaletteTo().toTypedArray())
////        values.add(PackBytes(PackName.SPRITE_PALETTE, position, this.data.size))
////        position = this.data.size
//
//
////        repeat(10) {
////            array.add(PackBytes(CompressionType.SPRITE_LAYER, it, 0, 0, 0))
//////            array.add(PackData(0, 0, 0, 0))
////        }
////        val spr = SpriteLayersPack("name___", 3)
////        spr.compress()
////        array.add(spr)
//        val g = gson.toJson(values)
//        println(g)
//        println("______________________________")
//        //-------------------------------------
//
//        values.clear()
//        val arrayTutorialType = object : TypeToken<ArrayList<PackBytes>>() {}.type
////        var tutorials: ArrayList<Compression> = gson.fromJson(g, array::class.java)
//
//        values.addAll(gson.fromJson(g, arrayTutorialType))
//
//        val s = values.filterIsInstance<TestPack>()
//        val b = values.filterIsInstance<SpriteLayersPack>()
//        println(s.size)
//        println("++++++++++++")
//
//
//        values.forEach { println(it.type) }
//
//        println(values.find { it.type == PackName.SPRITE_PALETTE }?.startPosition)
//
////        println(b[0].name)
////        array.forEach { it.compress() }
////        tutorials.forEachIndexed { idx, tut -> println("> Item ${idx}:\n${tut}") }
//
//
////        val c: LayerPack = gson.fromJson(map["sprite layer_1"], LayerPack::class.java)
//
//
//    }

    fun clear() {
        position = 0
        values.clear()
        data.clear()
    }


}

@ExperimentalUnsignedTypes
val packer = Packer()

class PackName {
    companion object {
        const val SPRITE_LAYER = "SPRITE_LAYER_"
        const val SPRITE_PALETTE = "SPRITE_PALETTE_"
    }
}