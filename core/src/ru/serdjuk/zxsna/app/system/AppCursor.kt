package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor

class AppCursor {

//    val sc = Pixmap(32, 32, Pixmap.Format.RGBA8888)
//    val selectCursor = Gdx.graphics.newCursor(sc, 0, 0)
//
//    init {
//        sc.setColor(Color.BLACK)
//        sc.fill()
//        sc.dispose()
//    }

    fun setSystem(cursor: Cursor.SystemCursor) {
        Gdx.graphics.setSystemCursor(cursor)
    }

}

val cursor = AppCursor()