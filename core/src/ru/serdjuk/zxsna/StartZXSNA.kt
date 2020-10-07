package ru.serdjuk.zxsna

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL30
import ru.serdjuk.zxsna.app.component.cameraControl
import ru.serdjuk.zxsna.app.component.component
import ru.serdjuk.zxsna.app.component.ui.palette.PaletteManager
import ru.serdjuk.zxsna.app.component.ui.palette.PaletteUtils
import ru.serdjuk.zxsna.app.component.ui.ui
import ru.serdjuk.zxsna.app.component.ui.windows.AppDebug
import ru.serdjuk.zxsna.app.component.ui.windows.MainMenu
import ru.serdjuk.zxsna.app.component.ui.windows.Tools
import ru.serdjuk.zxsna.app.component.world.shapes
import ru.serdjuk.zxsna.app.system.*
import ru.serdjuk.zxsna.app.utils.currentBackgroundColor
import ru.serdjuk.zxsna.app.utils.outActor

@ExperimentalUnsignedTypes
class StartZXSNA : ScreenAdapter() {


    private val color = currentBackgroundColor
    //    val tools = component.get<AppTools>()

    override fun show() {

        ui.install()
        PaletteUtils
        Gdx.input.inputProcessor = module.stage


        module.stage.addActor(AppDebug(module.skin))
        module.stage.addActor(PaletteManager().group)
        module.stage.addActor(Tools())

        MainMenu()

        super.show()
    }


    override fun render(delta: Float) {
        val camera = module.worldCamera
        val batch = module.worldBatch
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)

//        if (keyOnce(Input.Keys.P)) {
//            res.pixmap.convertImage()
//        }
        system.update(delta)
        res.layers[sensor.paletteOffset].update(delta)

        sensor.screenMouse.set(Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
        if (outActor()) {
            sensor.preMouse.set(sensor.worldMouse)
            sensor.mouseRefresh()
            cameraControl.update(delta)
            component.update(delta)
        } else {
            cameraControl.amount = 0f
        }

        batch.projectionMatrix = camera.combined
        batch.begin()
        system.draw()
        batch.draw(res.layers[sensor.paletteOffset].region, 0f, 0f)
        batch.end()

        shapes.update()
        ui.update(delta)

        super.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        module.worldViewport.update(width, height, true)
        module.uiViewport.update(width, height, true)
        super.resize(width, height)
    }
}

