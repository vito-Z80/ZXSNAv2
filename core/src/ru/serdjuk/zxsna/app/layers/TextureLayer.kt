package ru.serdjuk.zxsna.app.layers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import ru.serdjuk.zxsna.app.utils.RectInt
import ru.serdjuk.zxsna.app.utils.TextureLayerUtils
import ru.serdjuk.zxsna.app.utils.getBytes
import ru.serdjuk.zxsna.app.utils.getInt

@ExperimentalUnsignedTypes
class TextureLayer(var width: Int, var height: Int) : TextureLayerUtils() {
    
    
    // как всю эту поеботу хранить в проекте понятия не имею
    
    // подумать куда впихнуть имя слоя для удобства юзера, что бы он мог быстро по названию вспомнить требуемый слой
    var name = "Layer:  "
    
    // выдкеление на слое с помощью инструмента выдления
    val selectionArea = Rectangle()
    
    // позиция камеры до смены слоя
    var lastCameraPosition: Vector3? = null
    
    // скейл камеры до смены слоя
    var lastCameraScale: Float? = null
    
    // выделение до смены слоя (если было)
    val lastSelector: Rectangle? = null
    
    // ID последней выбранной ячейки палитры
    var lastCellId: Int? = null
    
    // количество использованных цветов из 4bpp палитры (по порядку в массиве)
    // когда в палитру добавляется новый цвет тогда нужно проверить был ли до этого цвет в ячейке палитры
    // если был (!=0) то диалоговое окно о замене или отказе замены
    val colorCount = Array<Int>(16) { 0 }
    
    var pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888).also {
        it.blending = Pixmap.Blending.None
        it.setColor(Color.CLEAR)
        it.fill()
    }
    
    var texture = Texture(pixmap)
    val region = TextureRegion(texture).also { it.flip(false, true) }
    val bounds = RectInt(0, 0, pixmap.width, pixmap.height)
    
    // данные при процессе обработки пользовательского выделения
    var selectionRectNumbers = 0        // кол-во прямоугольников входящих в выделенную область
    
    // вычисляется после определения как будет сохраняться - 8х8 или 16х16
    var emptyRectangles = 0             // кол-во пустых прямогольников в выделенной области
    var imageRectangles = 0             // кол-во прямоугольников в выделенной области в которых нарисован ъотя бы 1 пиксель
    
    
    fun redrawTexture() {
        texture.draw(pixmap, 0, 0)
    }
    
    fun update(delta: Float) {
    
    }
    
    fun clearSelectionArea() {
        selectionArea.set(0f, 0f, 0f, 0f)
    }
    
    fun getSelectionRegion(): TextureRegion {
        return TextureRegion(texture, selectionArea.x, selectionArea.y, selectionArea.width, selectionArea.height)
    }
    
    fun getSlices(type: Int) {
        val region = getSelectionRegion()
        
    }
    
    fun fillPixels(byteArray: ByteArray) {
        var counter = 0
        repeat(region.regionHeight) { y ->
            repeat(region.regionWidth) { x ->
                val color = getInt(byteArray.copyOfRange(counter, counter + 4))
                counter += 4
                pixmap.setColor(color)
                pixmap.drawPixel(x, y)
            }
        }
    }
    
    fun collectPixels(): ByteArray {
        val bytes = ArrayList<Byte>()
        repeat(region.regionHeight) { y ->
            repeat(region.regionWidth) { x ->
                bytes.addAll(getBytes(pixmap.getPixel(x, y)).toTypedArray())
            }
        }
        return bytes.toByteArray()
    }
    
    
}