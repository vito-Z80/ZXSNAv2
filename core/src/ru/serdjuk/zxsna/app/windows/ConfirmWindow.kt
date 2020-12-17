package ru.serdjuk.zxsna.app.windows

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.utils.keyOnce
import kotlin.reflect.KFunction0

@ExperimentalUnsignedTypes
class ConfirmWindow(title: String, text: String, private val function: KFunction0<Unit>) : Window(title, module.skin) {

    init {
        isModal = true
        isMovable = false
        val label = Label(text, skin)
//        label.wrap = true
        label.setAlignment(Align.center)
        add("").row()
        add(label).colspan(2).center().row()
        add("").row()
        add(cancelButton()).left().fill()
        add(confirmButton()).right().fill().row()
        pack()
        setPosition(
            Gdx.graphics.width / 2f - width / 2f,
            Gdx.graphics.height / 2f - height / 2f
        )
        module.stage.addActor(this)
    }

    override fun act(delta: Float) {
        if (keyOnce(Input.Keys.ESCAPE)) {
            removeWindow()
        }
        if (keyOnce(Input.Keys.ENTER)) {
            function.invoke()
            removeWindow()
        }

        super.act(delta)
    }

    private fun confirmButton(): TextButton {
        val button = TextButton("Confirm", skin)
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                function.invoke()
                removeWindow()
            }
        })
        return button
    }

    private fun cancelButton(): TextButton {
        val button = TextButton("Cancel", skin)
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                removeWindow()
            }
        })
        return button
    }

    private fun removeWindow() {
        addAction(Actions.removeActor())
    }

}