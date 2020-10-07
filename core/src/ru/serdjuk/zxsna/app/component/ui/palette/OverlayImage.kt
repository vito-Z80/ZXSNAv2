package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor

class OverlayImage : Image(module.skin.getDrawable(UI.TRANSPARENT_OVERLAY)) {
    private val region = (drawable as TextureRegionDrawable).region
    private val regionY = region.regionY
    private val regionHeight = region.regionHeight
    private var preSpinner = -1

    init {
        touchable = Touchable.disabled
        region.regionY = regionY - sensor.paletteOffset * 17
        region.regionHeight = regionHeight
    }

    private fun setRegionY() {
        if (sensor.paletteOffset != preSpinner) {
            preSpinner = sensor.paletteOffset
//            sensor.spinner = MathUtils.clamp(sensor.spinner + amount, 0, 15)
            region.regionY = regionY - sensor.paletteOffset * 17
            region.regionHeight = regionHeight
        }
    }

    fun setRegionY(amount: Int) {
        sensor.paletteOffset = MathUtils.clamp(sensor.paletteOffset + amount, 0, 15)
        region.regionY = regionY - sensor.paletteOffset * 17
        region.regionHeight = regionHeight
    }

    override fun act(delta: Float) {
        setRegionY()
        if (sensor.mouseWheel != 0 && Rectangle.tmp.set(x, y, width, height).contains(sensor.screenMouse)) {
            setRegionY(sensor.mouseWheel)
        }
        super.act(delta)
    }
}