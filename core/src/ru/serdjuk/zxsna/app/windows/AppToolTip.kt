package ru.serdjuk.zxsna.app.windows

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module

@ExperimentalUnsignedTypes
class AppToolTip : Group() {
    
    private val image = Image(module.skin.getPatch(UI.BUTTON_ON))
    private val imageEdges = module.skin.getPatch(UI.BUTTON_ON)
    private val label = Label("", module.skin, UI.LABEL_BN_LIGHT_BLACK)
    private var hash = 0
    
    
    init {
        image.isVisible = false
        label.isVisible = false
        image.touchable = Touchable.disabled
        label.touchable = Touchable.disabled
        addActor(image)
        addActor(label)
        module.stage.addActor(this)
        CoroutineScope(Dispatchers.Default).launch {
            process()
        }
    }
    
    private suspend fun process() {
        var show = true
        while (true) {
            val x = Gdx.input.x.toFloat()
            val y = (Gdx.graphics.height - Gdx.input.y).toFloat()
            val target = module.stage.hit(x, y, true)
            
            if (target != null && target is HelpMessage) {
                if (target.hashCode() != hash) {
                    hash = target.hashCode()
                    show = true
                }
                if (show) {
                    // TODO просчет позиции на экране, если актер выходит за экран.
                    show = false
                    label.setText(target.message)
                    label.setPosition(x, y)
                    label.isVisible = true
                    label.actions.clear()
                    label.color.a = 1f
                    label.pack()
                    image.setPosition(x - imageEdges.leftWidth, y - imageEdges.topHeight)
                    val imgWidth = label.width + imageEdges.leftWidth + imageEdges.rightWidth
                    val imgHeight = label.height + imageEdges.topHeight + imageEdges.bottomHeight
                    image.setSize(imgWidth, imgHeight)
                    image.isVisible = true
                    image.actions.clear()
                    image.color.a = 1f
                    toFront()
                }
            } else {
                label.addAction(Actions.fadeOut(0.25f))
                image.addAction(Actions.fadeOut(0.25f))
                show = true
            }
            delay(125)
        }
    }
    
}