package ru.serdjuk.zxsna.app.component.ui.debug

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ru.serdjuk.zxsna.StartZXSNA
import ru.serdjuk.zxsna.app.Packable
import ru.serdjuk.zxsna.app.component.cameraControl
import ru.serdjuk.zxsna.app.utils.WindowOutScreen
import ru.serdjuk.zxsna.app.system.History
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.system

@ExperimentalUnsignedTypes
class AppDebug() : WindowOutScreen("Debug window") {
    val table = Table(skin)

    private val fps = Label("", skin)
    private val worldDrawCall = Label("", skin)
    private val uiDrawCall = Label("", skin)
    private val javaHeap = Label("", skin)
    private val nativeHeap = Label("", skin)
    private val cameraPosition = Label("", skin)
    private val cameraZoom = Label("", skin)
    private val undoSize = Label("", skin)
    private val stageActors = Label("", skin)
    private val packableStorage = Label("", skin)
    private val packableTypes = Label("", skin)
    private val systemSize = Label("", skin)
    private val millis = Label("", skin)

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

        table.add("J.Heap:").fill()
        table.add(javaHeap).fill()
        table.row()

        table.add("N.Heap:").fill()
        table.add(nativeHeap).fill()
        table.row()


        table.add("Camera position:").fill()
        table.add(cameraPosition).fill()
        table.row()

        table.add("Camera zoom:").fill()
        table.add(cameraZoom).fill()
        table.row()

        table.add("Stage actors:").fill()
        table.add(stageActors).fill()
        table.row()

        table.add("Packable storage:").fill()
        table.add(packableStorage).fill()
        table.row()

        table.add("Packable types:").fill()
        table.add(packableTypes).fill()
        table.row()

        table.add("SYSTEM:").fill()
        table.add(systemSize).fill()
        table.row()

        table.add("cpu+gpu:").fill()
        table.add(millis).fill()
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
            val jHeap = Gdx.app.javaHeap
            val nHeap = Gdx.app.nativeHeap
            javaHeap.setText("${jHeap / 1048576}Mb | $jHeap")
            nativeHeap.setText("${nHeap / 1048576}Mb | $nHeap")
            cameraPosition.setText(cameraControl.camera.position.toString())
            cameraZoom.setText(cameraControl.camera.zoom.toString())
            undoSize.setText(History.hp.size)
            packableStorage.setText(Packable.storage.size)
            packableTypes.setText(Packable.types.size)
            stageActors.setText(module.stage.root.children.size)
            systemSize.setText(system.systems.size)
            millis.setText("${StartZXSNA.appTime}ms")
            timer = 0f
        }
        super.act(delta)
    }
}