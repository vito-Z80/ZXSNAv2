package ru.serdjuk.zxsna.app.windows.sheets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kotcrab.vis.ui.util.ActorUtils
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor
import ru.serdjuk.zxsna.app.windows.GridArea
import ru.serdjuk.zxsna.app.windows.popup.PopupWindow

@ExperimentalUnsignedTypes
class SheetItemsSettings(private val grid: GridArea) : PopupWindow() {
    private val menu = newWindow()
    private val spacingValuesWindow = newWindow()
    private val sizesValuesWindow = newWindow()


    fun getButton(): ImageButton {

        val itemSpacing = Label("Spacing", module.skin, UI.LABEL_JB_LIGHT_BLACK)
        val itemSize = Label("Item size", module.skin, UI.LABEL_JB_LIGHT_BLACK)
        menu.addItem(itemSpacing, popupWindow = spacingValuesWindow)
        menu.addItem(itemSize, popupWindow = sizesValuesWindow)

        spacingValues()
        sizeValues()
        val settingsButton = ImageButton(TextureRegionDrawable(module.skin.getRegion(UI.GEAR16x16)))
        settingsButton.addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                settingsButton.image.color = Color.BLACK
                super.enter(event, x, y, pointer, fromActor)
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                settingsButton.image.color = Color.WHITE
                super.exit(event, x, y, pointer, toActor)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                settingsButton.addAction(Actions.run {
                    if (!children[0].isVisible) {
                        val first = children[0] as PopupWindow.ScrollPaneContent
                        first.setPosition(sensor.screenMouseYUp.x, sensor.screenMouseYUp.y - first.highlight.height)
                        // FIXME утилита не учитывает размеры сторон бэкраунда (чисто ширина скроллируемой области)
                        ActorUtils.keepWithinStage(module.stage, first)
                        first.isVisible = true
                        toFront()
                    }
                })
                return super.touchDown(event, x, y, pointer, button)
            }
        })
        pack()
        module.stage.addActor(this)
        return settingsButton
    }

    private fun sizeValues() {
        repeat(3) {
            val value = Label("${(it+1) * 8}", module.skin, UI.LABEL_BN_LIGHT_WHITE)
            value.color = Color.GRAY
            sizesValuesWindow.addItem(value, fun() {
                sizesValuesWindow.table.children.forEach { item -> item.color = Color.GRAY }
                value.color = Color.BLACK
                grid.size = (it+1) * 8f
                grid.repack()
            })
        }
    }

    private fun spacingValues() {
        repeat(9) {
            val value = Label("$it", module.skin, UI.LABEL_BN_LIGHT_WHITE)
            value.color = Color.GRAY
            spacingValuesWindow.addItem(value, fun() {
                spacingValuesWindow.table.children.forEach { item -> item.color = Color.GRAY }
                value.color = Color.BLACK
                grid.spacing = it.toFloat()
                grid.repack()
            })
        }
    }

}