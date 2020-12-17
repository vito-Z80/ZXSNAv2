package ru.serdjuk.zxsna.app.palette

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.layers.AppLayersSystem
import ru.serdjuk.zxsna.app.resources.hexColor512
import ru.serdjuk.zxsna.app.system.*
import ru.serdjuk.zxsna.app.utils.*

@ExperimentalUnsignedTypes
class AppPaletteWindow : WindowOutScreen("Palette") {


    private val pixmap = res.pixmap
    private val texture = res.texture
    private val atlasUtils = module.atlasUtils

    // кол-во иконок в меню над пользовательской палитрой
    private var userPaletteColspan = 7
    private val previousMousePosition = Vector2()
    private val amount = Vector2()
    private var isTextureUpdate = false

    /**
     * индексы цветов смещенных палитр
     */
    private val offsetIndexes = IntArray(16) { it * 16 }


    // границы ячеек палитры относительно актера.
    private val actorBounds = Array<Rectangle>(512) {
        val x = space + (it and 15) * cellSize + space * (it and 15)
        val y = space + (it and 496) + space * ((it and 496) / 16)
        Rectangle(x.toFloat(), y.toFloat(), cellSize.toFloat(), cellSize.toFloat())
    }

    private val mainPalettePosition = atlasUtils.getFreePosition(tableWidth(), tableHeight512()).also {
        pixmap.setColor(Color.SLATE)
        pixmap.fillRectangle(it.x.toInt(), it.y.toInt(), tableWidth(), tableHeight512())
    }

    private val mainTable = Array<PaletteUnit>(hexColor512.size) {
        val x = mainPalettePosition.x.toInt() + (it and 15) * cellSize + space * (it and 15)
        val y = mainPalettePosition.y.toInt() + (it and 496) + space * ((it and 496) / 16)
        val region = TextureRegion(texture, x + 1, y + 1, cellSize, cellSize)
        pixmap.setColor(hexColor512[it].int)
        pixmap.fillRectangle(region.regionX, region.regionY, region.regionWidth, region.regionHeight)
        PaletteUnit(hexColor512[it].int, region)
    }
    private val mainPaletteRegion = TextureRegion(texture, mainPalettePosition.x.toInt(), mainPalettePosition.y.toInt(), tableWidth(), tableHeight512()).also {
        module.skin.add(UI.MAIN_PALETTE, it, TextureRegion::class.java)
        res.atlas.addRegion(UI.MAIN_PALETTE, it)
    }


    // top table
    val userColorInfo = Label("#NNNNNN", module.skin)


    // main palette show/hide button
    private val mpdb = SpriteDrawable(Sprite(res.atlas.findRegion(UI.DOUBLE_ARROW_LEFT)))
    private val mainPaletteDisplayButton = Image(mpdb)

    private var isMainPaletteVisible = true

    // notUsed для создания таблицы управления палитрами выше самих палитр
    private val notUsed = topTable()

    private val settingsGroup = Table()

    val userPaletteImage = Image(UserPalette.storage[0].region)
    private val mainPaletteImage = Image(mainPaletteRegion)

    private val userPaletteCell = add(userPaletteImage).colspan(userPaletteColspan).padRight(2f).top()
    private val mainPaletteCell = add(mainPaletteImage).padLeft(2f).top()

    private val userPaletteBounds = Rectangle()
    private val mainPaletteBounds = Rectangle()
    private val tmpBounds = Rectangle()

    // перемещаемая пользователем ячейка цвета
    private val movableCellPosition = atlasUtils.getFreePosition(cellSize, cellSize)
    val movableCellRegion = TextureRegion(texture, movableCellPosition.x.toInt(), movableCellPosition.y.toInt(), cellSize, cellSize).also {
        pixmap.setColor(Color.BLACK)
        pixmap.drawRectangle(movableCellPosition.x.toInt(), movableCellPosition.y.toInt(), cellSize, cellSize)
        module.skin.add(UI.LEVITATION_CELL, it, TextureRegion::class.java)
        res.atlas.addRegion(UI.LEVITATION_CELL, it)
        texture.draw(pixmap, 0, 0)
    }
    val userMovableImage = Image(movableCellRegion)

