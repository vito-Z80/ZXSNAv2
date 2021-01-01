package ru.serdjuk.zxsna.app.windows

import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ru.serdjuk.zxsna.app.Packable
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.cursor
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.system.system
import ru.serdjuk.zxsna.app.tools.AppToolsSystem
import ru.serdjuk.zxsna.app.tools.ToolName
import ru.serdjuk.zxsna.app.utils.WindowOutScreen
import ru.serdjuk.zxsna.app.windows.HelpMessage

@ExperimentalUnsignedTypes
class AppToolsWindow : WindowOutScreen(" Tools"), Packable {

    private val toolMarker = Image(res.atlas.findRegion(UI.SELECTOR))
    private val penIcon = object : Image(res.atlas.findRegion(UI.TOOL_PEN)), HelpMessage {
        override val message = "This is a pen TOOL !!!"
    }
    private val fillIcon = object : Image(res.atlas.findRegion(UI.TOOL_FILL)), HelpMessage {
        override val message = "sdafk jal kf;laks djfa;ls\nAnd other porbrn.... \n  aga aga"
    }
    private val selectIcon = object : Image(res.atlas.findRegion(UI.TOOL_SELECT)), HelpMessage {
        override val message = "This selection TOOL"
    }

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
        Packable.addComponent(this)
        system.show<AppToolsSystem>()    // tools system always is visible
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
                cursor.setSystem(Cursor.SystemCursor.Arrow)
                toolMarker.setPosition(penIcon.x, penIcon.y)
            }
            ToolName.FILL -> {
                cursor.setSystem(Cursor.SystemCursor.Hand)
                toolMarker.setPosition(fillIcon.x, fillIcon.y)
            }
            ToolName.SELECT -> {
                cursor.setSystem(Cursor.SystemCursor.Crosshair)
                toolMarker.setPosition(selectIcon.x, selectIcon.y)
            }
        }
    }


    override fun collectData(): ByteArray {
        return ByteArray(1) { AppToolsSystem.usedTool.toByte() }
    }

    override fun parseData(bytes: ByteArray) {
        AppToolsSystem.usedTool = bytes[0].toInt()
        setMarkerPosition()
    }

}