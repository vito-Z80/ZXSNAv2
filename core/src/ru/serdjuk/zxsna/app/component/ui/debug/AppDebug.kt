package ru.serdjuk.zxsna.app.component.ui.debug

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ru.serdjuk.zxsna.app.component.cameraControl
import ru.serdjuk.zxsna.app.utils.WindowOutScreen
import ru.serdjuk.zxsna.app.system.History
import ru.serdjuk.zxsna.app.system.module

@ExperimentalUnsignedTypes
class AppDebug() : WindowOutScreen("Debug window") {
    val table = Table(skin)

    private val fps = Label("", skin)
    private val worldDrawCall = Label("", skin)
    private val uiDrawCall = Label("", skin)
    private val memory = Label("", skin)
    private val cameraPosition = Label("", skin)
    private val cameraZoom = Label("", skin)
    private val undoSize = Label("", skin)

    init {
        table.pad(2f)
        table.add("FPS:").width(192f).align(Align.left)
        table.add(fps).width(256f).align(Align.left)
        table.row()
        table.add("World draw calls:").fill()
        table.add(worldDrawCall).fill()
        table.row()

        table.add("UI draw calls:").fill()
        table.add(uiDrawCall).fill()
        table.row()


        table.add("used:").fill()
        table.add(memory).fill()
        table.row()

        table.add("Camera position:").fill()
        table.add(cameraPosition).fill()
        table.row()

        table.add("Camera zoom:").fill()
        table.add(cameraZoom).fill()
        table.row()

        table.add("UNDO size:").fill()
        table.add(undoSize).fill()
        table.row()


        table.pack()
        add(table)
        pack()
    }


    var timer = 0f
    override fun act(delta: Float) {
        timer += delta
        if (timer > 1) {
            fps.setText("${Gdx.graphics.framesPerSecond}")
            worldDrawCall.setText(module.worldBatch.renderCalls)
            uiDrawCall.setText(module.uiBatch.renderCalls)
            val usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            memory.setText("${usedBytes / 1048576} Mb / $usedBytes b")
            cameraPosition.setText(cameraControl.camera.position.toString())
            cameraZoom.setText(cameraControl.camera.zoom.toString())
            undoSize.setText(History.hp.size)

            timer = 0f
        }
        super.act(delta)
    }
}