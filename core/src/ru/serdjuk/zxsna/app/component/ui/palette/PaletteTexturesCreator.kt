package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.component.ui.UI.Companion.TRANSPARENT_OVERLAY
import ru.serdjuk.zxsna.app.resources.hexColor512
import ru.serdjuk.zxsna.app.system.res

@ExperimentalUnsignedTypes
class PaletteTexturesCreator {
    private val pixmap = res.pixmap
    private val texture = res.texture
    private val atlasUtils = module.atlasUtils


    private val userPalettePosition = atlasUtils.getFreePosition(PaletteData.tableWidth(), PaletteData.tableHeight256()).also {
        pixmap.setColor(Color.SLATE)
        pixmap.fillRectangle(it.x.toInt(), it.y.toInt(), PaletteData.tableWidth(), PaletteData.tableHeight256())
    }

    // user palette table 4bpp from 9bpp (512 colors)
    val userTable = Array<PaletteCell>(256) {
        val x = userPalettePosition.x.toInt() + (it and 15) * PaletteData.cellSize + PaletteData.space * (it and 15)
        val y = userPalettePosition.y.toInt() + (it and 240) + PaletteData.space * ((it and 240) / 16)
        val region = TextureRegion(texture, x + 1, y + 1, PaletteData.cellSize, PaletteData.cellSize)
        pixmap.setColor(hexColor512[PaletteData.transparentColorId].int)
        pixmap.fillRectangle(region.regionX, region.regionY, region.regionWidth, region.regionHeight)
        PaletteCell(PaletteData.transparentColorId, hexColor512[PaletteData.transparentColorId].hex, region)
    }
    val userRegion = TextureRegion(texture, userPalettePosition.x.toInt(), userPalettePosition.y.toInt(), PaletteData.tableWidth(), PaletteData.tableHeight256()).also {
        module.skin.add(UI.USER_PALETTE, it, TextureRegion::class.java)
    }


    private val mainPalettePosition = atlasUtils.getFreePosition(PaletteData.tableWidth(), PaletteData.tableHeight512()).also {
        pixmap.setColor(Color.SLATE)
        pixmap.fillRectangle(it.x.toInt(), it.y.toInt(), PaletteData.tableWidth(), PaletteData.tableHeight512())
    }

    //  palette table 9bpp
    val mainTable = Array<PaletteCell>(hexColor512.size) {
        val x = mainPalettePosition.x.toInt() + (it and 15) * PaletteData.cellSize + PaletteData.space * (it and 15)
        val y = mainPalettePosition.y.toInt() + (it and 496) + PaletteData.space * ((it and 496) / 16)
        val region = TextureRegion(texture, x + 1, y + 1, PaletteData.cellSize, PaletteData.cellSize)
        pixmap.setColor(hexColor512[it].int)
        pixmap.fillRectangle(region.regionX, region.regionY, region.regionWidth, region.regionHeight)
        PaletteCell(it, hexColor512[it].hex, region)
    }
    val mainRegion = TextureRegion(texture, mainPalettePosition.x.toInt(), mainPalettePosition.y.toInt(), PaletteData.tableWidth(), PaletteData.tableHeight512()).also {
        module.skin.add(UI.MAIN_PALETTE, it, TextureRegion::class.java)
    }

    //  overlay for user palette table
    val width = PaletteData.tableWidth()
    val height = PaletteData.tableHeight512()
    val overlayPosition = atlasUtils.getFreePosition(width, height)
    val x = overlayPosition.x.toInt()
    val y = overlayPosition.y.toInt()
    val height15Cells = PaletteData.cellSize * 15 + PaletteData.space * 15
    val overlayRegion = TextureRegion(texture, x, y + height15Cells, width, PaletteData.tableHeight256()).also {
        module.skin.add(TRANSPARENT_OVERLAY, it, TextureRegion::class.java)
        pixmap.setColor(0f, 0f, 0f, 0.4f)
        pixmap.fillRectangle(x, y, width, height15Cells)
        pixmap.fillRectangle(x, y + height15Cells + PaletteData.cellSize + PaletteData.space * 2, width, height15Cells)
        pixmap.setColor(1f, 1f, 1f, 0.01f)
        pixmap.fillRectangle(x, y + height15Cells, width, 18)

    }

    // levitation cell
    val levitationPosition = atlasUtils.getFreePosition(PaletteData.cellSize, PaletteData.cellSize)
    val levitationRegion = TextureRegion(texture, levitationPosition.x.toInt(), levitationPosition.y.toInt(), PaletteData.cellSize, PaletteData.cellSize).also {
        pixmap.setColor(Color.BLACK)
        pixmap.drawRectangle(levitationPosition.x.toInt(), levitationPosition.y.toInt(), PaletteData.cellSize, PaletteData.cellSize)
        module.skin.add(UI.LEVITATION_CELL, it, TextureRegion::class.java)
        texture.draw(pixmap, 0, 0)
    }


}