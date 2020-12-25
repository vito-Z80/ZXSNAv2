package ru.serdjuk.zxsna.app.windows.popup

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.util.ActorUtils
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor
import ru.serdjuk.zxsna.app.utils.buttonOnce

@ExperimentalUnsignedTypes
open class PopupWindow : Group() {
    
    private var hideTimer = 0
    private var hideTime = 60
    
    init {
        this.hashCode().toString().also { name = it }        // FIXME work in native ?
        println(name)
    }
    
    fun newWindow(): ScrollPaneContent {
        addActor(ScrollPaneContent())
        return children.last() as ScrollPaneContent
    }
    
    fun pack() {
        children.forEach { (it as ScrollPaneContent).pack() }
    }
    
    fun hideAll() {
        children.forEach {
            it as ScrollPaneContent
            it.highlight.isVisible = false
            it.isVisible = false
        }
    }
    
    fun nothing() = Unit
    
    
    override fun act(delta: Float) {
        if (children[0].isVisible) {
            // hide, if cursor out of all popup windows for a while
            if (children.all { !it.hasScrollFocus() }) {
                if (hideTimer++ > hideTime) {
                    hideAll()
                    hideTimer = 0
                }
            } else {
                hideTimer = 0
            }
        }
        
        super.act(delta)
    }
    
    //-------------------------------------------------------------
    inner class ScrollPaneContent : ScrollPane(VerticalGroup()) {
        private var maxGroupWidth = 256f
        private var minGroupWidth = 96f
        private var maxGroupHeight = 256f
        private val tmpPosition = Vector2()
        val table = actor as VerticalGroup
        private val drawable = module.skin.getPatch(UI.POPUP_WINDOW)
        val highlight = Image(module.skin.getRegion(UI.LIGHT_GRAY)).also { it.name = "highlight${this.hashCode().toString()}" }
        private val hld = highlight.drawable
        override fun pack() {
            install()
            height = table.children.sumOf { it.height.toInt() }.toFloat().let {
                if (it >= maxGroupHeight) maxGroupHeight else it
            }
            width = table.children.maxOf { it.width }.let {
                MathUtils.clamp(it, minGroupWidth, maxGroupWidth)
            }
            validate()
            table.children.forEach {
                it.x = 0f
                // set touchable width for all items
                it.width = width
            }
            highlight.toBack()
            isVisible = false
//        super.pack()
        }
        
        fun show(x: Float, y: Float) {
            setPosition(x, y - height)
            // FIXME утилита не учитывает размеры сторон бэкраунда (чисто ширина скроллируемой области)
            ActorUtils.keepWithinStage(module.stage, this)
            isVisible = true
        }
        
        private fun install() {
            setFlickScroll(true)
            setScrollingDisabled(true, false)
            setOverscroll(false, false)
            fling(0f, 0f, 0f)
            table.top()
            table.left()
            setBounds(
                drawable.leftWidth,
                drawable.topHeight,
                maxGroupWidth - drawable.rightWidth - drawable.leftWidth,
                maxGroupHeight - drawable.bottomHeight - drawable.topHeight
            )
            highlight.setBounds(0f, 0f, 0f, 0f)
            highlight.isVisible = false
            highlight.touchable = Touchable.disabled
            table.addActor(highlight)
            addListener(object : InputListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    module.stage.scrollFocus = this@ScrollPaneContent
                    return super.touchDown(event, x, y, pointer, button)
                }
                
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    module.stage.scrollFocus = this@ScrollPaneContent
                    super.enter(event, x, y, pointer, fromActor)
                }
                
                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    // FIXME если курсор в актере - не обнулять фокус
                    module.stage.scrollFocus = null
                    super.exit(event, x, y, pointer, toActor)
                }
            })
        }
        
        private fun itemListener(item: Actor, actionTouch: () -> Unit, popupWindow: ScrollPaneContent?) = object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                this@PopupWindow.toFront()
                highlight.isVisible = true
                highlight.x = 0f
                highlight.y = item.y - hld.bottomHeight
                highlight.width = this@ScrollPaneContent.width
                highlight.height = item.height + hld.topHeight
                tmpPosition.set(highlight.x, highlight.y)
                localToStageCoordinates(tmpPosition)
                // hide all windows after this window
                var process = false
                this@PopupWindow.children.forEach {
                    if (process) {
                        it as ScrollPaneContent
                        it.isVisible = false
                        it.highlight.isVisible = false
                    }
                    if (it == this@ScrollPaneContent) process = true
                }
                // calculate position of the received popup window
                if (popupWindow != null) {
                    val rightPositionX = tmpPosition.x + highlight.width + drawable.rightWidth + drawable.leftWidth
                    val popupX = if (rightPositionX + popupWindow.width + drawable.leftWidth + drawable.rightWidth > module.stage.width) {
                        tmpPosition.x - drawable.leftWidth - popupWindow.width - drawable.rightWidth
                    } else {
                        rightPositionX
                    }
                    val popupY = tmpPosition.y + highlight.height - 1f
                    popupWindow.show(popupX, popupY)   // FIXME why -1 ?
                }
                super.enter(event, x, y, pointer, fromActor)
            }
            
            
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (button == 0) {
                    if (popupWindow == null) {
                        hideAll()
                    }
                    actionTouch.invoke()
                }
                return super.touchDown(event, x, y, pointer, button)
            }
        }
        
        /**
         * Add item actor to window, also add functions for listener
         * @param item added item.
         * @param popupWindow PopupWindow window. Will be shown if cursor enter into item.
         * @param actionTouch click to actor action function.
         */
        fun addItem(item: Actor, actionTouch: () -> Unit = this@PopupWindow::nothing, popupWindow: ScrollPaneContent? = null) {
            table.addActor(item)
            item.addListener(itemListener(item, actionTouch, popupWindow))
        }
        
        // FIXME
        fun addSeparator(image: Image? = null) {
            pack()
            if (image == null) {
                val separator = Image(module.skin.getRegion(UI.SEPARATOR8X8))
                separator.touchable = Touchable.disabled
                separator.width = 150f
                separator.height = 20f
                table.addActor(separator)
            } else {
                image.setSize(this@ScrollPaneContent.width, 1f)
                table.addActor(image)
            }
        }
        
        /**
         * Remove items without HIGHLIGHT
         */
        fun removeItems() {
            removeItemsAfter(highlight)
        }
        
        fun removeItemsAfter(item: Actor) {
            while (table.children.peek() != item) {
                table.children.pop()
            }
        }
        
        override fun draw(batch: Batch?, parentAlpha: Float) {
            if (isVisible) {
                drawable.draw(
                    batch,
                    x - drawable.leftWidth,
                    y - drawable.bottomHeight,
                    width + drawable.rightWidth + drawable.leftWidth,
                    height + drawable.topHeight + drawable.bottomHeight
                )
            }
            super.draw(batch, parentAlpha)
        }
        
    }
    
}
