package ru.serdjuk.zxsna.app.utils

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class RectInt(var x: Int = 0, var y: Int = 0, var width: Int = 0, var height: Int = 0) {

    fun overlaps(r: Rectangle): Boolean {
        return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y
    }

    fun contains(x: Float, y: Float): Boolean {
        return this.x <= x && this.x + width >= x && this.y <= y && this.y + height >= y
    }

    operator fun contains(point: Vector2): Boolean {
        return contains(point.x, point.y)
    }

    fun set(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    /** Creates rectangles of the specified size from the user's selection area.
     * Нарезка квадратов размером 8 или 16
     * @param selection user selection area
     * @param size the size of the cut rectangles, may be only 16 or 8
     * @return возвращает нарезанные ректанглы
     */
    suspend fun slicingRectangles(size: Int): Array<RectInt> {
        val width = this.width / size
        val height = this.height / size
        val rectangles = Array<RectInt>(width * height) { RectInt() }
        // создание ректанглов слеваСверху
        var count = 0
        while (this.height > 0) {
            this.height -= size
            var x = 0
            while (x < this.width) {
                rectangles[count++].set(this.x + x, this.y + this.height, size, size)
                x += size
            }
        }
        return rectangles

        // FIXME другой вариант:
        // FIXME    выделение должно быть кратно 8/16 либо отмена действия
        // FIXME    причем если выделение кратно 8 то работать можно только с тайлами
        // FIXME    если выделение кратно 16 то работать можно как со спрайтами так и с тайлами
        // FIXME    то есть меню ПКМ должно подстроиться под кратность выделения, при не кратности оповестить пользователя
    }


    override fun toString(): String {
        return "[$x,$y,$width,$height]"
    }


}