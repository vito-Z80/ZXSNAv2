package ru.serdjuk.zxsna.app.tools.actors

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ru.serdjuk.zxsna.app.palette.AppPaletteWindow
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor
import ru.serdjuk.zxsna.app.utils.WindowOutScreen
import ru.serdjuk.zxsna.app.windows.LayerMenuInfoWindow
import ru.serdjuk.zxsna.app.windows.UserSheets
import kotlin.reflect.KFunction1

@ExperimentalUnsignedTypes
class SelectorMenuWindow : PopupMenu() {
    private val selection = Rectangle() // sensor.selectRectangle
    private val separator = MenuItem("_______________").also {
        it.isDisabled = true
        it.label.setAlignment(Align.center)
    }
    
    //--------------------
    private val transformItem = MenuItem("Transform")
    
    //--------------------
    private val saveMenu = MenuItem("Save as...")
    private val saveTilesItem = MenuItem("tile`s")
    private val saveSpritesItem = MenuItem("sprite`s")
    
    //--------------------
    private val uploadMenu = MenuItem("Upload to...")
    private val uploadItemSpriteSheet = MenuItem("sprite sheet")
    private val uploadItemTileSheet = MenuItem("tile sheet")
    
    private val test = MenuItem("test")
    
    
    //--------------------
    private val recolorPixels = MenuItem("Recolor pixels")
    
    
    init {
        name = this::class.java.name
        installLayerMenu()
    }
    
    fun showLayerMenu() {
//        sensor.transformSelectionRectangle()
        displayLayerMenuMethod()
//        pack()
        showMenu(module.stage, sensor.screenMouseYUp.x, sensor.screenMouseYUp.y)
    }
    
    private fun displayLayerMenuMethod() {
        val outOfSelection = !selection.contains(sensor.worldMouse)
        val isSelectionExists = selection.width > 0 && selection.height > 0
        val isTileSelection =
            selection.width % 8f == 0f && selection.height % 8f == 0f && AppPaletteWindow.belong == AppPaletteWindow.Belongingness.TILES_4BPP
        val isSpriteSelection =
            selection.width % 16f == 0f && selection.height % 16f == 0f && AppPaletteWindow.belong == AppPaletteWindow.Belongingness.SPRITES_4BPP
        
        
        recolorPixels.isDisabled = !outOfSelection || isSelectionExists
        transformItem.isDisabled = outOfSelection
        //--------------
        saveMenu.isDisabled = outOfSelection
        saveTilesItem.isDisabled = outOfSelection || !isTileSelection
        saveSpritesItem.isDisabled = outOfSelection || !isSpriteSelection
        //--------------
        uploadMenu.isDisabled = outOfSelection
        uploadItemTileSheet.isDisabled = outOfSelection || !isTileSelection
        uploadItemSpriteSheet.isDisabled = outOfSelection || !isSpriteSelection
        //--------------
    }
    
    private fun installLayerMenu() {
        val spriteSize = 16
        val tileSize = 8
        
        addItem(recolorPixels)
        addItem(transformItem)
        addItem(separator)
        addItem(saveMenu)
        addItem(uploadMenu)
        
        testPopup()
        
        
        saveMenu.subMenu = PopupMenu()
        saveMenu.subMenu.addItem(saveTilesItem)
        saveMenu.subMenu.addItem(saveSpritesItem)
        
        uploadMenu.subMenu = PopupMenu()
        uploadMenu.subMenu.addItem(uploadItemTileSheet)
        uploadMenu.subMenu.addItem(uploadItemSpriteSheet)
        uploadMenu.subMenu.addItem(test)
        
        // Listeners
        saveSpritesItem.addListener(createListener(spriteSize))
        saveTilesItem.addListener(createListener(tileSize))
//        uploadItemSpriteSheet.addListener(asd(layers[sensor.paletteOffset]::createForSheets, spriteSize))
//        uploadItemTileSheet.addListener(asd(layers[sensor.paletteOffset]::createForSheets, tileSize))
//        uploadItemTileSheet.addListener(uploadListener())
        uploadItemSpriteSheet.addListener(uploadListener(this::displayLayerMenuMethod))
    }
    
    private fun uploadListener(function: () -> Unit) = object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
//            function.invoke()
            val target = module.stage.root.findActor<WindowOutScreen>(UserSheets::class.java.name)
            if (target != null) {
                // TODO select group for upload or create group if absent
                target as UserSheets
//                target.upload(sensor.selectRectangle)
            } else {
                // TODO instance UserSheets window
                // TODO create new group for upload
                // TODO call target.upload(sensor.selectRectangle)
            }
        }
    }
    
    fun asd(kFunction1: KFunction1<Int, Unit>, resourceSize: Int) = object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            kFunction1.invoke(resourceSize)
        }
    }
    
    private fun createListener(resourceSize: Int) = object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            val infoMenu = LayerMenuInfoWindow(
                when (resourceSize) {
                    8 -> "Tile`s"
                    else -> "Sprite`s"  // resourceSize == 16
                }
            )
            module.stage.addActor(infoMenu)
//            layers[sensor.paletteOffset].collect4bpp(resourceSize, infoMenu)
        }
    }
    
    
    private fun testPopup() {
        test.subMenu = PopupMenu()
        val scroll = ScrollPane(test.subMenu)
        scroll.setFlickScroll(true)
        scroll.setScrollingDisabled(true,false)
        repeat(17) {
            val l = Label("test_$it", module.skin)
            test.subMenu.add(l).row()
            
        }
        
        
    }
    
    
}



