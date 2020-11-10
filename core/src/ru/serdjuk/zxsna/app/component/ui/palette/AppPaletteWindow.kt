package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ru.serdjuk.zxsna.app.Packable
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.component.world.layers.AppLayersSystem
import ru.serdjuk.zxsna.app.resources.hexColor512
import ru.serdjuk.zxsna.app.system.*
import ru.serdjuk.zxsna.app.utils.*

@ExperimentalUnsignedTypes
class AppPaletteWindow : WindowOutScreen("Palette"), Packable {


    private val pixmap = res.pixmap
    private val texture = res.texture
    private val atlasUtils = module.atlasUtils

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

    /**
     * имена палитр для добавления в атлас и в скин
     */
    private val paletteNames = arrayOf(
            UI.SPRITE_PALETTE_4BPP, UI.TILE_PALETTE_4BPP, UI.SPRITE_PALETTE_9BPP, UI.TILE_PALETTE_9BPP
    )

    /**
     * Хранилище ЧЕТЫРЕХ палитр 2x4bpp `n` 2x9bpp
     */

//    private val userPaletteStorage = Array<UserPalette>(4) { userPaletteId ->
//        // получить место под палитру в текстуре атласа и координаты региона в атласе
//        val position = atlasUtils.getFreePosition(tableWidth(), tableHeight256())
//        pixmap.setColor(Color.SLATE)
//        pixmap.fillRectangle(position.x.toInt(), position.y.toInt(), tableWidth(), tableHeight256()
//        )
//        // заполнить палитру ячейками, создать коллекцию ячеек данной палитры PaletteUnit
//        val cells = Array<PaletteUnit>(256) {
//            val x = position.x.toInt() + (it and 15) * PaletteUnit.size + space * (it and 15)
//            val y = position.y.toInt() + (it and 240) + space * ((it and 240) / 16)
////            val color = hexColor512[455].int
//            val color = hexColor512[MathUtils.random(userPaletteId * 8, userPaletteId * 16)].int
//            val region = TextureRegion(texture, x + 1, y + 1, PaletteUnit.size, PaletteUnit.size)
//            val cell = PaletteUnit(color, region)
//            cell.upgrade(color)
//            cell
//        }
//        // добавить регион палитры а атлас и в скин
//        val region = TextureRegion(texture, position.x.toInt(), position.y.toInt(), tableWidth(), tableHeight256()).also {
//            module.skin.add(paletteNames[userPaletteId], it, TextureRegion::class.java)
//            res.atlas.addRegion(paletteNames[userPaletteId], it)
//        }
//        val palette = UserPalette( userPaletteId)
//        palette
//    }


//    private val userPalettePosition = atlasUtils.getFreePosition(tableWidth(), tableHeight256()).also {
//        pixmap.setColor(Color.SLATE)
//        pixmap.fillRectangle(it.x.toInt(), it.y.toInt(), tableWidth(), tableHeight256())
//    }
//
//    // user palette table 4bpp from 9bpp (512 colors)
//    private val userPaletteUnits = Array<PaletteUnit>(256) {
//        val x = userPalettePosition.x.toInt() + (it and 15) * PaletteUnit.size + space * (it and 15)
//        val y = userPalettePosition.y.toInt() + (it and 240) + space * ((it and 240) / 16)
//        val color = hexColor512[455].int
//        val region = TextureRegion(texture, x + 1, y + 1, PaletteUnit.size, PaletteUnit.size)
//        val cell = PaletteUnit(color, region)
//        cell.upgrade(color)
//        cell
//    }
//    private val userPaletteRegion = TextureRegion(texture, userPalettePosition.x.toInt(), userPalettePosition.y.toInt(), tableWidth(), tableHeight256()).also {
//        module.skin.add(UI.SPRITE_PALETTE_4BPP, it, TextureRegion::class.java)
//        res.atlas.addRegion(UI.SPRITE_PALETTE_4BPP, it)
//    }

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
    private val userColorInfo = Label("#NNNNNN", module.skin)


    // main palette show/hide button
    private val mpdb = SpriteDrawable(Sprite(res.atlas.findRegion(UI.DOUBLE_ARROW_LEFT)))
    private val mainPaletteDisplayButton = Image(mpdb)

    private var isMainPaletteVisible = true

    // notUsed для создания таблицы управления палитрами выше самих палитр
    private val notUsed = topTable()

    private val settingsGroup = Table()

    // TODO эта куета еще не проинициализировалась а мы ее тащим из UserPalette (йобаный круговорот)
    private val userPaletteImage = lazy {
        UserPalette.storage.add(UserPalette4bppSprites(0))
        UserPalette.storage[0]

    }.value
    private val mainPaletteImage = Image(mainPaletteRegion)

