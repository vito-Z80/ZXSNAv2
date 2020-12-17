package ru.serdjuk.zxsna.app.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res

@ExperimentalUnsignedTypes
object Selector {

    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f

    private val bounds = Rectangle()
    private val matrix = Matrix4()


    suspend fun update() {
        val x = Gdx.input.x.toFloat()
        val y = Gdx.graphics.height.toFloat() - Gdx.input.y.toFloat()
        val actor = module.stage.hit(x, y, true)?.parent
        if (actor != null) {
            println(actor.hashCode())
        }
        delay(100)
        update()
    }

    val coroutine = CoroutineScope(Dispatchers.Default).launch {
        update()
    }


    fun setPlatform(matrix4: Matrix4, bounds: Rectangle, step: Int, shiftStep: Int) {

    }


    fun contains(point: Vector2) = bounds.contains(point)

}