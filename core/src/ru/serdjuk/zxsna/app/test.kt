package ru.serdjuk.zxsna.app

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.serdjuk.zxsna.app.utils.RectInt
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun filesCollect(): Array<ByteArray> {
    val filesCollection: Array<ByteArray>
    val file = File("C:\\Users\\serdjuk\\Documents\\Adobe").listFiles(FileFilter { !it.isDirectory })
    val d = file?.size
    filesCollection = if (d != null) {
        Array<ByteArray>(d) {
            delay(100)
            println("Load File №:$it")
            file[it].readBytes() ?: byteArrayOf(0)
        }
    } else {
        Array<ByteArray>(0) { byteArrayOf() }
    }
    return suspendCoroutine {
        if (filesCollection.isNotEmpty()) {
            it.resume(filesCollection)
        } else {
            it.resumeWithException(Throwable("aaaaaaaaaaaaaaaaaaaaaa"))
        }
    }
}

@ExperimentalUnsignedTypes
fun main() {

    val size = 8
    val selection = RectInt(0, 0, 24, 32)
    val width = selection.width / size
    val height = selection.height / size
    val rectangles = Array<RectInt>(width * height) { RectInt() }
    // создание ректанглов слеваСверху
    var count = 0
    while (selection.height > 0) {
        selection.height -= size
        var x = 0
        while (x < selection.width) {
            rectangles[count++].set(selection.x + x, selection.y + selection.height, size, size)
            x += size
        }
    }

    println(rectangles.size)
    rectangles.forEach { println(it) }


    return

    val job = CoroutineScope(Dispatchers.Default).launch {
        val r = "coroutine is ending"
        filesCollect().forEach {
            println("File size: ${it.size}")
        }
        println(r)
    }

//    job.start()
//    println(job.children.count())
    println("--------------------------------------------------------------")
    while (true) {
        var result = 0
        repeat(10000000) {
            result += MathUtils.random(10)
        }
        println(result)
    }



    return


    val json = Json()
    val f = File(File("test.zip").absolutePath)
    var uClass = SimpleClass("sfsf", 234, byteArrayOf(32, 4, 23, 11, 111))
    val aPath = File("test.zip").absolutePath

    if (f.exists()) {
        // get path
        val file = ZipInputStream(FileInputStream(f))
        val result = file.readBytes()
        uClass = json.fromJson(
                SimpleClass::class.java,
                f.readText()
        )
        println(json.prettyPrint(uClass))
    } else {
        // new file

        uClass = SimpleClass("Hello", 40, ByteArray(10) { 32 })
        val p = json.toJson(uClass)


        val out = ZipOutputStream(FileOutputStream(f))
        val e = ZipEntry("json1.json")
        out.putNextEntry(e)

        val data = p.toByteArray()
        out.write(data, 0, data.size)
        out.closeEntry()

        out.close()


//        val zip = ZipFile(File("test.zip").absolutePath)
//        println(zip.size())
//        file.writeBytes(zip.toString().toByteArray())
//        println(p)
    }


    val simple = Triple(234, byteArrayOf(1, 2, 3, 4, 5), "hello")
    val result = json.toJson(simple)
    println(result)
//
//    println(json.prettyPrint(json.toJson(result)))
//    val f = Gdx.files.internal("scores.json")
//
//    f.writeString(result, false)


    return


//
//    val a = 50528767
//    println(a.toString(16))


    val b = "-6d929201"
    val long16 = b.toLong(16)
//    val long = b.toLong()
    val longToInt = long16.toInt()
    val intToUInt = longToInt.toUInt()
    val intToHex = intToUInt.toString(16)
    val uIntToHex = intToUInt.toString(16)

    println(b)
    println(long16)

    println(longToInt)
    println(intToUInt)
    println(intToHex)
    println(uIntToHex)

//    val c = -1845493505
//    println(c.toString(16))

}

class SimpleClass(val name: String = "asdasdsa", val age: Int = 32, val foto: ByteArray = byteArrayOf(21, 3, 44, 43)) {

}

class SheetsAll : Json.Serializable {
    var pixmap: ByteArray? = byteArrayOf(1, 2, 3, 4)
    override fun write(json: Json?) {
        json?.writeValue("pixmap", pixmap)
    }

    override fun read(json: Json?, jsonData: JsonValue?) {
        pixmap = jsonData?.child()?.asByteArray()
    }
//    val texture = Texture(pixmap)
}