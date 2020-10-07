package ru.serdjuk.zxsna.app.system

class SpriteSystem : ISystem {

    val components = null
    val resources = null
    val sensors = null

    override var isVisible = false
    override fun update(delta: Float) {

    }

    override fun draw() {

    }

    override fun setSystem() {
        isVisible = true
    }

    override fun resetSystem() {
        isVisible = false
    }

}

val spriteSystem = SpriteSystem()