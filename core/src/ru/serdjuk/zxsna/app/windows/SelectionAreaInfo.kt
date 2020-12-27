package ru.serdjuk.zxsna.app.windows

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module

@ExperimentalUnsignedTypes
class SelectionAreaInfo : Group() {
    private val background = Image(module.skin.getPatch(UI.POPUP_WINDOW))
    private val labelX = Label("", module.skin, UI.LABEL_JB_LIGHT_BLACK)
    private val labelY = Label("", module.skin, UI.LABEL_JB_LIGHT_BLACK)
    private val labelWidth = Label("", module.skin, UI.LABEL_JB_LIGHT_BLACK)
    private val labelHeight = Label("", module.skin, UI.LABEL_JB_LIGHT_BLACK)

    val boundsInfo = Rectangle()

    init {
        addActor(background)
        addActor(labelX)
        addActor(labelY)
        addActor(labelWidth)
        addActor(labelHeight)
        module.stage.addActor(this)
    }


    override fun act(delta: Float) {
        isVisible = boundsInfo.width != 0f || boundsInfo.height != 0f
        if (isVisible) {
            toFront()
            setText()
            setBounds()
        } else return
        super.act(delta)
    }

    private fun setBounds() {
        background.x = boundsInfo.x
        background.y = boundsInfo.y
        background.width = boundsInfo.width
        background.height = boundsInfo.height


    }

    private fun setText() {


    }

}