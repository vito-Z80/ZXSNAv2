package ru.serdjuk.zxsna.app.component.ui.windows

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.*

@ExperimentalUnsignedTypes
class LayerMenuInfoWindow(private val workName: String) : Window(" Process $workName", module.skin) {

    val progressBar = ProgressBarDisplay(this)

    var rectangleNumbersInfo = Label("0", module.skin)
    var emptyRectanglesInfo = Label("0", module.skin)
    var imageRectanglesInfo = Label("0", module.skin)
    var isPaletteOffsetNumber = false
    var fileExtension = ""

    // дополнение к имени сохраняемого файла(ов)
    private val additionToFileName = StringBuilder()
    private val progressBarRegion = TextureRegion(module.skin.getRegion(UI.PROGRESS_BAR))
    private val progressBarSprite = Sprite(progressBarRegion)
//    private val progressBarImage = progressBar()
    private val confirmButton = TextButton("Confirm", module.skin)
    private val cancelButton = TextButton("Cancel", module.skin)
    private var processCompleted = false

    init {

        add(createTables()).pad(16f, 8f, 16f, 8f).row()
        add("_________________").center().expandX().row()
        add(progressBar).padTop(8f).left().row()
        isModal = true
        pack()
        setPosition(module.stage.width / 2 - prefWidth / 2, module.stage.height / 2 - prefHeight / 2)
    }

    private fun createTables(): Table {
        val table = Table(module.skin)
        val rectangleNumbers = "$workName in selection"
        val emptyRectangles = "Empty $workName"
        val imageRectangles = "Colored $workName"
        table.add(rectangleNumbers).left().width(200f)
        rectangleNumbersInfo.setAlignment(Align.right)
        table.add(rectangleNumbersInfo).right().width(32f).fillX()
        table.row()
        table.add(emptyRectangles).left()
        table.add(emptyRectanglesInfo).right().row()
        table.add(imageRectangles).left()
        table.add(imageRectanglesInfo).right().row()
        saveWithEmpty(table)
        saveWithOffsetNumber(table)
        aboutPaletteInfo(table)

        table.pack()
        return table
    }

    private fun saveWithOffsetNumber(table: Table) {
        val addOffsetNumber = CheckBox("add palette offset\nnumber with filename", module.skin)
        addOffsetNumber.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                isPaletteOffsetNumber = addOffsetNumber.isChecked
            }
        })
        table.add(addOffsetNumber).left().row()
    }


    private fun aboutPaletteInfo(table: Table) {

    }

    // чекбоксы с выбором сохранения пустых спрайтов/тайлов
    private fun saveWithEmpty(table: Table) {
        val group = ButtonGroup<CheckBox>()
        val with1EmptyBlock = CheckBox("with 1 empty", module.skin)
        val withAllEmptyBlock = CheckBox("with all empty", module.skin)
        val withoutEmptyBlock = CheckBox("without empty", module.skin)
        group.add(withoutEmptyBlock, with1EmptyBlock, withAllEmptyBlock)
        group.uncheckAll()
        group.setMinCheckCount(1)
        group.setMaxCheckCount(1)
        withoutEmptyBlock.isChecked = true

        table.add().row()
        table.add("Save options:").left()
        table.add(withoutEmptyBlock).left().row()
        table.add("")
        table.add(with1EmptyBlock).expandX().left().row()
        table.add("")
        table.add(withAllEmptyBlock).expandX().left().row()

    }

    fun createBottomButtons(): Table {
        cancelButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                addAction(Actions.removeActor())
            }
        })
        confirmButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                addAction(Actions.removeActor())
                if (isPaletteOffsetNumber) {
                    additionToFileName.append("_")
                    additionToFileName.append(sensor.paletteOffset)
                }

                // FIXME определять какое расширение файлу добавлять, так-же дать юзеру изменять расширение файла
                additionToFileName.append(".spr")

                file.saveData(res.layers[sensor.paletteOffset].collectResource!!, additionToFileName.toString())
                res.layers[sensor.paletteOffset].collectResource = null
            }
        })
        val table = Table()
        table.add(cancelButton).padRight(16f).padBottom(8f)
        table.add(confirmButton).padLeft(16f).padBottom(8f)
        return table
    }


//    private fun progressBar(): Image {
//        progressBarSprite.setSize(500f, progressBarRegion.regionHeight.toFloat())
//        progressBarSprite.regionWidth = 1
//        println(progressBarSprite.regionX)
//        val image = Image(progressBarSprite)
//
//        image.pack()
//        image.width = 0f
//        return image
//    }

//
//    var timer = 0f
//    override fun act(delta: Float) {
//
//
//        // remove manic miner and create shader process bar
////        processBar(delta)
//        super.act(delta)
//    }
//
//
//    private fun processBar(delta: Float) {
//        if (!processCompleted) {
//            timer += delta
//            if (timer >= 1.125f) {
//                timer = 0f
//                val amount = if (progressBarSprite.regionX and Int.MAX_VALUE - 63 != 0) 1f - 16f / 2048f else 1f - 8f / 2048f
//                progressBarSprite.scroll(amount, 0f)
//                println(amount)
//            }
//            progressBarImage.width = sensor.progressBarIndicator.toFloat()
//            progressBarSprite.regionWidth = progressBarImage.width.toInt()
//            if (sensor.progressBarIndicator > prefWidth) {
//                sensor.progressBarIndicator = 0
//            }
//            if (progressBarImage.width >= prefWidth) {
//                progressBarImage.width = prefWidth
//                processCompleted = true
//                removeActor(progressBarImage)
//                add(createBottomButtons()).row()
//            }
//        }
//    }
//
//
//    private fun createInfoMessage() {
//
//    }

}