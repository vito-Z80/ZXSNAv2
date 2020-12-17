package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.component.ui.ui
import ru.serdjuk.zxsna.app.windows.ConfirmWindow
import ru.serdjuk.zxsna.app.windows.TextEntryWindow
import ru.serdjuk.zxsna.app.system.tiles.AppSprite
import ru.serdjuk.zxsna.app.utils.RectInt
import ru.serdjuk.zxsna.app.utils.WindowOutScreen
import ru.serdjuk.zxsna.app.utils.doubleClick
import ru.serdjuk.zxsna.app.utils.keyOnce

@ExperimentalUnsignedTypes
class SheetsTest : WindowOutScreen(" Sheets") {
    private val pixmap = res.pixmap

    private var plusButtonWidth = 20f
    private val counterWidth = 64f

    companion object {
        var type = Type.TILES
    }

    enum class Type {
        SPRITES, TILES {

        }
    }

    private var isRemoveKey = false

    // получить место  в текстуре атласа и координаты региона в атласе
    private val position = module.atlasUtils.getFreePosition(2048, 16).let {
        val font = module.skin.getFont(UI.FONT_16).data.getGlyph('?')
        val gReg = res.atlas.findRegion(UI.FONT_16)
        val qReg = RectInt(font.srcX + gReg.regionX, font.srcY + gReg.regionY, font.width, font.height)
        var x = it.x.toInt()
        while (x <= 2048) {
            var y = it.y.toInt()
            var r = 1f
            while (y < it.y.toInt() + 16) {
                pixmap.setColor(r, 0f, 0f, 1f)
                pixmap.fillRectangle(x, y, 16, 1)
                pixmap.drawLine(x, y, x + 16, y)
                y++
                r -= r / 16
            }
            pixmap.drawPixmap(pixmap, x, y - 16, qReg.x, qReg.y, qReg.width, qReg.height)
            x += 16
        }
        module

        it
    }

    private val groups = ArrayList<SheetGroup>()

    init {
        isResizable = true
        top()
        left()
        crateGroupButton(::addNewGroup)
        pack()
        setSize(200f, 200f)
    }

    private fun crateGroupButton(kFunction1: (String) -> Unit) {
        val button = TextButton("+", skin)
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                stage.addActor(TextEntryWindow("Add new group", "group_${groups.size}", kFunction1))
            }
        })
        plusButtonWidth = button.prefWidth
        add(button).row()
    }

    private var preSize = Vector2()

    override fun act(delta: Float) {


        isRemoveKey = keyOnce(Input.Keys.FORWARD_DEL)
//        height = if (height > 512) 512f else if (height < 192f) 192f else height
//        width = if (width > 512) 512f else if (width < 192f) 192f else width
        height = MathUtils.clamp(height, 192f, 512f)
        width = MathUtils.clamp(width, 192f, 512f)
        if (preSize.x != width || preSize.y != height) {
            repackAll()
        }
        preSize.set(width, height)

        var groupSize = groups.size - 1
        while (groupSize > -1) {
            groups[groupSize--].update()
        }
        super.act(delta)
    }

    private fun addNewGroup(name: String) {
        val n = if (name.isEmpty()) "group_${groups.size}" else name
        top()
        left()
        // +-
        val cb = CheckBox("", skin, UI.CHECK_BUTTON)
        cb.name = "cb"
        add(cb).fill()
        // group name - show/hide sprites
        val label = Label(n, ui.styleLabelOut)
        label.wrap = true
        label.setEllipsis(true)
        label.name = "labl"
        val cellLabel = add(label)
        cellLabel.left().width(100f)
        // indexes of sprites in group
        val counter = Label("empty", skin)
        counter.setAlignment(Align.right)
        counter.width = counterWidth
        counter.name = "count"
        val cellCounter = add(counter)
        cellCounter.padRight(8f).right()

        row()
        // container for sprites (image not touchable)
        val container = Image(skin.getDrawable(UI.BUTTON_OFF))
        container.touchable = Touchable.disabled
        val cellContainer = add(container)
        cellContainer.colspan(3).fillX()
        row()

        val group = SheetGroup(cb, cellLabel, counter, cellContainer)
        groups.add(group)
        repackAll()
    }

    private fun getLabelWidth() = width - counterWidth - plusButtonWidth - 14   // 14 = sides of window (7+7)

    private fun repackAll() {
        groups.forEach {
            it.repack()
        }
    }

    // отэта я хз как буду делать....
    // TODO 1) вставлять в таблицу между строками (котейнер с изображениями)
    // TODO 2) перемещать строки ввех/вниз

    //-----------------------------------------------------------------------
    inner class SheetGroup(
        // FIXME  все окутать в CEll
        val cb: CheckBox,
        val label: Cell<Label>,
        val counter: Label,
        val cellContainer: Cell<Image>
    ) {
        val sprites = ArrayList<AppSprite>()
        private val container = cellContainer.actor as Image
        private val l = label.actor as Label
        private var into = false

        private val containerEdges = skin.getDrawable(UI.BUTTON_OFF).let {
            Rectangle(it.leftWidth, it.rightWidth, it.topHeight, it.bottomHeight)
        }

        init {
            l.addListener(object : InputListener() {

                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    into = true
                    l.style = ui.styleLabelIn
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    into = false
                    l.style = ui.styleLabelOut
                    super.exit(event, x, y, pointer, toActor)
                }

                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    if (doubleClick()) {
                        stage.addActor(
                            TextEntryWindow(
                                "Rename",
                                l.text.toString(),
                                this@SheetGroup::rename
                            )
                        )
                    }
                    return super.touchDown(event, x, y, pointer, button)
                }
            })

        }


        fun update() {
            if (into && isRemoveKey) {
                ConfirmWindow("Remove", "Are you sure wanna remove this group ?\n\"${l.text}\"", this::removeRow)
            }
            if (keyOnce(Input.Keys.S)) {
                val s = AppSprite(skin.getRegion(UI.TOOL_FILL))
                sprites.add(s)
//                s.image.setSize(16f,16f)
                this@SheetsTest.addActor(s.image)
                repack()
            }


        }


        fun repack() {
            label.left().width(getLabelWidth()).fillX()
            val spacing = 1f
            val cellSize = 16f
            var x = container.x + containerEdges.x
            var y = container.y + container.imageHeight - containerEdges.width - cellSize
            val width = container.imageWidth
            var rows = 1
            sprites.forEachIndexed { id, sprite ->
                sprite.image.setPosition(x, y)
                x += spacing + cellSize
                if (x > width) {
                    x = container.x + containerEdges.x
                    y -= spacing + cellSize
                    sprite.image.setPosition(x, y)
                    rows++
                }
            }
            cellContainer.height(rows * (cellSize + spacing) + containerEdges.width + containerEdges.height)
        }

        private fun removeRow() {
            this@SheetsTest.removeActor(l)
            this@SheetsTest.removeActor(counter)
            this@SheetsTest.removeActor(cb)
            sprites.forEach {
                it.image.addAction(Actions.removeActor())
            }
            sprites.clear()

            this@SheetsTest.removeActor(container)
            cellContainer.reset()
            groups.remove(this@SheetGroup)
        }

        private fun rename(name: String) {
            (label.actor as Label).setText(name)
            repack()
        }
    }
}