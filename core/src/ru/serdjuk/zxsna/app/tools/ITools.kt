package ru.serdjuk.zxsna.app.tools

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ru.serdjuk.zxsna.app.system.UR


abstract class ITools() {


    abstract var toolName: Int

    abstract fun undo(data: UR?)
    abstract fun update(delta: Float)
    abstract fun draw(batch: SpriteBatch)
    abstract fun draw(shape: ShapeRenderer)
}

