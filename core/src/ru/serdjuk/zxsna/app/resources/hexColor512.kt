package ru.serdjuk.zxsna.app.resources

import com.badlogic.gdx.Gdx

//val hexColor512 = lazy {
//    // FIXME перегнать в нормальный файл и сохранить для использования
//    Gdx.files.internal("palette/hexColor512").readString().let { s ->
//        val l = s.split("\\n\" +")
//        List(512) { l[it].trim().slice(1..8) }.reversed()
//    }
//}.value

class AppColor(val hex: String, val int: Int)

@ExperimentalUnsignedTypes
val hexColor512 = lazy {
    val s = Gdx.files.internal("palette/hexColor512.txt").readString().split("\n")
    val map = Array<AppColor>(s.size) {
//        AppColor(s[it].trim(), s[it].trim().toLong(16).toInt())
        val hex = s[it].trimIndent()
        val int =  hex.toLong(16).toInt()
        AppColor(hex, int)
    }
    map
}.value