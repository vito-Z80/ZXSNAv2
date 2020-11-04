package ru.serdjuk.zxsna.app.component.world.layers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ru.serdjuk.zxsna.app.component.ui.palette.AppPaletteWindow
import ru.serdjuk.zxsna.app.system.pack.Compressable

@ExperimentalUnsignedTypes
class AppLayers : Compressable {

    private val storage = Array<TextureLayer>(16) { TextureLayer(1024, 1024) }

    fun draw(batch: SpriteBatch) {
        batch.draw(storage[AppPaletteWindow.offset].region, 0f, 0f)
    }

    fun update(delta: Float) {
        storage[AppPaletteWindow.offset].update(delta)
    }


    override fun compress(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun uncompress(bytes: ByteArray) {
        TODO("Not yet implemented")
    }
}