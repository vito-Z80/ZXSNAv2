package ru.serdjuk.zxsna.app.windows.popup

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.util.ActorUtils
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor
import ru.serdjuk.zxsna.app.windows.TextEntryWindow
import ru.serdjuk.zxsna.app.windows.UserSheets

/*
    create FIRST window
        add items to FIRST window
        add SECOND windows to FIRST window
            add items to SECOND window
            add THIRD window to SECOND window
                ...

    install POPUP
        POPUP add item
        POPUP add item
        POPUP add item
        POPUP add new POPUP


 */



@ExperimentalUnsignedTypes
class AppSelectionMenu : PopupWindow() {
    
    private val mainMenu = newWindow()
    private val uploadMenu = newWindow()
    private val tileGroupsMenu = newWindow()
//    private val spriteGroupsMenu = newWindow()
    
    
    init {
        createMain()
        fillUploadMenu()
        fillTileGroups()


//        addActor(mainMenu)
//        addActor(uploadMenu)
//        addActor(tileGroupsMenu)
        module.stage.addActor(this)
        pack()
    }
    
    
    fun show() {
        if (!children[0].isVisible) {
            val first = children[0] as ScrollPaneContent
            first.setPosition(sensor.screenMouseYUp.x, sensor.screenMouseYUp.y - first.highlight.height)
            // FIXME утилита не учитывает размеры сторон бэкраунда (чисто ширина скроллируемой области)
            ActorUtils.keepWithinStage(module.stage, first)
            first.isVisible = true
        }
    }
    
    private fun fillUploadMenu() {
        testFill(uploadMenu)
        val uploadTiles = Label("Tiles", module.skin, UI.LABEL_BN_LIGHT_BLACK)
        val uploadSprites = Label("Sprites", module.skin, UI.LABEL_BN_LIGHT_BLACK)
        uploadMenu.addItem(uploadTiles, popupWindow = tileGroupsMenu)
        uploadMenu.addItem(uploadSprites)
//        uploadMenu.pack()
        uploadMenu.setPosition(300f, 300f)
    }
    
    private fun fillTileGroups() {
        val groupsIsEmpty = Label("Add new group", module.skin)
        val sheets = module.stage.root.findActor<UserSheets>(UserSheets::class.java.name)
        fun fillGroups() {
            println(tileGroupsMenu.table.children.size)
            tileGroupsMenu.removeItemsAfter(groupsIsEmpty)
            sheets.userGroup.forEach {
                val label = Label(it.labelCell.actor.text, module.skin, UI.LABEL_BN_LIGHT_BLACK)
                tileGroupsMenu.addItem(label)
            }
        }
        if (sheets != null) {
            tileGroupsMenu.addItem(groupsIsEmpty, actionTouch = fun() {
                val text = "group_${sheets.userGroup.size}"
                module.stage.addActor(TextEntryWindow("Add new group", text, fun(str: String) {
                    sheets::createUserLine.invoke(str)
                    fillGroups()
                    tileGroupsMenu.pack()
                }))
            })
//            tileGroupsMenu.addSeparator()
        }
        tileGroupsMenu.pack()
    }
    
    
    private fun createMain() {
        val labelUpload = Label("Upload", module.skin, UI.LABEL_BN_LIGHT_BLACK)
        mainMenu.addItem(labelUpload, popupWindow = uploadMenu)
//        mainMenu.pack()
    }
    
    private fun testClick() {
        println("CLICK")
    }
    
    private fun testEnter() {
        println("ENTER")
    }
    
    private fun testFill(pane: ScrollPaneContent) {
        repeat(21) {
            val label = Label("Content_#${it}", module.skin, UI.LABEL_BN_LIGHT_BLACK)
            pane.addItem(label)
        }
    }
    
}