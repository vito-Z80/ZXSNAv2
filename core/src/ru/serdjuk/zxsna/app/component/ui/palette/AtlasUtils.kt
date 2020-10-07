package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2

class AtlasUtils(val pixmap: Pixmap) {


    private val errorMessage = "No more space in pixmap."
    private val regions = ArrayList<InsertRegion>()

    fun insertRectangle(color: Color, fill: Boolean, area: Pair<Int, Int>): Vector2 {
        val position = getFreePosition(area.first, area.second)
        pixmap.setColor(color)
        if (fill) pixmap.fillRectangle(position.x.toInt(), position.y.toInt(), area.first, area.second)
        pixmap.drawRectangle(position.x.toInt(), position.y.toInt(), area.first, area.second)
        return position
    }

    fun insertRectangleNamed(name: String, color: Color, fill: Boolean, area: Pair<Int, Int>, texture: Texture): Vector2 {
        val position = getFreePosition(area.first, area.second)
        pixmap.setColor(color)
        if (fill) pixmap.fillRectangle(position.x.toInt(), position.y.toInt(), area.first, area.second)
        pixmap.drawRectangle(position.x.toInt(), position.y.toInt(), area.first, area.second)
        regions.add(InsertRegion(texture, position.x.toInt(), position.y.toInt(), area.first, area.second, name = name))
        return position
    }


    fun insertAreas(color: Color, fill: Boolean, vararg areas: Pair<Int, Int>) {
        areas.forEach {  area ->
            insertRectangle(color, fill, area)
        }
    }

    /*
    insertColoredAres (width,height,Color)
     */
    fun insertColorAreas(texture: Texture, fill: Boolean, vararg coloredAreas: Triple<Int, Int, Color>) {
        coloredAreas.forEach {
            val position = insertRectangle(it.third, fill, Pair(it.first, it.second))
            regions.add(InsertRegion(texture, position.x.toInt(), position.y.toInt(), it.first, it.second))
            pixmap.setColor(Color.WHITE)
            pixmap.drawRectangle(position.x.toInt(), position.y.toInt(), it.first, it.second)
        }
    }

    fun insert(pixmap: Pixmap) {
        val position = getFreePosition(pixmap.width, pixmap.height)
        pixmap.drawPixmap(pixmap, position.x.toInt(), position.y.toInt())
    }

    /*
    pixmap  new pixmap well need added
    name - name for atlas
    texture - this. pixmap texture
     */
    fun insertNamed(pixmap: Pixmap, name: String, texture: Texture): Vector2 {
        val position = getFreePosition(pixmap.width, pixmap.height)
        pixmap.drawPixmap(pixmap, position.x.toInt(), position.y.toInt())
        regions.add(InsertRegion(texture, position.x.toInt(), position.y.toInt(), pixmap.width, pixmap.height, name = name))
        return position
    }

    fun findRegion(name: String) = regions.find { it.name == name } ?: error("Not find region with name: $name")
    fun getRegion(id: Int) = regions[id]

//    fun drawText(text: String, font: BitmapFont) {
//        println(font.region.regionX)
//        println(font.region.regionY)
//        println(font.region.regionWidth)
//        println(font.region.regionHeight)
//        val position = getFreePosition(font.region.regionWidth, font.region.regionHeight)
//
//
//        font.region.texture.textureData.prepare()
//        font.regions.forEach {
//            val b = it.texture.textureData.consumePixmap()
//            pixmap.drawPixmap(b,
//                    position.x.toInt(), position.y.toInt(), 0, 0, 256, 128
//            )
//        }
//
//    }


    fun getFreePosition(soughtWidth: Int, soughtHeight: Int, clearPixelColor: Int = 0): Vector2 {
        val position = Vector2()
        while (true) {
            var fit = true
            var h = 0
            getClearPixel(position, clearPixelColor, soughtWidth)
            val x = position.x
            val y = position.y
            while (h++ != soughtHeight) {
                if (position.y > (pixmap.height - soughtHeight)) error(errorMessage)

                repeat(soughtWidth) w@{
                    if (pixmap.getPixel((position.x + it).toInt(), position.y.toInt()) != clearPixelColor) {
                        fit = false
                        position.x += it + 1
                        return@w
                    }
                }
                if (!fit) {
                    position.y = y
                    break
                } else {
                    position.y++
                }
            }
            if (fit) {
                position.set(x, y)
                break
            }
        }
        return position
    }


    private fun getClearPixel(position: Vector2, clearPixel: Int, soughtWidth: Int): Vector2 {
        while ((position.x + soughtWidth).toInt() >= pixmap.width || pixmap.getPixel(position.x.toInt(), position.y.toInt()) != clearPixel) {
            if ((position.x + soughtWidth).toInt() >= pixmap.width) {
                position.x = -1f
                position.y++
            }
            position.x++
        }
        return position
    }


}