    private val userPaletteCell = add(userPaletteImage).colspan(userPaletteColspan).padRight(2f).top()
    private val mainPaletteCell = add(mainPaletteImage).padLeft(2f).top()

    private val userPaletteBounds = Rectangle()
    private val mainPaletteBounds = Rectangle()
    private val tmpBounds = Rectangle()

    // перемещаемая пользователем ячейка цвета
    private val movableCellPosition = atlasUtils.getFreePosition(cellSize, cellSize)
    private val movableCellRegion = TextureRegion(texture, movableCellPosition.x.toInt(), movableCellPosition.y.toInt(), cellSize, cellSize).also {
        pixmap.setColor(Color.BLACK)
        pixmap.drawRectangle(movableCellPosition.x.toInt(), movableCellPosition.y.toInt(), cellSize, cellSize)
        module.skin.add(UI.LEVITATION_CELL, it, TextureRegion::class.java)
        res.atlas.addRegion(UI.LEVITATION_CELL, it)
        texture.draw(pixmap, 0, 0)
    }
    private val userMovableImage = Image(movableCellRegion)

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

    private val userHighlightCellImage = Image(highlightCellRegion)
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
    private val highlightUserColorImage = Image(whiteCellRegion)

    // отображение выбранного пользователем цвета
    private val userColorPosition = atlasUtils.getFreePosition(PaletteUnit.size, PaletteUnit.size)
    private val userColorRegion = TextureRegion(texture, userColorPosition.x.toInt(), userColorPosition.y.toInt(), PaletteUnit.size, PaletteUnit.size).also {
        pixmap.setColor(Color.WHITE)
        pixmap.fillRectangle(userColorPosition.x.toInt(), userColorPosition.y.toInt(), PaletteUnit.size, PaletteUnit.size)
        module.skin.add(UI.USER_COLOR, it, TextureRegion::class.java)
        res.atlas.addRegion(UI.USER_COLOR, it)
        texture.draw(pixmap, 0, 0)
    }
    private val userColorImage = Image(userColorRegion).also { it.color.a = 0f }


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
        var userIntColor: Int? = null   // цвет выбранный юзером для рисования
        val space = 1       // расстояние между ячейками палитры (думаю не давать менять юзеру)
        var offset = 0      // смещение 16-цветной палитры. Так-же этим смещением обозначается полотно (слой пользователя для рисования)
        var cellSize = 16   // физический размер ячейки палитры
        var belong = 0      // принадлежность палитры (4bpp/9bpp)
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
        // user color into title
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
                    convertPaletteFrom(data)
                    // обновить индексы смещения в исходные позиции (каждое смещение с нулевой ячейки  (0,16,32,48....))
                    offsetIndexes.onEachIndexed { id, _ -> offsetIndexes[id] = id * 16 }
                    offset = 0  // сбросить смещение на 0
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
                file.saveData(convertPaletteTo())
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

        val offsetLabel = object : Label("00", module.skin) {
            var preOffset = AppPaletteWindow.offset
            override fun act(delta: Float) {
                if (preOffset != AppPaletteWindow.offset) {
                    preOffset = AppPaletteWindow.offset
                    setText(preOffset)
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
                offset = MathUtils.clamp(--offset, 0, 15)
            }
        })
        offsetUpButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                offset = MathUtils.clamp(++offset, 0, 15)
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
        val paletteChanger = TextButton("S4B", module.skin)
        paletteChanger.addListener(object : ChangeListener() {
            val popupWindow = PopupMenu()
            val item1 = MenuItem("Sprites 4 bpp")
            val item2 = MenuItem("Tiles 4 bpp")
            val item3 = MenuItem("Sprites 9 bpp")
            val item4 = MenuItem("Tiles 9 bpp")

            init {
                item1.addListener(itemListener(Belongingness.SPRITES_4BPP, item1).invoke())
                item2.addListener(itemListener(Belongingness.TILES_4BPP, item2).invoke())
                item3.addListener(itemListener(Belongingness.SPRITES_9BPP, item3).invoke())
                item4.addListener(itemListener(Belongingness.TILES_9BPP, item4).invoke())
                popupWindow.addItem(item1)
                popupWindow.addItem(item2)
                popupWindow.addItem(item3)
                popupWindow.addItem(item4)
                popupWindow.pack()
            }

            // листенер итемов
            private fun itemListener(belong: Int, item: MenuItem): () -> ChangeListener = {
                object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        AppPaletteWindow.belong = belong
                        val palName = item.text.split(" ")
                        val newName = StringBuilder()
                        repeat(palName.size) { loop ->
                            newName.append(palName[loop].first().toUpperCase())
                        }
                        paletteChanger.setText(newName.toString())
                        // visual change palette
                        (userPaletteImage.drawable as TextureRegionDrawable).region.setRegion(
//                                userPaletteStorage[belong].region   // вот так не отображает первую палитру [id=0] я хуй пойми почему
                                res.atlas.findRegion(paletteNames[belong])
                        )
                    }
                }
            }