    // ячейки подсвечивающая местоположение курсора палитр
    private val highlightCellPosition = atlasUtils.getFreePosition(cellSize + 2, cellSize + 2)
    private val highlightCellRegion = TextureRegion(texture, highlightCellPosition.x.toInt(), highlightCellPosition.y.toInt(), cellSize + 2, cellSize + 2).also {
        pixmap.setColor(Color.WHITE)
        pixmap.drawRectangle(highlightCellPosition.x.toInt(), highlightCellPosition.y.toInt(), cellSize + 2, cellSize + 2)
        module.skin.add(UI.HIGHLIGHT_CELL, it, TextureRegion::class.java)
        res.atlas.addRegion(UI.HIGHLIGHT_CELL, it)
        texture.draw(pixmap, 0, 0)
    }
    private val mainHighlightCellImage = Image(highlightCellRegion)
    private val mainHighlightPosition = Vector2()

    val userHighlightCellImage = Image(highlightCellRegion)
    private val userHighlightPosition = Vector2()

    // подсветка выбранного пользователем цвета
    private val whiteCellPosition = atlasUtils.getFreePosition(PaletteUnit.size, PaletteUnit.size)
    private val whiteCellRegion = TextureRegion(texture, whiteCellPosition.x.toInt(), whiteCellPosition.y.toInt(), PaletteUnit.size, PaletteUnit.size).also {
        pixmap.setColor(Color.WHITE)
        pixmap.fillRectangle(whiteCellPosition.x.toInt(), whiteCellPosition.y.toInt(), PaletteUnit.size, PaletteUnit.size)
        module.skin.add(UI.WHITE_CELL, it, TextureRegion::class.java)
        res.atlas.addRegion(UI.WHITE_CELL, it)
        texture.draw(pixmap, 0, 0)
    }
    val highlightUserColorImage = Image(whiteCellRegion)

    // отображение выбранного пользователем цвета
    private val userColorPosition = atlasUtils.getFreePosition(PaletteUnit.size, PaletteUnit.size)
    private val userColorRegion = TextureRegion(texture, userColorPosition.x.toInt(), userColorPosition.y.toInt(), PaletteUnit.size, PaletteUnit.size).also {
        pixmap.setColor(Color.WHITE)
        pixmap.fillRectangle(userColorPosition.x.toInt(), userColorPosition.y.toInt(), PaletteUnit.size, PaletteUnit.size)
        module.skin.add(UI.USER_COLOR, it, TextureRegion::class.java)
        res.atlas.addRegion(UI.USER_COLOR, it)
        texture.draw(pixmap, 0, 0)
    }
    val userColorImage = Image(userColorRegion).also { it.color.a = 0f }


    // overlay panel
    private val tableWidth = tableWidth()
    private val tableHeight = tableHeight512()
    private val overlayPosition = atlasUtils.getFreePosition(tableWidth, tableHeight)
    private val tableX = overlayPosition.x.toInt()
    private val tableY = overlayPosition.y.toInt()
    private val height15Cells = cellSize * 15 + space * 15
    private val overlayPanelRegion = TextureRegion(texture, tableX, tableY + height15Cells, tableWidth, tableHeight256()).also {
        module.skin.add(UI.OVERLAY_PANEL, it, TextureRegion::class.java)
        res.atlas.addRegion(UI.OVERLAY_PANEL, it)
        pixmap.setColor(0f, 0f, 0f, 0.4f)
        pixmap.fillRectangle(tableX, tableY, tableWidth, height15Cells)
        pixmap.fillRectangle(tableX, tableY + height15Cells + cellSize + space * 2, tableWidth, height15Cells)
        pixmap.setColor(1f, 1f, 1f, 0.0f)
        pixmap.fillRectangle(tableX, tableY + height15Cells, tableWidth, 18)
    }
    private val overlayPanelRegionY = overlayPanelRegion.regionY
    private val overlayPanelRegionHeight = overlayPanelRegion.regionHeight
    private val overlayPanelImage = Image(overlayPanelRegion)


    private var time = 0f

    companion object {

        val transparentIntColor = hexColor512[455].int
        val space = 1       // расстояние между ячейками палитры (думаю не давать менять юзеру)
        var cellSize = 16   // физический размер ячейки палитры
        var belong = 0      // принадлежность/ID палитры (4bpp/9bpp)
    }

    /**
     * Принадлежность палитры к:
     */
    object Belongingness {
        const val SPRITES_4BPP = 0
        const val TILES_4BPP = 1
        const val SPRITES_9BPP = 2
        const val TILES_9BPP = 3
    }


