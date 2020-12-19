package ru.serdjuk.zxsna.app.windows

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.kotcrab.vis.ui.util.ActorUtils
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.utils.keyOnce
import kotlin.math.min

@ExperimentalUnsignedTypes
class PopupGroup : WidgetGroup() {
//    val scroll = ScrollPane(this)
    var maxGroupWidth = 256f
    var maxGroupHeight = 256f
    private val drawable = module.skin.getPatch(UI.BUTTON_OFF)
    private val highlight = Image(module.skin.getPatch(UI.BUTTON_ON))
    private val hld = highlight.drawable
    private val items = ArrayList<Actor>()
    
    init {
//        scroll.setFlickScroll(true)
//        scroll.setScrollingDisabled(true, false)
//        scroll.setOverscroll(false, false)
        highlight.setBounds(0f, 0f, 0f, 0f)
//        addActor(scroll)
        addActor(highlight)
        repack()
    }
    
    override fun draw(batch: Batch?, parentAlpha: Float) {
        drawable.draw(batch, x, y, width, height)
        super.draw(batch, parentAlpha)
    }
    
    override fun act(delta: Float) {
        if (keyOnce(Input.Keys.F)) {
            val label = Label("Item # ${children.size}. Almost before we knew it, we had left the ground.", module.skin, UI.LABEL_BN_LIGHT_BLACK)
            label.wrap = true
            label.setEllipsis(true)
            addItem(label)
        }
        if (keyOnce(Input.Keys.C)) {
            clearItems()
        }
        super.act(delta)
    }
    
    
    private fun repack() {
        highlight.isVisible = false
        highlight.setBounds(0f, 0f, 0f, 0f)
        if (children.isEmpty) {
            setBounds(0f, 0f, 0f, 0f)
            this.isVisible = false
            return
        }
        this.isVisible = true
        val height = children.sumByDouble { it.height.toDouble() }.toFloat()
        this.height = min(height + drawable.topHeight + drawable.bottomHeight, maxGroupHeight)
        var number = children.size
        var y = drawable.bottomHeight
        while (--number > -1) {
            children[number].y = y
            y += children[number].height
        }
        ActorUtils.keepWithinStage(module.stage, this)
    }
    
    private fun addItem(item: Actor) {
        width = MathUtils.clamp(item.width, width, maxGroupWidth)
        item.x = drawable.leftWidth
        item.width = width - drawable.leftWidth - drawable.rightWidth
        items.add(item)
        addActor(item)
        repack()
        item.addListener(itemListener(item))
    }
    
    fun removeItem(item: Actor) {
        children.removeValue(item, true)
        items.remove(item)
    }
    
    fun insertItem(index: Int, item: Actor) {
        children.insert(index, item)
        items.add(index, item)
    }
    
    fun clearItems() {
        children.clear()
        items.clear()
        repack()
    }
    
    private fun itemListener(item: Actor) = object : InputListener() {
        override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
            highlight.isVisible = true
            highlight.x = item.x - hld.leftWidth
            highlight.y = item.y - hld.bottomHeight
            highlight.width = item.width + hld.leftWidth + hld.rightWidth
            highlight.height = item.height + hld.topHeight + hld.topHeight
            super.enter(event, x, y, pointer, fromActor)
        }
    }
    
    
    fun show(popupWindow: PopupGroup, actor: Actor) {
        if (!module.stage.root.children.contains(popupWindow)) {
            module.stage.addActor(this)
        }
        isVisible = true
        // TODO calculate position
    }
    
}
