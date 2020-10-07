package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ru.serdjuk.zxsna.app.resources.TextureLayer

@ExperimentalUnsignedTypes
class Resource {
    val atlas = TextureAtlas("zxsna.atlas")

    // текстура спрайтов и тайлов разделенная попалам по Y
    // Y 0-255 = sprites
    // Y 256-511 = tiles
    val sheetsPixmap = Pixmap(512, 512, Pixmap.Format.RGBA8888)
    val sheetsTexture = Texture(sheetsPixmap)


    val pixmap = Pixmap(Gdx.files.internal("zxsna.png"))
    val texture = lazy {
        val t = Texture(pixmap)
        atlas.regions[0].texture.dispose()
        atlas.textures.clear()
        atlas.textures.add(t)
        atlas.regions.forEach { it.texture = t }
        t.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        t
    }.value
    val text = null
    val layers = Array<TextureLayer>(16) { TextureLayer(1024, 1024) }


    var uploadImagePixmap: Pixmap? = null
    var uploadImageTexture: Texture? = null
    fun disposeUploadImage() {
        lazy {
            uploadImagePixmap?.dispose()
            uploadImageTexture?.dispose()
            uploadImagePixmap = null
            uploadImageTexture = null
        }.value
    }

    fun textureUpdate() {
        texture.draw(pixmap, 0, 0)
    }
}

val res = lazy { Resource() }.value