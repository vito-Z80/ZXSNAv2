package ru.serdjuk.zxsna.app.windows

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.component.ui.ui
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.system.tiles.AppSprite
import ru.serdjuk.zxsna.app.utils.WindowOutScreen
import ru.serdjuk.zxsna.app.utils.doubleClick
import ru.serdjuk.zxsna.app.utils.keyOnce

@ExperimentalUnsignedTypes
class UserSheets(var cellSize: Int = 16, var spacing: Int = 1) : WindowOutScreen("sheets") {

    val userGroup = ArrayList<UserGroup>()           // ячейки группы (+-,name,range and images pane)
    private val table = Table()                           // scroll content
    private val scroll = ScrollPane(table)
    private val leftWidth = background.leftWidth
    private val rightWidth = background.rightWidth
    private val topHeight = background.topHeight
    private val bottomHeight = background.bottomHeight


    init {
        name = this::class.java.name
        width = 300f
        isResizable = true
        top()
        left()
        addMenu()
        scrollConfig()
        pack()
        setSize(300f, 300f)
        repackAll()

    }

    private var preSize = Vector2()
    override fun act(delta: Float) {
        height = MathUtils.clamp(height, 192f, 512f)
        width = MathUtils.clamp(width, 192f, 512f)
        if (preSize.x != width || preSize.y != height) {
            repackAll()
        }
        preSize.set(width, height)


        if (keyOnce(Input.Keys.P)) {
            createUserLine()
        }
        if (keyOnce(Input.Keys.S)) {
            println("add")
            userGroup.forEach { it.addImage(res.atlas.findRegion(UI.TOOL_FILL)) }
        }

//        scroll.cullingArea = Rectangle(scroll.x, scroll.y, scroll.width, scroll.height)
        super.act(delta)
    }


    fun createUserLine(groupName: String? = null) {
        toFront()
        val name = groupName ?: "group_${userGroup.size}"
        table.top()
        table.left()
        // +-
        val cb = CheckBox("", skin, UI.CHECK_BUTTON)
        val checkBoxCell = table.add(cb)
        checkBoxCell.left().width(32f).fillX()
        // group name - show/hide sprites
        val label = Label(name, ui.styleLabelOut)
        label.wrap = true
        label.setEllipsis(true)
        val cellLabel = table.add(label)
        cellLabel.left().fillX()

        // indexes of sprites in group
        val counter = Label("empty", skin)
        counter.width = 64f
        val cellCounter = table.add(counter)
        cellCounter.right().fillX().row()

        val image = Image(module.skin.getPatch(UI.BUTTON_OFF))
        image.touchable = Touchable.disabled
        val imageCell = table.add(image)
        imageCell.width(width - leftWidth - rightWidth).colspan(3).fillX().row()
        userGroup.add(UserGroup(checkBoxCell, cellLabel, cellCounter, imageCell))
    }

    private fun repackAll() {
        table.width = width - leftWidth - rightWidth
        userGroup.forEach { it.repack() }
    }


    private fun addMenu() {
        val addGroupButton = TextButton("+", module.skin)
        add(addGroupButton).left().fillX()
        row()
    }

    private fun scrollConfig() {
        scroll.setFlickScroll(true)
        scroll.setScrollingDisabled(true, false)
        scroll.setOverscroll(false, false)
        scroll.addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                module.stage.scrollFocus = scroll
                super.enter(event, x, y, pointer, fromActor)
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                module.stage.scrollFocus = null
                super.exit(event, x, y, pointer, toActor)
            }
        })
//        scroll.pack()
        add(scroll).fillX().row()
    }

    fun upload(selection: Rectangle, userGroup: UserGroup) {
        // TODO check selection is not empty
        // TODO get pixels from selection and convert to AppSprite (sprite+image) for user group


        println("UPLOAD")
    }

    //-------------------------------------------------------------------------------------
    inner class UserGroup(
        val checkBoxCell: Cell<CheckBox>,
        val labelCell: Cell<Label>,
        val counterCell: Cell<Label>,
        val imageCell: Cell<Image>
    ) {
        val images = ArrayList<AppSprite>()
        val region = TextureRegionDrawable()

        init {
            addListener()
        }

        fun repack() {
            println(table.height)
            labelCell.left()
                .width(this@UserSheets.width - leftWidth - rightWidth - checkBoxCell.actorWidth - counterCell.actorWidth)
            imageCell.width(this@UserSheets.width - leftWidth - rightWidth)
            // images ranking
            var x = imageCell.actor.drawable.leftWidth
//            var y = imageCell.actorY + imageCell.actorHeight - 16f
            var y = table.height - imageCell.actorY + imageCell.actor.drawable.bottomHeight

            var rows = 1
            images.forEach {
                it.image.setPosition(x, y)
                x += 16f + 1f
                if (x > imageCell.actorWidth) {
                    x = imageCell.actor.drawable.leftWidth
                    y -= 16f + 1f
                    rows++
                }
                it.image.toFront()
            }
            imageCell.height(rows * (16f + 1f) + imageCell.actor.drawable.topHeight + imageCell.actor.drawable.bottomHeight)
        }

        fun addImage(textureRegion: TextureRegion) {
            val sprite = AppSprite(textureRegion)
            images.add(sprite)
            table.addActor(sprite.image)
            sprite.image.toFront()
            repack()
        }

        private fun addListener() {
            labelCell.actor.addListener(object : InputListener() {

                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
//                    into = true
                    labelCell.actor.style = ui.styleLabelIn
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
//                    into = false
                    labelCell.actor.style = ui.styleLabelOut
                    super.exit(event, x, y, pointer, toActor)
                }

                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    if (doubleClick()) {
//                        stage.addActor(
//                            TextEntryWindow(
//                                "Rename",
//                                l.text.toString(),
//                                this@SheetGroup::rename
//                            )
//                        )
                    }
                    return super.touchDown(event, x, y, pointer, button)
                }
            })
        }

    }

}