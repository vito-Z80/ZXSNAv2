package ru.serdjuk.zxsna.app.system.pack

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import ru.serdjuk.zxsna.app.system.compression.Compression
import ru.serdjuk.zxsna.app.system.res
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets.UTF_8
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

@ExperimentalUnsignedTypes
class PackCollector {
    private val gson = Gson()
    private val builder = GsonBuilder().create()
    private val array = ArrayList<Compression>()

    fun collect(): ByteArray {
        array.clear()
        val layer = SpriteLayersPack("name_0", 0)
        layer.compress()
        array.add(layer)


//        fillArray()
        return gzip(gson.toJson(array))
    }

    fun parse(content: ByteArray): ArrayList<Compression> {
        array.clear()
        val text = unGzip(content)
        val data: ArrayList<Compression> = gson.fromJson(text, array::class.java)
        array.addAll(data)

        val a = array.filterIsInstance<SpriteLayersPack>()

        println(a.size)
        array.forEach {
            println((it as SpriteLayersPack).name)
        }
//        array.forEach { it.deCompress() }

//        var tutorials: ArrayList<Compression> = gson.fromJson(g, array::class.java)
//        var tutorials: ArrayList<Compression> = gson.fromJson(content, array::class.java)
        //
        return array
    }

    private fun gzip(content: String): ByteArray {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).buffered().bufferedWriter(UTF_8).use { it.write(content) }
        return bos.toByteArray()
    }

    private fun unGzip(content: ByteArray): String {
        return GZIPInputStream(content.inputStream()).buffered().bufferedReader(UTF_8).use { it.readText() }
    }

    private fun fillArray() {

//        res.layers.forEachIndexed { id, data ->
//            val layer = SpriteLayersPack("name_$id", id)
//            layer.compress()
//            array.add(layer)
//        }
    }
}


@ExperimentalUnsignedTypes
val packCollector = PackCollector()

class PackBytes(val type: String, val startPosition: Int, val endPosition: Int, val width: Int = 0, val height: Int = 0)


class PalettePack(val name: String)
class SpriteSheetsPack(val name: String, val numbers: Int)


@ExperimentalUnsignedTypes
class SpriteLayersPack(val name: String, val layerId: Int, var data: ByteArray? = null) : Compression {
    override fun compress() {
//        data = ByteArray(res.layers[layerId].pixmap.pixels.remaining())
//        res.layers[layerId].pixmap.pixels.get(data)
    }

    override fun deCompress() {
        println("CAST BLYAT")
//        res.layers[layerId].name = name
//        val bb = ByteBuffer.wrap(data)
//        res.layers[layerId].pixmap.pixels.put(bb)
    }

}

class TestPack(val pt: Int) : Compression {

    override fun compress() {
        println("AAAAAAAAAAAAA")
    }

    override fun deCompress() {

    }

}


@ExperimentalUnsignedTypes
class GAdapter(packBytes: PackBytes) : InstanceCreator<ArrayList<PackBytes>> {
    val data = packBytes

    override fun createInstance(type: Type?): ArrayList<PackBytes> {
        return ArrayList<PackBytes>()
    }

}



