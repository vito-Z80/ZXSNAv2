package ru.serdjuk.zxsna.app.layers

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import ru.serdjuk.zxsna.app.Packable
import ru.serdjuk.zxsna.app.palette.AppPaletteWindow
import ru.serdjuk.zxsna.app.palette.UserPalette
import ru.serdjuk.zxsna.app.system.ISystem
import ru.serdjuk.zxsna.app.system.module
import kotlin.math.max

@ExperimentalUnsignedTypes
class AppLayersSystem : Packable, ISystem {
    override var isVisible = true

    private val batch = module.worldBatch

    private val sprites4bpp = Array<TextureLayer>(16) { TextureLayer(1024, 1024) }
    private val tiles4bpp = Array<TextureLayer>(16) { TextureLayer(1024, 1024) }

    private val sprites9bpp = Array<TextureLayer>(1) { TextureLayer(1024, 1024) }
    private val tiles9bpp = Array<TextureLayer>(1) { TextureLayer(1024, 1024) }

    private val packLayers = mapOf(
        Pair(AppPaletteWindow.Belongingness.SPRITES_4BPP, sprites4bpp),
        Pair(AppPaletteWindow.Belongingness.TILES_4BPP, tiles4bpp),
        Pair(AppPaletteWindow.Belongingness.TILES_9BPP, tiles9bpp),
        Pair(AppPaletteWindow.Belongingness.SPRITES_9BPP, sprites9bpp)
    )

    init {
        Packable.addComponent(this)
    }

    override fun draw() {
        val layer = getType()
        if (layer != null) {
            // если в коллекции один слой - используется палитра 9bpp, в палитре 4bpp используется 16 слоев
            val layerId = if (layer.size > 1) UserPalette.storage[AppPaletteWindow.belong].offset else 0
            batch.draw(layer[layerId].region, 0f, 0f)
        }
    }

    override fun update(delta: Float) {

        val layer = getType()
        if (layer != null) {
            // если в коллекции один слой - используется палитра 9bpp, в палитре 4bpp используется 16 слоев
            val layerId = if (layer.size > 1) UserPalette.storage[AppPaletteWindow.belong].offset else 0
            layer[layerId].update(delta)
        }
    }

    //    fun getLayer() = getType()?.get(AppPaletteWindow.offset)
    fun getLayer() = getType()?.let {
        // если в коллекции один слой - используется палитра 9bpp, в палитре 4bpp используется 16 слоев
        if (it.size > 1) it[UserPalette.storage[AppPaletteWindow.belong].offset] else it[0]
    }

    /**
     * Get multiple size by selection.
     * Sprites 16x16 or tiles 8x8 or 0 is not multiple
     * @param area select area
     * @param spriteSize sprite size (16f)
     * @param tileSize tile size (8f)
     * @return size (sprite or tile (16 or 8))
     */
    fun getMultipleSizeBySelection(area: Rectangle, spriteSize: Float, tileSize: Float) = max(
        (area.width % spriteSize == 0f && area.height % spriteSize == 0f).let { if (it) spriteSize else 0f },
        (area.width % tileSize == 0f && area.height % tileSize == 0f).let { if (it) tileSize else 0f }
    ).toInt().also { println(it) }

    /**
     * @return массив регионов
     */
    fun getLayerSlices(): Array<TextureRegion>? {
        val layer = getLayer()
        if (layer != null) {
            val spriteSize = 16f
            val tileSize = 8f
            val area = layer.selectionArea
            val itemSize = getMultipleSizeBySelection(area, spriteSize, tileSize)
            if (itemSize != 0) {
                val columns = (area.width / itemSize).toInt()
                val rows = (area.height / itemSize).toInt()
//                println(columns)
//                println(rows)
                val slicesBounds = layer.getSlicesBounds(rows, columns, itemSize)
                return Array<TextureRegion>(slicesBounds.size) {
                    TextureRegion(
                        layer.texture,
                        slicesBounds[it].x + area.x.toInt(),
                        slicesBounds[it].y + area.y.toInt(),
                        slicesBounds[it].width,
                        slicesBounds[it].height
                    )
                }
            }   // else stops slicing
        }
        return null
    }


    private fun getType() = when (AppPaletteWindow.belong) {
        AppPaletteWindow.Belongingness.SPRITES_4BPP -> sprites4bpp
        AppPaletteWindow.Belongingness.TILES_4BPP -> tiles4bpp
        AppPaletteWindow.Belongingness.SPRITES_9BPP -> sprites9bpp
        AppPaletteWindow.Belongingness.TILES_9BPP -> tiles9bpp
        else -> null
    }


    /**
     *
     * 1) количество коллекций слоев.
     * 2) текущая принадлежность ралитры (belong var)
     * 3) перебирается коллекция слоев, очередная колекция сохраняет данные всех своих слоев по порядку. Затем следующая коллекуия слоев.
     *
     *  FIXME любой слой имеет разрешение 1024х1024, если будет возможность пользователям изменять разрешения, то добавить.
     *  не изменять расположения коллекции слоев !!!
     */

    override fun collectData(): ByteArray {
        val bytes = ArrayList<Byte>()
        // количество коллекций слоев.
        bytes.add(packLayers.size.toByte())
        // текущая принадлежность ралитры
        bytes.add(AppPaletteWindow.belong.toByte())
        println("collections: ${packLayers.size}")
        repeat(packLayers.size) { layerCollectionId ->
            print("collection $layerCollectionId: ")
            // данные всех слоев очередной коллекции слоев добавляются поочередно.
            packLayers[layerCollectionId]?.forEachIndexed { lId, la ->
                bytes.addAll(la.collectPixels().toTypedArray())
                print("${la.hashCode()}, ")
            }
            println()
        }
        return bytes.toByteArray()
    }


    override fun parseData(bytes: ByteArray) {
        var counter = 0
        // количество коллекций слоев.
        val collectionNumbers = bytes[counter++].toInt()
        // текущая принадлежность ралитры
        AppPaletteWindow.belong = bytes[counter++].toInt()
        println("collections: $collectionNumbers")
        val layerSize = 1024 * 1024 * 4 // in bytes
        repeat(collectionNumbers) { id ->
            print("collection $id: ")
            packLayers[id]?.forEachIndexed { lId, la ->
                la.fillPixels(bytes.copyOfRange(counter, counter + layerSize))
                counter += layerSize
                print("${la.hashCode()}, ")
            }
            println()
        }
        redrawAllLayers()
    }

    fun redrawAllLayers() {
        sprites4bpp.forEach { it.redrawTexture() }
        sprites9bpp.forEach { it.redrawTexture() }
        tiles4bpp.forEach { it.redrawTexture() }
        tiles9bpp.forEach { it.redrawTexture() }
    }
}