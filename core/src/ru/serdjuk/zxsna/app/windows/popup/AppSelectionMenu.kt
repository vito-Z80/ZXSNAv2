package ru.serdjuk.zxsna.app.windows.popup

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.util.ActorUtils
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.layers.AppLayersSystem
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor
import ru.serdjuk.zxsna.app.system.system
import ru.serdjuk.zxsna.app.windows.CancelDialog
import ru.serdjuk.zxsna.app.windows.TextEntryWindow
import ru.serdjuk.zxsna.app.windows.sheets.FinalSheets

@ExperimentalUnsignedTypes
class AppSelectionMenu : PopupWindow() {

    private val mainMenu = newWindow()
    private val uploadMenu = newWindow()
    private val tileGroupsMenu = newWindow()
//    private val spriteGroupsMenu = newWindow()

    private val uploadTiles = Label("Tiles", module.skin, UI.LABEL_BN_LIGHT_BLACK)
    private val uploadSprites = Label("Sprites", module.skin, UI.LABEL_BN_LIGHT_BLACK)


    init {
        createMain()
        fillUploadMenu()
        fillTileGroups()
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
        uploadMenu.addItem(uploadTiles, popupWindow = tileGroupsMenu)
        uploadMenu.addItem(uploadSprites)
//        uploadMenu.pack()
        uploadMenu.setPosition(300f, 300f)
    }

    private fun fillTileGroups() {

        val groupsIsEmpty = Label("Add new group", module.skin)
        val sheets = module.stage.root.findActor<FinalSheets>(FinalSheets::class.java.name)
        if (sheets != null) {
            tileGroupsMenu.addItem(groupsIsEmpty, actionTouch = fun() {
                val text = "new_group_${sheets.groupList.size}"
                module.stage.addActor(TextEntryWindow("Add new group", text, fun(str: String) {
                    val layerSystem = system.get<AppLayersSystem>()
                    val regions = layerSystem?.getLayerSlices()
                    if (regions != null) {
                        val item = Label(str, module.skin, UI.LABEL_JB_LIGHT_BLACK)
                        tileGroupsMenu.addItem(item)
                        val size = regions.size.toString()
                        sheets::addNewGroup.invoke("$str ($size)", regions)
                    } else {
                        CancelDialog("The selection must be a multiple of 8 or 16.")
                    }
                    tileGroupsMenu.pack()
                }))
            })
        }
        tileGroupsMenu.pack()
    }


    private fun createMain() {
        val labelUpload = Label("Upload", module.skin, UI.LABEL_BN_LIGHT_BLACK)
        mainMenu.addItem(labelUpload, popupWindow = uploadMenu)
//        mainMenu.pack()
    }

//    private fun testFill(pane: ScrollPaneContent) {
//        repeat(21) {
//            val label = Label("Content_#${it}", module.skin, UI.LABEL_BN_LIGHT_BLACK)
//            pane.addItem(label)
//        }
//    }

}