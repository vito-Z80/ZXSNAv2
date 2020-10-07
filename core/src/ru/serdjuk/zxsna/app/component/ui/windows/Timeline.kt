package ru.serdjuk.zxsna.app.component.ui.windows

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ru.serdjuk.zxsna.app.component.ui.DraggedPane
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module

class Timeline : DraggedPane(true) {

    private val skin = module.skin
    private val table = Table()


    init {
        repeat(3) {
            table.add(Image(skin.getDrawable(UI.CHECK_ON))).fill()
            table.add(Image(skin.getDrawable(UI.CHECK_ON))).fill()
            table.add(Image(skin.getDrawable(UI.CHECK_ON))).fill()
            table.add(Image(skin.getDrawable(UI.CHECK_ON))).fill()
            table.row()
        }
        table.pack()
        content = table

        pack()
        contentPack()
        module.stage.addActor(this)
        module.stage.addActor(table)

    }


}