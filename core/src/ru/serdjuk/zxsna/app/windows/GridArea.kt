package ru.serdjuk.zxsna.app.windows

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import ru.serdjuk.zxsna.app.system.tiles.AppSprite
import kotlin.math.abs

@ExperimentalUnsignedTypes
class GridArea(private val bg: Drawable, areaWidth: Float) : Container<Group>(Group()) {

    var size = 32f
    var spacing = 1f
    val storage = ArrayList<AppSprite>()


    init {
        align(Align.topLeft)
        width = areaWidth
        setBackground(bg, true)
    }

    fun repack() {
        val width = this.width - background.leftWidth - background.rightWidth
        var x = 0f
        var y = -size
        actor.children.forEachIndexed { index, actor ->
            if (x + size + spacing > width) {
                x = 0f
                y -= (size + spacing)
            }
            actor.setPosition(x, y)
            x += (size + spacing)
        }
        height = abs(y) + background.topHeight + background.bottomHeight
    }

    fun addItems(vararg sprites: AppSprite) {
        sprites.forEach {
            storage.add(it)
            actor.addActor(it.image)
        }
        repack()
    }

    fun addItem(sprite: AppSprite) {
        sprite.image.setSize(size, size)
        sprite.image.addListener(object : DragListener() {


            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                sprite.color = Color.LIGHT_GRAY
                super.enter(event, x, y, pointer, fromActor)
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                sprite.color = Color.WHITE
                super.exit(event, x, y, pointer, toActor)
            }


            override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                sprite.image.toFront()
                super.dragStart(event, x, y, pointer)
            }


            override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                sprite.image.x += Gdx.input.deltaX
                sprite.image.y -= Gdx.input.deltaY

                super.drag(event, x, y, pointer)
            }

            override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                repack()
                super.dragStop(event, x, y, pointer)
            }
        })
        storage.add(sprite)
        actor.addActor(sprite.image)
        repack()
    }

//    fun display(visible: Boolean) {
//        isVisible = visible
//        actor.children.forEach { it.isVisible = visible }
//    }

}