            // листенер иконки палитр
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                popupWindow.showMenu(module.stage, sensor.screenMouse.x, sensor.screenMouse.y)
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
            // FIXME придумать что делать с mainHighlightCellImage при перетаскивании перемещаемой ячйки
            mainHighlightCellImage.isVisible = false
        } else {
            // устанавливаем передвигаемую ячейку в пользовательскую палитру, или не устанавливаем
            setMovableCellToUserPalette()   // портит tmp2Position, далее следует восстановить
            userMovableImage.isVisible = false  // after setMovableCellToUserPalette()
        }
        tmp2Position.set(x, y)
        screenToLocalCoordinates(tmp2Position)
        // выполняем действия с пользовательской палитрой
        userPaletteActions(tmp2Position, delta)

        transformPanelOverlay()
        determineSelectedColor()

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

        val cellId = offsetIndexes[offset]
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
        userIntColor = pixelColor


        // scaled
        val c = (time / 2) % 1
        highlightUserColorImage.setScale(1f + c)
        highlightUserColorImage.color.a = 1f - c

    }


    /**
     * Действия с пользовательской палитрой
     * @param position mouse position
     * @param delta delta time
     */
    private fun userPaletteActions(position: Vector2, delta: Float) {
        val inUserPalette = userPaletteBounds.contains(position)
//        val cellId = showHighlightCell(inUserPalette, userHighlightCellImage, userPaletteBounds, delta)

        tmp2Position.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        userPaletteImage.screenToLocalCoordinates(tmp2Position)
        // flip Y
        tmp2Position.y = userPaletteImage.height - tmp2Position.y
//        val cellId = PaletteUtils.actorBounds.indexOfFirst { it.contains(tmp2Position) }
        val cellId = getCellIdByOffset(tmp2Position.x, tmp2Position.y)

        // hide highlight cell
        userHighlightCellImage.isVisible = false
        userHighlightCellImage.setPosition(-1000f, -1000f)

        if (inUserPalette) {

            if (cellId > -1 && cellId < 256) {
                if (buttonOnce(0)) {
                    offsetIndexes[offset] = cellId
//                    determineSelectedColor()
                }
                // show highlight cell
                userHighlightCellImage.isVisible = true
                userHighlightCellImage.setPosition(
                        actorBounds[cellId].x + userPaletteImage.x - 1,
                        actorBounds[255 - cellId].y + userPaletteImage.y - 1
                )
            }
        }


        coloredHighlightCell(delta, userHighlightCellImage)

    }

