package ru.serdjuk.zxsna.app.system.levelEditorSystem.components

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ru.serdjuk.zxsna.app.utils.buttonOnce
import ru.serdjuk.zxsna.app.system.sensor

class SheetElement(val region: TextureRegion, val bounds: Rectangle) {


    fun tap(): Boolean = buttonOnce(0) && bounds.contains(sensor.worldMouse.x, sensor.worldMouse.y)


    fun move(position: Vector2) {

    }

    fun replace(newRegion: TextureRegion) {

    }


}