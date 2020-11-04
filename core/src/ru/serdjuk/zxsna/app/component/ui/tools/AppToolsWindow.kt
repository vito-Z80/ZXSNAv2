package ru.serdjuk.zxsna.app.component.ui.tools

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.utils.WindowOutScreen
import ru.serdjuk.zxsna.app.component.world.tools.AppToolsSystem
import ru.serdjuk.zxsna.app.component.world.tools.ToolName
import ru.serdjuk.zxsna.app.system.pack.Compressable
import ru.serdjuk.zxsna.app.system.res

@ExperimentalUnsignedTypes
class AppToolsWindow : WindowOutScreen(" Tools"), Compressable {

    private val toolMarker = Image(res.atlas.findRegion(UI.SELECTOR))

    private val penIcon = Image(res.atlas.findRegion(UI.TOOL_PEN))
    private val fillIcon = Image(res.atlas.findRegion(UI.TOOL_FILL))
    private val selectIcon = Image(res.atlas.findRegion(UI.TOOL_SELECT))



    init {
        penIcon.addListener(toolListener(fun() { AppToolsSystem.usedTool = ToolName.PEN }))
        fillIcon.addListener(toolListener(fun() { AppToolsSystem.usedTool = ToolName.FILL }))
        selectIcon.addListener(toolListener(fun() { AppToolsSystem.usedTool = ToolName.SELECT }))

        add(penIcon).row()
        add(fillIcon).row()
        add(selectIcon).row()
        pack()

        addActor(toolMarker)
        toolMarker.setPosition(-1000f, -1000f)
        toolMarker.touchable = Touchable.disabled

    }

    private fun toolListener(function: () -> Unit) = object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            function.invoke()
            setMarkerPosition()
            super.clicked(event, x, y)
        }
    }

    private fun setMarkerPosition() {
        when (AppToolsSystem.usedTool) {
            ToolName.PEN -> {
                toolMarker.setPosition(penIcon.x, penIcon.y)
            }
            ToolName.FILL -> {
                toolMarker.setPosition(fillIcon.x, fillIcon.y)
            }
            ToolName.SELECT -> {
                toolMarker.setPosition(selectIcon.x, selectIcon.y)
            }
        }
    }


    override fun compress(): ByteArray {
        return ByteArray(1) { AppToolsSystem.usedTool.toByte() }
    }

    override fun uncompress(bytes: ByteArray) {
        AppToolsSystem.usedTool = bytes[0].toInt()
        setMarkerPosition()
    }

}