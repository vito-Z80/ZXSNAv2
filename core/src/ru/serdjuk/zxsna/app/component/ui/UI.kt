package ru.serdjuk.zxsna.app.component.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kotcrab.vis.ui.Sizes
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.Tooltip
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.kotcrab.vis.ui.widget.file.FileChooserStyle
import net.dermetfan.gdx.scenes.scene2d.ui.ListFileChooser
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.system.sensor
import ru.serdjuk.zxsna.app.component.ui.palette.PaletteManager
import ru.serdjuk.zxsna.app.component.ui.windows.AppDebug

@ExperimentalUnsignedTypes
class UI {

    companion object {
        const val DEFAULT = "default"
        const val DEFAULT_PANE9 = "defaultPane9"
        const val DRAGGED_PANE = "titlePane9"
        const val SETTINGS_ICON = "settingsIcon"
        const val CHECK_ON = "checkOnIcon"
        const val CHECK_OFF = "checkOffIcon"
        const val ARROW_16x8 = "arrow16x8"
        const val DOUBLE_ARROW_LEFT = "doubleArrow"
        const val DOUBLE_ARROW_RIGHT = "doubleArrow"
        const val SQUAD_ICON = "squadIcon"
        const val ARROW_DOWN = "arrowDown"

        const val FONT_16 = "font_16_java"

        const val CLOSE_WINDOW = "close-window"
        const val WINDOW = "window"
        const val VIS_LABEL = "menuitem-shortcut"


        const val SELECTOR = "selector"
        const val BORDER = "border"
        const val GRADIENT_BG = "gradientBG"
        const val GRADIENT_GG = "gradientGG"

        // tools
        const val TOOL_PEN = "penTool"

        const val TOOL_FILL = "fillTool"
        const val TOOL_SELECT = "selectTool"

        // colors
        const val BLACK = "colorBlack"
        const val WHITE = "colorWhite"
        const val GRAY = "colorGray"
        const val LIGHT_GRAY = "colorLightGray"
        const val DARK_GRAY = "colorDarkGray"


        const val LABEL_D = "labelD"


        const val USER_PALETTE = "userPalette"
        const val MAIN_PALETTE = "mainPalette"
        const val TRANSPARENT_OVERLAY = "transparentOverlay"
        const val LEVITATION_CELL = "levitationCell"

        const val EMPTY_8X8 = "empty8x8"

        const val PALETTE_MENU = "paletteMenu"

        const val BUTTON_ON = "buttonOn9p"
        const val BUTTON_OFF = "buttonOff9p"

        const val PROGRESS_BAR = "manicminer"
    }

    private val atlas = res.atlas
    private val stage = module.stage
    private val skin = module.skin

//    private val manager = PaletteManager()


    fun install() {
        val sizes = Sizes()
        sizes.scaleFactor = 1f
        sizes.spacingBottom = 8f
        sizes.spacingRight = 6f
        sizes.buttonBarSpacing = 10f
        sizes.menuItemIconSize = 22f
        sizes.borderSize = 1f
        sizes.spinnerButtonHeight = 12f
        sizes.spinnerFieldSize = 50f
        sizes.fileChooserViewModeBigIconsSize = 200f
        sizes.fileChooserViewModeMediumIconsSize = 128f
        sizes.fileChooserViewModeSmallIconsSize = 64f
        sizes.fileChooserViewModeListWidthSize = 150f
        skin.add(DEFAULT, sizes, Sizes::class.java)

        createFonts()
        createRegions()
        buttons()
        toolTips()
        text()
        fileChooser()

        // ui added here


    }

    fun update(delta: Float) {
        stage.act(delta)
        sensor.mouseWheel = 0
        stage.draw()
    }

    private fun createFonts() {
        val fontN = BitmapFont(Gdx.files.internal("font_16_java.fnt"), atlas.findRegion(FONT_16))
        skin.add(FONT_16, fontN, BitmapFont::class.java)
        skin.add(DEFAULT, fontN, BitmapFont::class.java)
    }

