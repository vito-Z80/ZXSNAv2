package ru.serdjuk.zxsna.app.component.ui.windows

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.component.ui.debug.AppDebug
import ru.serdjuk.zxsna.app.component.ui.palette.AppPaletteWindow
import ru.serdjuk.zxsna.app.utils.WindowOutScreen
import ru.serdjuk.zxsna.app.component.ui.tools.AppToolsWindow
import ru.serdjuk.zxsna.app.system.ImageSystem
import ru.serdjuk.zxsna.app.system.file
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.pack.packer
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
    private val popupWindow = PopupMenu()
    private val sprite = SpriteDrawable(Sprite(module.skin.getRegion(UI.GRADIENT_GG)))

    init {
        sprite.sprite.setFlip(false, true)
        fileButton.pack()
        imageSystemButton.pack()


        buttonGroup.add(imageSystemButton, spriteEditorButton, levelEditorButton)
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

        createFilePopup()
        createWindowsPopUp()
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

    private fun createFilePopup() {
        val loadImage = MenuItem("Load image`s")
        loadImage.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
//                imageSystemButton.isChecked = true
//                val imageSystem = system.display<ImageSystem>(true) ?: return
                val imageSystem = system.set<ImageSystem>(true) ?: return
                file.loadImagesSequence()
            }
        })
        popupFile.addItem(loadImage)

        val saveProject = MenuItem("Save project")
        saveProject.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                lazy {
//                    file.saveProject(projectCompression.compress(), additionToFileName = "_Project", extension = ".snp")
//                    file.saveProject(packCollector.collect(), additionToFileName = "_Project", extension = ".snp")
                    file.saveProject(packer.compress(), additionToFileName = "_Project", extension = ".snp")
                }.value
//                file.saveProject(project.compressProject(), additionToFileName = "_Project", extension = ".snp")
            }
        })

        popupFile.addItem(saveProject)

        val loadProject = MenuItem("Load project")
        loadProject.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

                lazy {

                    packer.decompress(file.loadProject())
//                    packCollector.parse(file.loadProject())
                }.value
//                projectCompression.uncompress(file.loadProject())

//                project.unCompressProject(file.loadProject())
            }
        })

        popupFile.addItem(loadProject)

        val saveZXN = MenuItem("Save")
        saveZXN.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                TODO("Save zx spectrum next files: sprites,tiles,palettes,maps,info")
            }
        })
        popupFile.addItem(saveZXN)


    }

    private fun menuListeners() {
        fileButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                fileButton.isChecked = false
                popupFile.zIndex = Int.MAX_VALUE - 1
                popupFile.showMenu(module.stage, x, y)
            }
        })

        windowsButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                windowsButton.isChecked = false
                popupWindow.zIndex = Int.MAX_VALUE - 1
                popupWindow.showMenu(module.stage, windowsButton.x, y)
            }
        })

//        imageSystemButton.addListener(object : ChangeListener() {
//            override fun changed(event: ChangeEvent?, actor: Actor?) {
//                imageSystemButton.isChecked = !imageSystemButton.isChecked
//                system.display<ImageSystem>(imageSystemButton.isChecked)
//            }
//        })
    }


    private fun createWindowsPopUp() {
        val palette = MenuItem("Palette")
        palette.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                windowsCollector<AppPaletteWindow>()
            }
        })
        popupWindow.addItem(palette)

        val debugWindow = MenuItem("Debug window")
        debugWindow.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                windowsCollector<AppDebug>()
            }
        })
        popupWindow.addItem(debugWindow)

        val toolsWindow = MenuItem("Tools")
        toolsWindow.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                windowsCollector<AppToolsWindow>()
            }
        })

        popupWindow.addItem(toolsWindow)

    }

    /**
     * Find a <T> window if it exists, or new instance of <T> window.
     * after, window.toCenter()
     */
    inline fun <reified T> windowsCollector() {
        module.stage.root.findActor<WindowOutScreen>(T::class.java.name)?.toCenter()
                ?: (T::class.java.newInstance() as WindowOutScreen).toCenter()
    }


}