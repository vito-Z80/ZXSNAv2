package ru.serdjuk.zxsna.app.windows.sheets

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.ActorUtils
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor
import ru.serdjuk.zxsna.app.system.tiles.AppSprite
import ru.serdjuk.zxsna.app.utils.WindowOutScreen
import ru.serdjuk.zxsna.app.utils.keyOnce
import ru.serdjuk.zxsna.app.windows.GridArea
import ru.serdjuk.zxsna.app.windows.TextEntryWindow
import ru.serdjuk.zxsna.app.windows.popup.PopupWindow

@ExperimentalUnsignedTypes
class FinalSheets : WindowOutScreen(" Sheets") {
    val table = Table()
    private val scroll = ScrollPane(table)
    val groupList = ArrayList<Cell<GridArea>>()

    init {
        install()
    }

    private fun install() {
        name = this::class.java.name
        top()
        left()

        scroll.setFlickScroll(true)
        scroll.setScrollingDisabled(true, false)
        scroll.setOverscroll(false, false)

        add(scroll).align(Align.topLeft)
        row()

        val menu = Label(name, module.skin, UI.LABEL_JB_LIGHT_BLACK)
        add(menu).align(Align.bottomLeft).expandY()

        pack()




        setSize(256f, 256f)
//        calculateItems()
        toCenter()
    }


    override fun act(delta: Float) {
        if (keyOnce(Input.Keys.O)) {
            module.stage.addActor(
                TextEntryWindow(
                    "Enter group name:",
                    "new_group_${groupList.size}",
                    this::addNewGroup
                )
            )
        }
        super.act(delta)
    }


    private fun groupName(name: String) = Label(name, module.skin, UI.LABEL_JB_LIGHT_BLACK)

    private fun showHideButton(isChecked: Boolean, rowIndex: Int) =
        CheckBox("", skin, UI.CHECK_BUTTON).also {
            it.isChecked = isChecked
            it.pad(2f, 2f, 2f, 4f)
            it.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    val cell = groupList[rowIndex]
                    cell.actor.isVisible = it.isChecked
                    gridBounds(cell)
                    table.invalidateHierarchy()
                }
            })
        }


    fun addItems(group: GridArea, vararg items: TextureRegion) {
        items.forEach { group.addItem(AppSprite(it)) }
    }

    fun addNewGroup(name: String, vararg items: TextureRegion) {
        val rowId = groupList.size
        table.add(showHideButton(true, rowId)).left()

        val label = groupName(name)
        // FIXME если врап включен то текст исчезает когда все бэкгроунды итемов скрыты
//        label.wrap = true
//        label.setEllipsis(true)
        label.pack()
        table.add(label).align(Align.left).expandX()

        val grid = GridArea(
            NinePatchDrawable(module.skin.getPatch(UI.BUTTON_OFF)),
            width - background.leftWidth - background.rightWidth
        )
        table.add(SheetItemsSettings(grid).getButton()).right().row()
        grid.name = name
        if (items.isNotEmpty()) {
            addItems(grid, *items)
        }
        val cell = table.add(grid).colspan(3).fill().height(grid.height)
        gridBounds(cell)
        groupList.add(cell)
        table.row()
        table.invalidateHierarchy()
    }

    private fun settingsButton(grid: GridArea): ImageButton {
        val settingsButton = ImageButton(TextureRegionDrawable(module.skin.getRegion(UI.GEAR16x16)))
        settingsButton.addListener(object : ChangeListener() {
            private val window = object : PopupWindow() {
                val menu = newWindow()
                var itemSpacing = Label("Spacing", module.skin, UI.LABEL_JB_LIGHT_BLACK)
                var itemSize = Label("Item size", module.skin, UI.LABEL_JB_LIGHT_BLACK)

                init {
                    menu.addItem(itemSpacing, fun() {
                        println(grid.spacing)
                    })
                    menu.addItem(itemSize, fun() {
                        println(grid.size)
                    })
                    pack()
                    module.stage.addActor(this)
                }
            }

            override fun changed(event: ChangeEvent?, actor: Actor?) {
                settingsButton.addAction(Actions.run {
                    if (!window.children[0].isVisible) {
                        val first = window.children[0] as PopupWindow.ScrollPaneContent
                        first.setPosition(sensor.screenMouseYUp.x, sensor.screenMouseYUp.y - first.highlight.height)
                        // FIXME утилита не учитывает размеры сторон бэкраунда (чисто ширина скроллируемой области)
                        ActorUtils.keepWithinStage(module.stage, first)
                        first.isVisible = true
                        window.toFront()
                    }
                })
            }
        })
        return settingsButton
    }

    private fun calculateGrids() {
        groupList.forEach {
            gridBounds(it)
        }
//        table.invalidateHierarchy()
    }

    private fun gridBounds(cell: Cell<GridArea>) {
        val width = this.width - background.leftWidth - background.rightWidth
        if (cell.actor.isVisible) {
            cell.actor.repack()
            cell.width(width).height(cell.actor.height)
        } else {
            cell.width(0f).height(0f)
        }
        table.invalidateHierarchy()
    }

}