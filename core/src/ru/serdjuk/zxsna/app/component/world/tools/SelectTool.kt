package ru.serdjuk.zxsna.app.component.world.tools

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ru.serdjuk.zxsna.app.component.ui.windows.layerMenu
import ru.serdjuk.zxsna.app.system.*
import ru.serdjuk.zxsna.app.utils.*
import kotlin.math.abs

@ExperimentalUnsignedTypes
class SelectTool : ITools() {

    private val positionInfo = TextField("000\n000", module.skin).also {
        it.pack()
        it.touchable = Touchable.disabled
        it.isVisible = false
    }


    override var toolName = ToolName.SELECT
    override fun undo(data: UR?) {

    }

    init {
        module.stage.addActor(positionInfo)
//        createMenu()
    }


    override fun update(delta: Float) {

        // FIXME запретить выполнение если открыто меню слоя

        if (toolName == ToolName.used) {
            select()
//            showMenu()
        }

        // remove selection
        if (keyHold(Input.Keys.CONTROL_LEFT) && keyOnce(Input.Keys.D)) {
            sensor.selectRectangle.set(0f, 0f, 0f, 0f)
            positionInfo.isVisible = false
            layerMenu.displayLayerMenuMethod()
        }
    }

    private fun showMenu() {
        if (buttonOnce(1)) {
            val x = if (sensor.selectRectangle.width < 0) (sensor.selectRectangle.x + sensor.selectRectangle.width).toInt() else sensor.selectRectangle.x.toInt()
            val y = if (sensor.selectRectangle.height < 0) (sensor.selectRectangle.y + sensor.selectRectangle.height).toInt() else sensor.selectRectangle.y.toInt()
            val width = abs(sensor.selectRectangle.width).toInt()
            val height = abs(sensor.selectRectangle.height).toInt()
            if (Rectangle.tmp.set(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat()).contains(sensor.worldMouse)) {
//                createMenu()
            }
        }

    }


    override fun draw(batch: SpriteBatch) {

    }

    private var firstX = 0f
    private var firstY = 0f

    override fun draw(shape: ShapeRenderer) {

    }


    private fun select() {
        if (buttonOnce(0)) {
            firstX = sensor.worldMouse.x.toInt().toFloat()
            firstY = sensor.worldMouse.y.toInt().toFloat()
            sensor.selectRectangle.set(firstX, firstY, 0f, 0f)
            positionInfo.setPosition(sensor.screenMouse.x, sensor.screenMouse.y)
            positionInfo.isVisible = true
        }
        if (buttonHold(0)) {
            cursor.setSystem(Cursor.SystemCursor.Crosshair)
            positionInfo.setPosition(sensor.screenMouse.x, sensor.screenMouse.y)
            positionInfo.text = "".intern()
            positionInfo.messageText = "W:${abs(sensor.selectRectangle.width).toInt()}\nH:${abs(sensor.selectRectangle.height).toInt()}"
            val n = 1

            if (sensor.worldMouse.x > sensor.selectRectangle.x) {
                sensor.selectRectangle.width = sensor.worldMouse.x.toInt().toFloat() - sensor.selectRectangle.x + n
                sensor.selectRectangle.x = firstX
            } else {
                sensor.selectRectangle.width = sensor.worldMouse.x.toInt().toFloat() - sensor.selectRectangle.x
                sensor.selectRectangle.x = firstX + n
            }
            if (sensor.worldMouse.y > sensor.selectRectangle.y) {
                sensor.selectRectangle.height = sensor.worldMouse.y.toInt().toFloat() - sensor.selectRectangle.y + n
                sensor.selectRectangle.y = firstY
            } else {
                sensor.selectRectangle.height = sensor.worldMouse.y.toInt().toFloat() - sensor.selectRectangle.y
                sensor.selectRectangle.y = firstY + n
            }
        }
    }

    //---------------------------------------------------------------------------

