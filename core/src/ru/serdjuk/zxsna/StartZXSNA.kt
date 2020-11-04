package ru.serdjuk.zxsna

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL30
import ru.serdjuk.zxsna.app.component.cameraControl
import ru.serdjuk.zxsna.app.component.component
import ru.serdjuk.zxsna.app.component.ui.palette.PaletteUtils
import ru.serdjuk.zxsna.app.component.ui.palette.AppPaletteWindow
import ru.serdjuk.zxsna.app.component.ui.ui
import ru.serdjuk.zxsna.app.component.ui.windows.MainMenu
import ru.serdjuk.zxsna.app.component.world.shapes
import ru.serdjuk.zxsna.app.system.*
import ru.serdjuk.zxsna.app.system.tiles.appSheets
import ru.serdjuk.zxsna.app.utils.*

@ExperimentalUnsignedTypes
class StartZXSNA : ScreenAdapter() {

    private val camera = module.worldCamera
    private val batch = module.worldBatch
    private val color = currentBackgroundColor
    //    val tools = component.get<AppTools>()

    override fun show() {


        ui.install()
        PaletteUtils
        Gdx.input.inputProcessor = module.stage


//        module.stage.addActor(Tools())

        MainMenu()







        super.show()

    }


    override fun render(delta: Float) {


        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)

//        system.update(delta)

//        res.layers[AppPaletteWindow.offset].update(delta)
        History.update()
        sensor.screenMouse.set(Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
        if (outActor()) {
            sensor.preMouse.set(sensor.worldMouse)
            sensor.mouseRefresh()
            cameraControl.update(delta)
            system.update(delta)
            res.layers[AppPaletteWindow.offset].update(delta)

//            component.update(delta)
        } else {
            cameraControl.amount = 0f
        }

        batch.projectionMatrix = camera.combined
        batch.begin()
        system.draw()
//        batch.draw(res.texture, 0f, 0f)
        batch.draw(res.layers[AppPaletteWindow.offset].region, 0f, 0f)
        batch.end()

        shapes.update()
        ui.update(delta)

        super.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        module.worldViewport.update(width, height, true)
        module.uiViewport.update(width, height, true)
        appSheets.viewport.update(width, height, true)
        sensor.resizeBounds()
        super.resize(width, height)
    }
}