    init {
//        color = Color.GRAY  // FIXME сделать фон темнее, а не затемнить все окно

        row()
        titleTable.add(userColorImage).fill().width(128f)
        titleTable.pack()

        pack()

        userPaletteBounds.set(
                userPaletteImage.x,
                userPaletteImage.y,
                userPaletteImage.width,
                userPaletteImage.height
        )
        mainPaletteBounds.set(
                mainPaletteImage.x,
                mainPaletteImage.y,
                mainPaletteImage.width,
                mainPaletteImage.height
        )

        // userMovableImage должна быть после pack() так как она изначально не видна в окне
        userMovableImage.isVisible = false
        userMovableImage.touchable = Touchable.disabled
        addActor(userMovableImage)

        // то-же что и для userMovableImage
        mainHighlightCellImage.isVisible = false
        mainHighlightCellImage.touchable = Touchable.disabled
        addActor(mainHighlightCellImage)

        userHighlightCellImage.isVisible = false
        userHighlightCellImage.touchable = Touchable.disabled
        addActor(userHighlightCellImage)

        highlightUserColorImage.isVisible = false
        highlightUserColorImage.touchable = Touchable.disabled
        highlightUserColorImage.setOrigin(Align.center)
        addActor(highlightUserColorImage)

        addActor(overlayPanelImage)

        // настройки и управление палитрами (то что ниже пользовательской палитры. Отображаются если отображается основная палитра)
        createSettings()
        addActor(settingsGroup)
        settingsGroup.setPosition(userMovableImage.x, userMovableImage.height - 7)


        res.textureUpdate()

        system.set<AppLayersSystem>(true)
    }

    private fun createSettings() {
        val slider = Slider(0f, 15f, 1f, false, module.skin)
        slider.value = 3f
        settingsGroup.add(slider).fillX().row()
        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                println(slider.value)
            }
        })


        val loadUserPalette = TextButton("Load palette", module.skin)
        loadUserPalette.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val data = file.loadData()
                if (data != null) {
                    UserPalette.storage[belong].convertPaletteFrom(data)
//                    convertPaletteFrom(data)
                    // обновить индексы смещения в исходные позиции (каждое смещение с 'левой' ячейки  (0,16,32,48....))
                    UserPalette.storage[belong].clearOffsets()
                    res.textureUpdate()
                } else {
                    throw Exception("PALETTE NOT LOADED")
                }
            }
        })
        // 7 это размер сторон 9patch окна
        settingsGroup.padLeft(7f)
        settingsGroup.add(loadUserPalette).width(userPaletteImage.width).fillX()
        settingsGroup.row()


        val saveUserPalette = TextButton("Save palette", module.skin)
        settingsGroup.add(saveUserPalette).fillX().align(Align.right)
        settingsGroup.row()

        saveUserPalette.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
