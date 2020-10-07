package ru.serdjuk.zxsna.app.component.ui.windows

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.component.world.tools.AppTools
import ru.serdjuk.zxsna.app.component.world.tools.ToolName
import ru.serdjuk.zxsna.app.system.module

class Tools : Window("Tools:", module.skin) {


    private val spriteSelected = Image(module.skin.getRegion(UI.SELECTOR))
    private val table = Table()
    private val pen = Image(module.skin.getRegion(UI.TOOL_PEN))
    private val fill = Image(module.skin.getRegion(UI.TOOL_FILL))
    private val select = Image(module.skin.getRegion(UI.TOOL_SELECT))


    init {
        spriteSelected.isVisible = false
        pen.addListener(listener(ToolName.PEN, pen))
        fill.addListener(listener(ToolName.FILL, fill))
        select.addListener(listener(ToolName.SELECT, select))
        table.add(pen).fill().row()
        table.add(fill).fill().row()
        table.add(select).fill().row()
        table.pack()
        add(table).row()
        add(spriteSelected)
        pack()
    }

    private fun listener(model: Int, image: Image) = object : ClickListener() {

        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            spriteSelected.isVisible = true
            spriteSelected.setPosition(table.x + image.x, table.y + image.y)
            ToolName.used = model
            super.clicked(event, x, y)
        }

    }


}