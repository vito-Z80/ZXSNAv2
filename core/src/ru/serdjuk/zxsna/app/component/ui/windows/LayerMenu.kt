package ru.serdjuk.zxsna.app.component.ui.windows

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.system.sensor
import kotlin.reflect.KFunction1

@ExperimentalUnsignedTypes
class LayerMenu {
    private val layers = res.layers
    private val selection = sensor.selectRectangle
    private val layerPopupMenu = PopupMenu()
    private val separator = MenuItem("_______________").also {
        it.isDisabled = true
        it.label.setAlignment(Align.center)
    }

    //--------------------
    private val transformItem = MenuItem("Transform")

    //--------------------
    private val saveMenu = MenuItem("Save as...")
    private val saveTilesItem = MenuItem("tiles")
    private val saveSpritesItem = MenuItem("sprites")

    //--------------------
    private val uploadMenu = MenuItem("Upload to...")
    private val uploadItemSpriteSheet = MenuItem("sprite sheet")
    private val uploadItemTileSheet = MenuItem("tile sheet")


    //--------------------
    private val recolorPixels = MenuItem("Recolor pixels")


    init {
        installLayerMenu()
    }

    fun showLayerMenu() {
        sensor.transformSelectionRectangle()
        displayLayerMenuMethod()
        layerPopupMenu.pack()
        layerPopupMenu.showMenu(module.stage, sensor.screenMouse.x, sensor.screenMouse.y)
    }

    fun displayLayerMenuMethod() {
        val outOfSelection = !selection.contains(sensor.worldMouse)
        val isSelectionExists = selection.width > 0 && selection.height > 0
        val isTileSelection = selection.width % 8f == 0f && selection.height % 8f == 0f
        val isSpriteSelection = selection.width % 16f == 0f && selection.height % 16f == 0f


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

        layerPopupMenu.addItem(recolorPixels)
        layerPopupMenu.addItem(transformItem)
        layerPopupMenu.addItem(separator)
        layerPopupMenu.addItem(saveMenu)
        layerPopupMenu.addItem(uploadMenu)

        saveMenu.subMenu = PopupMenu()
        saveMenu.subMenu.addItem(saveTilesItem)
        saveMenu.subMenu.addItem(saveSpritesItem)

        uploadMenu.subMenu = PopupMenu()
        uploadMenu.subMenu.addItem(uploadItemTileSheet)
        uploadMenu.subMenu.addItem(uploadItemSpriteSheet)

        // Listeners
        saveSpritesItem.addListener(createListener(spriteSize))
        saveTilesItem.addListener(createListener(tileSize))
        uploadItemSpriteSheet.addListener(asd(layers[sensor.paletteOffset]::createForSheets, spriteSize))
        uploadItemTileSheet.addListener(asd(layers[sensor.paletteOffset]::createForSheets, tileSize))
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
            layers[sensor.paletteOffset].collect4bpp(resourceSize, infoMenu)
        }
    }


}

@ExperimentalUnsignedTypes
val layerMenu = LayerMenu()


