package ru.serdjuk.zxsna.app.windows

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import ru.serdjuk.zxsna.app.system.module

@ExperimentalUnsignedTypes
class CancelDialog(text: String) : Dialog("", module.skin) {


    init {
        isModal = true
        text(" \n$text")
        row()
        pad(40f)
        button("Cancel")
        pack()
        setPosition(
                Gdx.graphics.width / 2f - prefWidth / 2f,
                Gdx.graphics.height / 2f - prefHeight / 2f
        )
        toFront()
        module.stage.addActor(this)
    }

}