    private fun createRegions() {
        skin.add(BLACK, Color.BLACK, Color::class.java)
        skin.add(WHITE, Color.WHITE, Color::class.java)
        skin.add(LIGHT_GRAY, Color.LIGHT_GRAY, Color::class.java)
        skin.add(GRAY, Color.GRAY, Color::class.java)
        skin.add(DARK_GRAY, Color.DARK_GRAY, Color::class.java)

        skin.add(SETTINGS_ICON, atlas.findRegion(SETTINGS_ICON), TextureRegion::class.java)
        skin.add(ARROW_16x8, atlas.findRegion(ARROW_16x8), TextureRegion::class.java)
        skin.add(SQUAD_ICON, atlas.findRegion(SQUAD_ICON), TextureRegion::class.java)
        skin.add(CHECK_ON, atlas.findRegion(CHECK_ON), TextureRegion::class.java)
        skin.add(CHECK_OFF, atlas.findRegion(CHECK_OFF), TextureRegion::class.java)
        skin.add(DOUBLE_ARROW_LEFT, atlas.findRegion(DOUBLE_ARROW_LEFT), TextureRegion::class.java)
        skin.add(ARROW_DOWN, atlas.findRegion(ARROW_DOWN), TextureRegion::class.java)
        skin.add(SELECTOR, atlas.findRegion(SELECTOR), TextureRegion::class.java)
        skin.add(GRADIENT_BG, atlas.findRegion(GRADIENT_BG), TextureRegion::class.java)
        skin.add(GRADIENT_GG, atlas.findRegion(GRADIENT_GG), TextureRegion::class.java)

        // regions 1x1
        skin.add(BLACK, atlas.findRegion(BLACK), TextureRegion::class.java)
        skin.add(WHITE, atlas.findRegion(WHITE), TextureRegion::class.java)
        skin.add(GRAY, atlas.findRegion(GRAY), TextureRegion::class.java)
        skin.add(LIGHT_GRAY, atlas.findRegion(LIGHT_GRAY), TextureRegion::class.java)
        skin.add(DARK_GRAY, atlas.findRegion(DARK_GRAY), TextureRegion::class.java)


        skin.add(PROGRESS_BAR, atlas.findRegion(PROGRESS_BAR), TextureRegion::class.java)

        // drawables
        skin.add(BLACK, atlas.findRegion(BLACK), TextureRegionDrawable::class.java)
        skin.add(WHITE, atlas.findRegion(WHITE), TextureRegionDrawable::class.java)
        skin.add(GRAY, atlas.findRegion(GRAY), TextureRegionDrawable::class.java)
        skin.add(LIGHT_GRAY, atlas.findRegion(LIGHT_GRAY), TextureRegionDrawable::class.java)
        skin.add(DARK_GRAY, atlas.findRegion(DARK_GRAY), TextureRegionDrawable::class.java)

        // tools
        skin.add(TOOL_PEN, atlas.findRegion(TOOL_PEN), TextureRegion::class.java)
        skin.add(TOOL_FILL, atlas.findRegion(TOOL_FILL), TextureRegion::class.java)
        skin.add(TOOL_SELECT, atlas.findRegion(TOOL_SELECT), TextureRegion::class.java)

        val buttonOn = atlas.findRegion(BUTTON_ON)
        val buttonOnSplits = buttonOn.splits
        skin.add(BUTTON_ON, NinePatch(buttonOn, buttonOnSplits[0], buttonOnSplits[1], buttonOnSplits[2], buttonOnSplits[3]), NinePatch::class.java)

        val buttonOff = atlas.findRegion(BUTTON_OFF)
        val buttonOffSplits = buttonOff.splits
        skin.add(BUTTON_OFF, NinePatch(buttonOff, buttonOffSplits[0], buttonOffSplits[1], buttonOffSplits[2], buttonOffSplits[3]), NinePatch::class.java)


        val region = atlas.findRegion(DEFAULT_PANE9)
        val s = region.splits
        skin.add(DEFAULT_PANE9, NinePatch(region, s[0], s[1], s[2], s[3]), NinePatch::class.java)


        val window = Window.WindowStyle(skin.getFont(DEFAULT), Color.BLACK, skin.getDrawable(DEFAULT_PANE9))
        skin.add(DEFAULT, window, Window.WindowStyle::class.java)

        val label = Label.LabelStyle(skin.getFont(FONT_16), Color.BLACK)
        label.background = null
        skin.add(DEFAULT, label, Label.LabelStyle::class.java)
        skin.add(VIS_LABEL, label, Label.LabelStyle::class.java)

        val labelD = Label.LabelStyle(skin.getFont(DEFAULT), Color.WHITE)
        labelD.background = skin.getDrawable(GRADIENT_GG)
        skin.add(LABEL_D, labelD, Label.LabelStyle::class.java)

        // popupMenu
        val popupMenu = PopupMenu.PopupMenuStyle(skin.getDrawable(UI.LIGHT_GRAY), skin.getDrawable(BORDER))
        skin.add(DEFAULT, popupMenu, PopupMenu.PopupMenuStyle::class.java)
        // menuItem
        val menuItem = MenuItem.MenuItemStyle()
        menuItem.checked = skin.getDrawable(SELECTOR)
        menuItem.checkedFocused = skin.getDrawable(DARK_GRAY)
        menuItem.checkedOver = skin.getDrawable(GRAY)
        menuItem.font = skin.getFont(DEFAULT)
        menuItem.overFontColor = Color.WHITE
        menuItem.fontColor = Color.DARK_GRAY
        menuItem.downFontColor = Color.GREEN
        menuItem.subMenu = skin.getDrawable(DARK_GRAY)
        menuItem.over = skin.getDrawable(GRAY)
        menuItem.disabledFontColor = Color.GRAY
        menuItem.disabled = skin.getDrawable(LIGHT_GRAY)
        skin.add(DEFAULT, menuItem, MenuItem.MenuItemStyle::class.java)


        val checkBox = CheckBox.CheckBoxStyle(skin.getDrawable(CHECK_OFF), skin.getDrawable(CHECK_ON), skin.getFont(DEFAULT), Color.BLACK)
//        checkBox.checkboxOnOver = skin.getDrawable(GRADIENT_BG)
        skin.add(DEFAULT, checkBox, CheckBox.CheckBoxStyle::class.java)

    }

