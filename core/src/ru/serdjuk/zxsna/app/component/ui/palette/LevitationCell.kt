package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ru.serdjuk.zxsna.app.utils.buttonHold
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.resources.hexColor512
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.system.sensor

@ExperimentalUnsignedTypes
class LevitationCell : Image(module.skin.getDrawable(UI.LEVITATION_CELL)) {

    private val region = (drawable as TextureRegionDrawable).region
    private var colorId = 0

    init {
        isVisible = false
        pack()

    }

    // FIXME когда такскаем ячейку карандаш рисует и не заносит в историю нарисованные пиксели
    // FIXME такскае переделать с листенера, так как deltaXY нарушает временные рамки при таскании и outActor() не срабатывает
    override fun act(delta: Float) {
        if (buttonHold(0)) {
            setPosition(x + Gdx.input.deltaX, y - Gdx.input.deltaY)
        } else {
            if (isVisible) {
                sensor.landingCell.colorId = colorId
                sensor.landingCell.hexColor = hexColor512[colorId].hex
                isVisible = false
            }
        }




        super.act(delta)
    }

    fun show(colorId: Int, position: Vector2) {
        this.colorId = colorId
        res.pixmap.setColor(hexColor512[colorId].int)
        res.pixmap.fillRectangle(
                region.regionX + 1,
                region.regionY + 1,
                PaletteData.cellSize - 2,
                PaletteData.cellSize - 2
        )
        res.textureUpdate()
        setPosition(position.x, position.y)
        toFront()
        isVisible = true
    }


}