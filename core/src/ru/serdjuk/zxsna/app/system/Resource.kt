package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

@ExperimentalUnsignedTypes
class Resource {
    val atlas = TextureAtlas("zxsna.atlas")



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

@ExperimentalUnsignedTypes
val res = lazy { Resource() }.value