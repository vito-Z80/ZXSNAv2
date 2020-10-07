package ru.serdjuk.zxsna.app.system.levelEditorSystem

import com.badlogic.gdx.Input
import ru.serdjuk.zxsna.app.system.ISystem
import ru.serdjuk.zxsna.app.utils.keyOnce
import ru.serdjuk.zxsna.app.system.levelEditorSystem.components.LevelEditorComponent
import ru.serdjuk.zxsna.app.system.levelEditorSystem.resources.LevelMaps

class LevelSystem : ISystem {

    val components = LevelEditorComponent()
    val resources = LevelMaps()
    val sensors = null

    override var isVisible = false
    override fun update(delta: Float) {
        if (resources.currentMapId < 0) {
            resources.createNewMap()
        }
        if (keyOnce(Input.Keys.ENTER)) {
            resources.createNewMap()
        }
    }


    override fun draw() {

    }

    override fun setSystem() {

    }

    override fun resetSystem() {

    }

}