    private fun fileChooser() {
        val lfc = ListFileChooser.Style()
        lfc.space = 2f
        lfc.setButtonStyles(skin.get(DEFAULT, Button.ButtonStyle::class.java))
        lfc.pathFieldStyle = skin.get(DEFAULT, TextField.TextFieldStyle::class.java)
        lfc.background = skin.getDrawable(DEFAULT_PANE9)
        lfc.contentsStyle = skin.get(DEFAULT, List.ListStyle::class.java)
        lfc.contentsPaneStyle = skin.get(DEFAULT, ScrollPane.ScrollPaneStyle::class.java)
        skin.add(DEFAULT, lfc, ListFileChooser.Style::class.java)


//        val fc = FileChooserStyle()
//        fc.iconArrowLeft = skin.getDrawable(ARROW_16x8)
//        fc.iconArrowRight = skin.getDrawable(ARROW_16x8)
//        fc.iconDrive = skin.getDrawable(ARROW_16x8)
//        fc.iconFileAudio = skin.getDrawable(ARROW_16x8)
//        fc.iconFileImage = skin.getDrawable(ARROW_16x8)
//        fc.iconFilePdf = skin.getDrawable(ARROW_16x8)
//        fc.iconFileText = skin.getDrawable(ARROW_16x8)
//        fc.iconFolder = skin.getDrawable(ARROW_16x8)
//        fc.iconFolderNew = skin.getDrawable(ARROW_16x8)
//        fc.iconFolderParent = skin.getDrawable(ARROW_16x8)
//        fc.iconFolderStar = skin.getDrawable(ARROW_16x8)
//        fc.iconListSettings = skin.getDrawable(ARROW_16x8)
//        fc.iconRefresh = skin.getDrawable(ARROW_16x8)
//        fc.iconStar = skin.getDrawable(ARROW_16x8)
//        fc.iconStarOutline = skin.getDrawable(ARROW_16x8)
//        fc.iconTrash = skin.getDrawable(ARROW_16x8)
//
//        fc.contextMenuSelectedItem = skin.getDrawable(SELECTOR)
//        fc.expandDropdown = skin.getDrawable(BORDER)
//        fc.highlight = skin.getDrawable(LIGHT_GRAY)
//
//        fc.popupMenuStyle = skin.get(DEFAULT, PopupMenu.PopupMenuStyle::class.java)
//
//        skin.add(DEFAULT, fc, FileChooserStyle::class.java)
    }

    private fun buttons() {
        val b = Button.ButtonStyle()
        b.over = skin.getDrawable(LIGHT_GRAY)
        b.disabled = skin.getDrawable(LIGHT_GRAY)
        b.checkedOver = skin.getDrawable(LIGHT_GRAY)
        b.checked = skin.getDrawable(LIGHT_GRAY)
        b.down = skin.getDrawable(LIGHT_GRAY)
        b.focused = skin.getDrawable(LIGHT_GRAY)
        b.checkedFocused = skin.getDrawable(LIGHT_GRAY)
        skin.add(DEFAULT, b, Button.ButtonStyle::class.java)


        val ib = ImageButton.ImageButtonStyle()
        ib.imageChecked = skin.getDrawable(LIGHT_GRAY)
        ib.imageCheckedOver = skin.getDrawable(LIGHT_GRAY)
        ib.imageDisabled = skin.getDrawable(LIGHT_GRAY)
        ib.imageDown = skin.getDrawable(LIGHT_GRAY)
        ib.imageOver = skin.getDrawable(LIGHT_GRAY)
        ib.imageUp = skin.getDrawable(LIGHT_GRAY)
        skin.add(DEFAULT, ib, ImageButton.ImageButtonStyle::class.java)

        val textButton = TextButton.TextButtonStyle(
                skin.getDrawable(BUTTON_ON),
                skin.getDrawable(BUTTON_OFF),
                skin.getDrawable(BUTTON_OFF),
                skin.getFont(DEFAULT)
        )
        textButton.fontColor = skin.getColor(GRAY)
        textButton.downFontColor = skin.getColor(LIGHT_GRAY)
        textButton.checkedOverFontColor = skin.getColor(BLACK)
        textButton.checkedFontColor = skin.getColor(BLACK)

        textButton.pressedOffsetX = 2f
        textButton.pressedOffsetY = -2f
        textButton.checkedOffsetX = 1f
        textButton.checkedOffsetY = -1f

        skin.add(DEFAULT, textButton, TextButton.TextButtonStyle::class.java)

        val visTextButton = VisTextButton.VisTextButtonStyle(
                skin.getDrawable(GRAY),
                skin.getDrawable(DARK_GRAY),
                skin.getDrawable(DARK_GRAY),
                skin.getFont(DEFAULT)
        )
        skin.add(DEFAULT, visTextButton, VisTextButton.VisTextButtonStyle::class.java)

        val menu = Menu.MenuStyle()
        menu.background = skin.getDrawable(GRADIENT_GG)
        menu.border = skin.getDrawable(BORDER)
        menu.openButtonStyle = visTextButton
        skin.add(DEFAULT, menu, Menu.MenuStyle::class.java)
        val menuBar = MenuBar.MenuBarStyle(skin.getDrawable(GRADIENT_BG))
        skin.add(DEFAULT, menuBar, MenuBar.MenuBarStyle::class.java)

    }

