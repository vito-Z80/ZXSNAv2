package ru.serdjuk.zxsna.app.windows

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.utils.keyOnce

@ExperimentalUnsignedTypes
class TextEntryWindow(title: String, private val text: String? = null, private val function: (String) -> Unit) : Window(title, module.skin) {
    
    val field = TextField(text, skin)
    
    init {
        name = this::class.java.name
        isModal = true
        isMovable = false
        add(" ").fill().row()
        add("Enter Name:").width(128f).center().row()
        textField()
        add(" ").fill().row()
        button()
        pack()
        setPosition(
            Gdx.graphics.width / 2f - width / 2f, Gdx.graphics.height / 2f - height / 2f
        )
        module.stage.keyboardFocus = field
    }
    
    
    private fun textField() {
        field.alignment = Align.left
        field.selectAll()
        field.pack()
        add(field).top().center().fill().row()
    }
    
    private fun button() {
        val b = TextButton("Ok", skin)
        b.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                sendText()
            }
        })
        add(b).center().row()
    }
    
    override fun act(delta: Float) {
        if (keyOnce(Input.Keys.ENTER)) sendText()
        if (keyOnce(Input.Keys.ESCAPE)) disable()
        super.act(delta)
    }
    
    private fun sendText() {
        function.invoke(field.text)
        disable()
    }
    
    private fun disable() {
        addAction(Actions.removeActor(this))
    }
    
}