//                file.saveData(convertPaletteTo())
                file.saveData(UserPalette.storage[belong].convertPaletteTo())
            }
        })


        settingsGroup.width = userMovableImage.width
        settingsGroup.pack()
    }


    private fun topTable() {
        val offsetDownButton = ImageButton(module.skin.getDrawable(UI.ARROW_DOWN))
        val offsetUpButton = ImageButton(module.skin.getDrawable(UI.ARROW_DOWN))
        // FIXME добавить в текстуру перевернутую стрелку, либо добавить в атлас перевернутый регион
        (offsetUpButton.image.drawable as SpriteDrawable).sprite.flip(false, true)

        val offsetLabel = object : Label("0", module.skin) {
            var preOffset = UserPalette.storage[belong].offset
            override fun act(delta: Float) {
                offsetDownButton.isVisible = overlayPanelImage.isVisible
                offsetUpButton.isVisible = overlayPanelImage.isVisible


                if (overlayPanelImage.isVisible) {
                    if (preOffset != UserPalette.storage[belong].offset) {
                        preOffset = UserPalette.storage[belong].offset
                        setText(preOffset)
                    }
                } else {
                    setText(UserPalette.storage[belong].offsetIndexes[UserPalette.storage[belong].offset])
                }


                super.act(delta)
            }
        }

        val minMaxButton = TextButton("min", module.skin)

        minMaxButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                minMaxButton.setText("max")
                // TODO уменьшить окно палитры до минимума работая с одним смещением 16 цветами
            }
        })


        offsetDownButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val userPalette = UserPalette.storage[belong]
                userPalette.offset = MathUtils.clamp(--userPalette.offset, 0, 15)
                userPalette.setUserColor(this@AppPaletteWindow)

            }
        })
        offsetUpButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val userPalette = UserPalette.storage[belong]
                userPalette.offset = MathUtils.clamp(++userPalette.offset, 0, 15)
                userPalette.setUserColor(this@AppPaletteWindow)
            }
        })



        mainPaletteDisplayButton.addListener(object : ClickListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                isMainPaletteVisible = !isMainPaletteVisible
                if (isMainPaletteVisible) {
                    mainPaletteImage.isVisible = true
                    mainPaletteCell.setActor(mainPaletteImage)
                    settingsGroup.isVisible = true
                    this@AppPaletteWindow.y -= mainPaletteCell.prefHeight / 2
                } else {
                    this@AppPaletteWindow.y += mainPaletteCell.prefHeight / 2
                    mainPaletteImage.isVisible = false
                    mainPaletteCell.clearActor()
                    settingsGroup.isVisible = false
                }
                mpdb.sprite.flip(true, false)
                pack()
                roundPosition()
                reboundUserPalette()
                super.touchUp(event, x, y, pointer, button)
            }
        })


        // иконка смены палитры
        val paletteChanger = TextButton(UserPalette.storage[belong].getShortName(), module.skin)
        paletteChanger.addListener(object : ChangeListener() {
            val popupWindow = PopupMenu()

            init {
                UserPalette.storage.forEachIndexed { id, palette ->
                    val paletteName = palette.paletteName.toString()
                    val paletteItem = MenuItem(paletteName)
                    paletteItem.addListener(itemListener(id, paletteItem, palette).invoke())
                    popupWindow.addItem(paletteItem)
                }
                popupWindow.pack()
            }

            // листенер итемов
            private fun itemListener(belong: Int, item: MenuItem, palette: UserPalette): () -> ChangeListener = {
                object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        AppPaletteWindow.belong = belong
                        paletteChanger.setText(UserPalette.storage[Companion.belong].getShortName())
                        UserPalette.storage[Companion.belong].setUserColor(this@AppPaletteWindow)
                        // visual change palette
                        (userPaletteImage.drawable as TextureRegionDrawable).region.setRegion(
                                res.atlas.findRegion(palette.paletteName.toString())
                        )
                    }
                }
            }

            // листенер иконки палитр
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                popupWindow.showMenu(module.stage, sensor.screenMouseYUp.x, sensor.screenMouseYUp.y)
            }
        })



        add(userColorInfo).width(56f)
        add(offsetUpButton).right().width(16f)
        add(offsetDownButton).left().width(16f)
        add(offsetLabel).left().width(16f)
        add(paletteChanger).width(48f)
        add(minMaxButton).fillX()
        add(mainPaletteDisplayButton).right()

        row()
    }

    // обязательный апгрейд границ пользовательской палитры после show\hide основной палитры
    private fun reboundUserPalette() {
        userPaletteBounds.set(
                userPaletteImage.x,
                userPaletteImage.y,
                userPaletteImage.width,
                userPaletteImage.height
        )
    }

    override fun act(delta: Float) {
        if (!isVisible) return
        time += delta * 4
        isTextureUpdate = false
        val x = Gdx.input.x.toFloat()
        val y = Gdx.input.y.toFloat()

        val userPalette = UserPalette.storage[belong]

        // deltaX & deltaY
        amount.set(calculateAmount(x, y, previousMousePosition.x, previousMousePosition.y))
        tmp2Position.set(x, y)
        screenToLocalCoordinates(tmp2Position)
        // выполняем действия с основной палитрой
        mainPaletteActions(tmp2Position, delta)
        // таскаем передвигаемую ячейку за курсором
        if (userMovableImage.isVisible && buttonHold(0)) {
            userMovableImage.x -= amount.x
            userMovableImage.y += amount.y
            mainHighlightCellImage.isVisible = false
        } else {
            // устанавливаем передвигаемую ячейку в пользовательскую палитру, или не устанавливаем
            if (userPalette.setAcceptedColor(this)) {
                isTextureUpdate = true
            }
            userMovableImage.isVisible = false
        }
        tmp2Position.set(x, y)
        screenToLocalCoordinates(tmp2Position)
        // выполняем действия с пользовательской палитрой
        userPalette.action(this, delta)
        userPalette.setUserColorMark(this)

        transformPanelOverlay()

        if (isTextureUpdate) {
            res.textureUpdate()
        }

        // сохраним позицию мыши для следующего кадра
        previousMousePosition.set(x, y)
        super.act(delta)
    }

    private fun coloredHighlightCell(delta: Float, image: Image) {
        if (image.isVisible) {
            val c = if (time.toInt() % 2 == 0) 1f else 0f
            image.setColor(c, c, c, 1f)
        }
    }

    /**
     * показ подсветки ячейки
     * @param inPalette cursor in palette or not
     * @param cellImage highlight image
     * @param paletteBounds palette bounds
     * @param delta delta time
     */
    private fun showHighlightCell(inPalette: Boolean, cellImage: Image, paletteBounds: Rectangle, delta: Float): Int {

        if (inPalette) {
            coloredHighlightCell(delta, cellImage)
            val cellId = getCellId(paletteBounds)
            if (cellId > -1) {
                cellImage.setPosition(
                        actorBounds[cellId].x + paletteBounds.x - 1,
                        actorBounds[cellId].y + paletteBounds.y - 1
                )
                cellImage.isVisible = true
                cellImage.toFront()
                return cellId
            }
            return -1
        } else {
            cellImage.setPosition(-100f, -100f)
            cellImage.isVisible = false
            return -1
        }
    }

    private fun determineSelectedColor() {

//        val cellId = offsetIndexes[offset]
        val cellId = UserPalette.storage[belong].geCellId()


        val pixelColor = UserPalette.storage[belong].cells[cellId].intColor
        highlightUserColorImage.color.set(pixelColor)
        highlightUserColorImage.setPosition(
                actorBounds[cellId].x + userPaletteImage.x,
                actorBounds[255 - cellId].y + userPaletteImage.y
        )
        highlightUserColorImage.toFront()
        highlightUserColorImage.isVisible = true
        highlightUserColorImage.setOrigin(Align.center)
        highlightUserColorImage.setScale(1f)

        userColorInfo.setText("#${hexColor512.find { it.int == pixelColor }?.hex?.dropLast(2)}")
        userColorImage.color.set(pixelColor)
        UserPalette.userIntColor = pixelColor


        // scaled
        val c = (time / 2) % 1
        highlightUserColorImage.setScale(1f + c)
        highlightUserColorImage.color.a = 1f - c

    }

    /**
     * получить ID ячейки по смещению или -1
     */
    private fun getCellIdByOffset(x: Float, y: Float): Int {
        for (cell in UserPalette.storage[belong].offset * 16..UserPalette.storage[belong].offset * 16 + 15) {
            if (actorBounds[cell].contains(x, y)) {
                return cell
            }
        }
        return -1
    }

    private fun getCellId(bounds: Rectangle) = actorBounds.indexOfFirst {
        // получить регион нажатой ячейки из основной палитры
        Rectangle.tmp.set(it).addPosition(bounds.x, bounds.y)
        Rectangle.tmp.contains(tmp2Position)
    }

    /**
     * Различные действия с основной палитрой.
     * @param position mouse position
     * @param delta delta time
     */
    private fun mainPaletteActions(position: Vector2, delta: Float) {
        // определить нажатие на ячейку цвета основной палитры
        val inMainPalette = mainPaletteBounds.contains(position)
        val cellId = showHighlightCell(inMainPalette, mainHighlightCellImage, mainPaletteBounds, delta)

        if (buttonOnce(0) && inMainPalette && cellId > -1 && mainPaletteImage.isVisible) {
            userMovableImage.isVisible = true
            userMovableImage.toFront()

            // границы нажатой ячейки относительно окна (this)
            tmpBounds.set(actorBounds[cellId])
            // установить координаты передвигаемой ячейки цвета
            userMovableImage.setPosition(
                    actorBounds[cellId].x + mainPaletteImage.x,
                    actorBounds[cellId].y + mainPaletteImage.y
            )
            // Y нужно перевернуть (откуда копировать битмап)
            val y = (mainPaletteRegion.regionY - tmpBounds.y + mainPaletteRegion.regionHeight + 1 - tmpBounds.height - space).toInt()
            // скопировать полученый регион ячейки основноной палитры в переносимую ячейку
            res.pixmap.drawPixmap(
                    res.pixmap,
                    movableCellRegion.regionX, movableCellRegion.regionY,
                    (mainPaletteRegion.regionX + tmpBounds.x).toInt(),
                    y,
                    tmpBounds.width.toInt(),
                    tmpBounds.height.toInt()
            )
            // сообщить о необходимочти перерисовки текстуры
            isTextureUpdate = true
        }
    }

    /**
     * OVERLAY PANE
     */
    private fun transformPanelOverlay() {
        overlayPanelImage.isVisible = UserPalette.storage[belong].showOverlayPanel
        overlayPanelImage.setPosition(userPaletteImage.x, userPaletteImage.y)
        overlayPanelRegion.regionY = overlayPanelRegionY - UserPalette.storage[belong].offset * 17
        overlayPanelRegion.regionHeight = overlayPanelRegionHeight
    }


    private fun tableWidth() = 16 * cellSize + space * cellSize + space
    private fun tableHeight256() = tableWidth()
    private fun tableHeight512() = tableWidth() * 2 - 1


}