    private fun toolTips() {
        val t = Tooltip.TooltipStyle()
        t.background = skin.getDrawable(SELECTOR)
        skin.add(DEFAULT, t, Tooltip.TooltipStyle::class.java)

        val list = List.ListStyle()
        list.font = skin.getFont(DEFAULT)
        list.background = skin.getDrawable(GRADIENT_GG)
        list.over = skin.getDrawable(GRADIENT_GG)
        list.selection = skin.getDrawable(GRADIENT_GG)
        list.down = skin.getDrawable(GRADIENT_GG)
        skin.add(DEFAULT, list, List.ListStyle::class.java)

        val sp = ScrollPane.ScrollPaneStyle()
        sp.background = skin.getDrawable(GRADIENT_GG)
        sp.corner = skin.getDrawable(GRADIENT_GG)
        sp.hScroll = skin.getDrawable(GRADIENT_GG)
        sp.hScrollKnob = skin.getDrawable(GRADIENT_GG)
        sp.vScroll = skin.getDrawable(GRADIENT_GG)
        sp.vScrollKnob = skin.getDrawable(GRADIENT_GG)
        skin.add(DEFAULT, sp, ScrollPane.ScrollPaneStyle::class.java)

    }


    private fun text() {
        val tf = TextField.TextFieldStyle()

        tf.background = skin.getDrawable(GRADIENT_GG)
        tf.focusedBackground = skin.getDrawable(GRADIENT_GG)
        tf.cursor = skin.getDrawable(GRADIENT_GG)
        tf.disabledBackground = skin.getDrawable(GRADIENT_GG)
        tf.selection = skin.getDrawable(GRADIENT_GG)
        tf.font = skin.getFont(DEFAULT)
        tf.messageFont = skin.getFont(DEFAULT)
        tf.messageFontColor = Color.WHITE
        tf.disabledFontColor = Color.DARK_GRAY
        tf.focusedFontColor = Color.LIGHT_GRAY
        tf.fontColor = Color.WHITE
        skin.add(DEFAULT, tf, TextField.TextFieldStyle::class.java)

    }


//    // создание пользовательской палитры в текстуре атласа
//    private val bitmapPosition = module.atlasFiller.getFreePosition(PaletteData.tableWidth(), PaletteData.tableHeight256()).also {
//        res.pixmap.setColor(Color.SLATE)
//        pixmap.fillRectangle(it.x.toInt(), it.y.toInt(), PaletteData.tableWidth(), PaletteData.tableHeight256())
//    }
//
//    // user palette table 4bpp from 9bpp (512 colors)
//    val table = Array<PaletteCell>(256) {
//        val x = bitmapPosition.x.toInt() + (it and 15) * PaletteData.cellSize + PaletteData.space * (it and 15)
//        val y = bitmapPosition.y.toInt() + (it and 240) + PaletteData.space * ((it and 240) / 16)
//        val region = TextureRegion(res.texture, x + 1, y + 1, PaletteData.cellSize, PaletteData.cellSize)
//        res.pixmap.setColor(hexColor512[manager.transparentColor].toLong(16).toInt())
//        res.pixmap.fillRectangle(region.regionX, region.regionY, region.regionWidth, region.regionHeight)
//        PaletteCell(manager.transparentColor, hexColor512[manager.transparentColor], region)
//    }
//    val region = TextureRegion(res.texture, bitmapPosition.x.toInt(), bitmapPosition.y.toInt(), PaletteData.tableWidth(), PaletteData.tableHeight256()).also {
////        it.flip(false, true)
//    }

}

val ui = lazy { UI() }.value