package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.windows.LayerMenuInfoWindow

@ExperimentalUnsignedTypes
class ProgressBarDisplay(val layerMenuInfoWindow: LayerMenuInfoWindow) : Image() {

    var maxRectangles = 0
    var rectanglesCounter = 0
    val sprite = Sprite()
    var isStart = false

    init {
        sprite.setRegion(module.skin.getRegion(UI.GRADIENT_GG))
        sprite.setBounds(0f, 0f, 200f, 16f)
        drawable = SpriteDrawable(sprite)
        pack()
    }
    // TODO запилить с шейдерами какуюнить приколюху


    override fun act(delta: Float) {
        if (isStart) {
            val maxSpriteWidth = layerMenuInfoWindow.prefWidth
            val percentWidth = maxSpriteWidth / maxRectangles * rectanglesCounter.toFloat()
            println("maxSpriteWidth: $maxSpriteWidth")
            println("rectangleNumbers: $rectanglesCounter")
            println("preWidth: $percentWidth")
            println(percentWidth)
            if (percentWidth >= maxSpriteWidth) {
                addAction(Actions.removeActor())
            } else {
//                sprite.setBounds(0f, 0f, maxSpriteWidth, 16f)

                width = percentWidth
//                pack()
            }
        }
        super.act(delta)
    }

}