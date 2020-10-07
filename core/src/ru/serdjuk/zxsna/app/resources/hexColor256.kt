package ru.serdjuk.zxsna.app.resources

import com.badlogic.gdx.Gdx


@ExperimentalUnsignedTypes
val hexColor256 = lazy {
    val s = Gdx.files.internal("palette/hexColor256.txt").readString().split("\n")
    val map = Array<AppColor>(s.size) {
        val hex = s[it].trimIndent()
        val int = hex.toLong(16).toInt()
        AppColor(hex, int)
    }
    map
}.value
