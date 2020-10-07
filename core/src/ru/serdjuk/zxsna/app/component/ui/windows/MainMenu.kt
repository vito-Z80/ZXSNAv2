package ru.serdjuk.zxsna.app.component.ui.windows

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.ImageSystem
import ru.serdjuk.zxsna.app.system.file
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.system

@ExperimentalUnsignedTypes
class MainMenu : Image() {

    private val buttonGroup = ButtonGroup<TextButton>()
    private val table = Table()
    private val fileButton = TextButton(" File", module.skin)
    private val windowsButton = TextButton(" Windows", module.skin)
    private val imageSystemButton = TextButton(" Image system", module.skin)
    private val spriteEditorButton = TextButton(" Sprite editor", module.skin)
    private val levelEditorButton = TextButton(" Level editor", module.skin)

    private val popupFile = PopupMenu()
    private val sprite = SpriteDrawable(Sprite(module.skin.getRegion(UI.GRADIENT_GG)))

    init {
        sprite.sprite.setFlip(false, true)
        fileButton.pack()
        imageSystemButton.pack()


        buttonGroup.add(imageSystemButton,  spriteEditorButton, levelEditorButton)
        buttonGroup.uncheckAll()

        setDrawableBounds()
        drawable = sprite
        pack()

        module.stage.addActor(this)


        table.pad(1f)
        table.add(fileButton).fillX().expandX().pad(1f)
        table.add(windowsButton).fillX().expandX().pad(1f)
        table.add(imageSystemButton).fillX().expandX().pad(1f)
        table.add(spriteEditorButton).fillX().expandX().pad(1f)
        table.add(levelEditorButton).fillX().expandX().pad(1f).fillX()
        table.pack()
        module.stage.addActor(table)

        createPopups()
        menuListeners()
    }

    private fun setDrawableBounds() {
        setPosition(0f, module.stage.height - 50f)
        sprite.setMinSize(module.stage.width, fileButton.height * 2)
        table.setPosition(x, y - 1)
        zIndex = Int.MAX_VALUE - 3
        table.zIndex = Int.MAX_VALUE - 2
//        pack()
    }

    override fun act(delta: Float) {
        setDrawableBounds()
        super.act(delta)
    }

    private fun createPopups() {
        val loadImage = MenuItem("Load image`s")
        loadImage.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
//                imageSystemButton.isChecked = true
                val imageSystem = system.display<ImageSystem>(true)?:return
                imageSystem.setSystem()
                file.loadImagesSequence()
            }
        })

        popupFile.addItem(loadImage)
    }

    private fun menuListeners() {
        fileButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                fileButton.isChecked = false
                popupFile.zIndex = Int.MAX_VALUE - 1
                popupFile.showMenu(module.stage, x, y)
            }
        })

//        imageSystemButton.addListener(object : ChangeListener() {
//            override fun changed(event: ChangeEvent?, actor: Actor?) {
//                imageSystemButton.isChecked = !imageSystemButton.isChecked
//                system.display<ImageSystem>(imageSystemButton.isChecked)
//            }
//        })
    }

}