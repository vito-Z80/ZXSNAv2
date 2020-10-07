package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class InsertRegion(texture: Texture, x: Int, y: Int, width: Int, height: Int, val index: Int = -1, val name: String = "${globalId++}") :
        TextureRegion(texture, x, y, width, height) {

    init {
        super.setTexture(texture)
    }


    companion object {
        var globalId = 0
    }


}