package ru.serdjuk.zxsna.app.component.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.component.ui.UI

// панель перетаскиваемая/не перетаскиваемая
open class DraggedPane(var isDragged: Boolean = false, var content: Actor? = null) : Image() {
    private val skin = module.skin
    private val patch = skin.getDrawable(UI.DRAGGED_PANE)
    private val p = patch as NinePatchDrawable
    private val listener = object : DragListener() {
        val position = Vector2()
        val offset = Vector2()
        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            if (isDragged && content != null) {
                position.set(x, y)
                localToScreenCoordinates(position)
                offset.set(this@DraggedPane.x - position.x, this@DraggedPane.y - (Gdx.graphics.height - position.y))
                toFront()
                content?.toFront()
            }
            return super.touchDown(event, x, y, pointer, button)
        }


        override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
            if (isDragged && content != null) {
                position.set(x, y)
                localToScreenCoordinates(position)
                position.y = (Gdx.graphics.height - position.y) + offset.y
                position.x += offset.x
                content?.setPosition(position.x + p.leftWidth, position.y + p.bottomHeight)
                this@DraggedPane.setPosition(position.x, position.y)
            }
            super.touchDragged(event, x, y, pointer)
        }

    }

    init {
        drawable = patch
        if (isDragged) {
            this.addListener(listener)
        }
        this.pack()
        contentPack()
    }

    override fun act(delta: Float) {
        if (content != null) content?.isVisible = isVisible

        if (!isDragged && content != null) {
            content?.setPosition(x + p.leftWidth, y + p.bottomHeight)
        }
        super.act(delta)
    }

    fun contentPack() {
        if (content != null) {
            setSize(content!!.width + p.leftWidth + p.rightWidth, content!!.height + p.topHeight + p.bottomHeight)
            content!!.zIndex++
            content?.setPosition(p.leftWidth, p.bottomHeight)
        }
    }


}