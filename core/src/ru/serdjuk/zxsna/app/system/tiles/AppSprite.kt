package ru.serdjuk.zxsna.app.system.tiles

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res

@ExperimentalUnsignedTypes
class AppSprite(val textureRegion: TextureRegion) : Sprite(textureRegion) {

    val image = Image(SpriteDrawable(this)).also { it.pack();it.toFront();it.setSize(16f, 16f) }
    val name = StringBuilder()
    var paletteOffset = -1
    var isVisible = false


    init {
        upload(textureRegion)
        flip(false, true)
    }


    /**
     * Upload region from layer texture to main texture.
     * Use flip Y:
     * Before process - for copy region. (if function will be call more one time.)
     * After process - for normal display.
     * @param textureRegion is layer region.
     */
    fun upload(textureRegion: TextureRegion) {
        val from = textureRegion.texture.textureData.consumePixmap()
        val position = module.atlasUtils.getFreePosition(textureRegion.regionWidth, textureRegion.regionHeight)
        res.pixmap.drawPixmap(
            from, position.x.toInt(), position.y.toInt(),
            textureRegion.regionX, textureRegion.regionY, textureRegion.regionWidth, textureRegion.regionHeight
        )
        this.textureRegion.setRegion(
            position.x.toInt(), position.y.toInt(), textureRegion.regionWidth, textureRegion.regionHeight
        )
        res.textureUpdate()
    }


}