    //--------------------SAVE-------------------------------
    private fun saveMenu(): MenuItem {
        val save = MenuItem("Save")
        val saveSubMenu = PopupMenu()
        val saveSelectionSubMenu = PopupMenu()

        val saveSelection = MenuItem("selection as...")
        val saveLayer = MenuItem("layer")

        saveSubMenu.addItem(saveSelection)
        saveSubMenu.addItem(saveLayer)

        val saveSelection8bpp = MenuItem("8bpp")
        val saveSelection4bpp = MenuItem("4bpp")
        saveSelectionSubMenu.addItem(saveSelection8bpp)
        saveSelectionSubMenu.addItem(saveSelection4bpp)

        saveSelection4bpp.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

                // transform selection rectangle to true shape values.
                val x = if (sensor.selectRectangle.width < 0) (sensor.selectRectangle.x + sensor.selectRectangle.width).toInt() else sensor.selectRectangle.x.toInt()
                val y = if (sensor.selectRectangle.height < 0) (sensor.selectRectangle.y + sensor.selectRectangle.height).toInt() else sensor.selectRectangle.y.toInt()
                val width = abs(sensor.selectRectangle.width).toInt()
                val height = abs(sensor.selectRectangle.height).toInt()


                val rectInt = RectInt(x, y, width, height)
//                res.layers[sensor.spinner].pixmap.get4bpp(rectInt)
//                val b = res.layers[sensor.spinner].pixmap.get4bpp(rectInt)
//                file.save4bppSprites(b, sensor.spinner)
//                b?.forEach { println(it) }
            }
        })

//        saveSelection8bpp.addListener(selectionListener(SaveSelectionNext8bpp))
//        saveSelection4bpp.addListener(selectionListener(SaveSelectionNext4bpp))

        saveSelection.subMenu = saveSelectionSubMenu
        save.subMenu = saveSubMenu

        return save
    }

//    private fun selectionListener(save: ISave) = object : ChangeListener() {
//        override fun changed(event: ChangeEvent?, actor: Actor?) {
//            FileMenu.process(FileChooser.Mode.SAVE, FileChooser.SelectionMode.FILES, object : SingleFileChooserListener() {
//                override fun selected(file: FileHandle?) {
//                    if (file == null) error("Wrong file !!!")
//                    file.writeBytes(save.execute(), false)
//                }
//            })
//        }
//    }

    //--------------------LOAD-------------------------------


    private fun loadMenu(): MenuItem {
        val load = MenuItem("Load")
        val loadSubMenu = PopupMenu()
        val loadSprites = MenuItem("sprites")
        val loadSpritesSubMenu = PopupMenu()
        val loadTiles = MenuItem("tiles")
        val loadImage = MenuItem("images")
        load.subMenu = loadSubMenu
        loadSprites.subMenu = loadSpritesSubMenu
        loadSubMenu.addItem(loadTiles)
        loadSubMenu.addItem(loadSprites)
        loadSubMenu.addItem(loadImage)

        val loadSprites8bpp = MenuItem("8bpp")
        val loadSprites4bpp = MenuItem("4bpp")
        loadSpritesSubMenu.addItem(loadSprites8bpp)
        loadSpritesSubMenu.addItem(loadSprites4bpp)


//        loadSprites8bpp.addListener(loadListener(DrawType.SPRITE8BPP))
//        loadSprites4bpp.addListener(loadListener(DrawType.SPRITE4BPP))


        return load
    }

//    private fun loadListener(type: DrawType) = object : ChangeListener() {
//        override fun changed(event: ChangeEvent?, actor: Actor?) {
//            FileMenu.process(FileChooser.Mode.OPEN, FileChooser.SelectionMode.FILES, object : FileChooserAdapter() {
//                override fun selected(files: Array<FileHandle>?) {
//                    if (files != null) {
//                        val layer = SELManager.newLayer(SELManager.getLayer().pixmap.width
//                                ?: 1024, SELManager.getLayer().pixmap.height ?: 1024)
//                        SpriteEditor.layersWindow.addLine(layer)
//                        files.forEach {
//                            layer.drawWithoutUpgradeTexture(it.readBytes(), type)
//                        }
//                        layer.texture.draw(layer.pixmap, 0, 0)
//                    }
//                    super.selected(files)
//                }
//            })
//        }
//    }


}