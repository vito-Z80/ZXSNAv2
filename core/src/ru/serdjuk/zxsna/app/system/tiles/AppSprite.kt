package ru.serdjuk.zxsna.app.system.tiles

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream

class AppSprite(val textureRegion: TextureRegion) : Sprite(textureRegion) {

    val image = Image(SpriteDrawable(this)).also { it.pack();it.toFront();it.setSize(16f,16f) }
    val name = StringBuilder()
    var paletteOffset = -1
    var isVisible = false

    // TODO начать делать сохранение и загрузку всего проекта
    // TODO все конвертим в массив байт с адресами (ID), следом ZIP и на диск
    // TODO и обратные действия


}