//    private fun manipulateOffsetIndex(besave: Int): Int {
//        offsetIndex[offset] = besave
//        return
//    }

    /**
     * получить ID ячейки по смещению или -1
     */
    private fun getCellIdByOffset(x: Float, y: Float): Int {
        for (cell in offset * 16..offset * 16 + 15) {
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
     * устанавливаем перетаскиваемую ячейку цвета в пользовательскую палитру
     * если перетаскиваемая ячейка попадает в одну из ячеек пользовательской палитры
     */
    private fun setMovableCellToUserPalette() {
        if (userMovableImage.isVisible) {
            Rectangle.tmp.set(
                    userPaletteImage.x,
                    userPaletteImage.y,
                    userPaletteImage.width,
                    userPaletteImage.height
            )
            tmp2Position.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            screenToLocalCoordinates(tmp2Position)
            if (Rectangle.tmp.contains(tmp2Position)) {
                tmp2Position.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
                userPaletteImage.screenToLocalCoordinates(tmp2Position)
                // flip Y
                tmp2Position.y = userPaletteImage.height - tmp2Position.y
//                val cellId = PaletteUtils.actorBounds.indexOfFirst { it.contains(tmp2Position) }
                val cellId = getCellIdByOffset(tmp2Position.x, tmp2Position.y)
                if (cellId > -1 && cellId < UserPalette.storage[belong].cells.size) {
                    val regionPositionX = UserPalette.storage[belong].cells[cellId].unitRegion.regionX
                    val regionPositionY = UserPalette.storage[belong].cells[cellId].unitRegion.regionY
                    res.pixmap.drawPixmap(
                            res.pixmap,
                            regionPositionX, regionPositionY,
                            movableCellRegion.regionX,
                            movableCellRegion.regionY,
                            movableCellRegion.regionWidth,
                            movableCellRegion.regionHeight
                    )
                    val color = res.pixmap.getPixel(regionPositionX + 1, regionPositionY + 1)
                    UserPalette.storage[belong].cells[cellId].intColor = color
                    isTextureUpdate = true
                }
            }
        }
    }

    /**
     * OVERLAY PANE
     */
    private fun transformPanelOverlay() {
        overlayPanelImage.setPosition(userPaletteImage.x, userPaletteImage.y)
        overlayPanelRegion.regionY = overlayPanelRegionY - offset * 17
        overlayPanelRegion.regionHeight = overlayPanelRegionHeight
    }


    /**
     * CONVERTER
     */

    fun convertPaletteTo(): ByteArray {
        // for save palette
//        val paletteBytes = ArrayList<Byte>()
//
//        val line = ByteArray(32)
//        repeat(16) { row ->
//            repeat(16) { cell ->
//                val color = userPaletteUnits[row * 16 + cell].get9bit()
//                val bit9 = color and 256 shr 8
//                val byte = color and 255
//                line[cell * 2] = byte.toByte()
//                line[cell * 2 + 1] = bit9.toByte()
//            }
//            paletteBytes.addAll(0, line.toList())
//        }


        val result = com.badlogic.gdx.utils.ByteArray()
        val ba = com.badlogic.gdx.utils.ByteArray()
        Array<String>(16) {
            val str = StringBuilder()
            ba.clear()
            repeat(16) { index ->
                val c = convertTo9bpp(UserPalette.storage[belong].cells[it * 16 + index].colorId())
                val bit9 = c and 256 shr 8
                val byte = c and 255
                // TODO add hex variation
                str.append("$byte, $bit9, ")
                ba.add(byte.toByte(), bit9.toByte())
            }
            result.addAll(ba)
            str.toString()
        }
        return result.toArray()
    }

    // индекс цвета делится на 2 и если есть остаток то включить бит 9
    private fun convertTo9bpp(colorId: Int) = (colorId / 2) or (colorId % 2 shl 8)

    private fun convertPaletteFrom(byteArray: ByteArray) {
        // for load palette
        // если бит 9 включен то умножаем первый байт на 2 + 1
        UserPalette.storage[belong].cells.forEachIndexed { id, cell ->
            val colorId = if (byteArray[id * 2 + 1].toUByte().toInt() == 0) {
                byteArray[id * 2].toUByte().toInt() * 2
            } else {
                byteArray[id * 2].toUByte().toInt() * 2 + 1
            }
            cell.upgrade(hexColor512[colorId].int)
        }
    }

    /**
     * etc...
     */

    private fun tableWidth() = 16 * cellSize + space * cellSize + space
    private fun tableHeight256() = tableWidth()
    private fun tableHeight512() = tableWidth() * 2 - 1

    /**
     * FOR COMPRESS
     *
     *
     * склееваем в один массив следущее:
     * offset
     * offsetIndexed
     * color ID`s from general palette
     *
     * палитру (PaletteUnit.intColor), текущее смещение (offset), массив смещений (offsetIndexed)
     * TODO добавить позицию окна палитры относительно размера окна приложения в %
     * TODO видимость основной палитры и общая видимость окна палитры
     */
    override fun collectData(): ByteArray {
        this::class.java
        Gdx.app.log(this::class.java.name, "Packed.")
        val bytes = ArrayList<Byte>()
        // offset byte (1 byte)
        bytes.add(offset.toByte())
        // offset indexes (16 bytes)
        bytes.addAll(offsetIndexes.map { (it and 15).toByte() })

        // palette colors id (512 bytes)
        bytes.addAll(convertPaletteTo().toTypedArray())
        return bytes.toByteArray()
    }

    /**
     * распихиваем из массива в том же порядке что и пихали
     */
    override fun parseData(bytes: ByteArray) {
        Gdx.app.log(this::class.java.name, "Unpacked.")
        // offset byte (1 byte)
        offset = bytes[0].toInt()
        // offset indexes (16 bytes)
        for (id in 0..15) {
            // +1 offset from start data
            offsetIndexes[id] = bytes[id + 1].toInt() + (id * 16)
        }
        // palette colors id (512 bytes)
        convertPaletteFrom(bytes.copyOfRange(17, 17 + 512))
        res.textureUpdate() // FIXME remove from here if parse with coroutine
    }


}
