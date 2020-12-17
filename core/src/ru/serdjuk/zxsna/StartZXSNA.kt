package ru.serdjuk.zxsna

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL30
import ru.serdjuk.zxsna.app.component.cameraControl
import ru.serdjuk.zxsna.app.palette.*
import ru.serdjuk.zxsna.app.component.ui.ui
import ru.serdjuk.zxsna.app.windows.UserSheets
import ru.serdjuk.zxsna.app.windows.MainMenu
import ru.serdjuk.zxsna.app.component.world.shapes
import ru.serdjuk.zxsna.app.system.*
import ru.serdjuk.zxsna.app.system.tiles.appSheets
import ru.serdjuk.zxsna.app.utils.*
import ru.serdjuk.zxsna.app.windows.AppToolTip
import kotlin.system.measureTimeMillis

@ExperimentalUnsignedTypes
class StartZXSNA : ScreenAdapter() {
    
    private val camera = module.worldCamera
    private val batch = module.worldBatch
    private val color = currentBackgroundColor
    
    
    companion object {
        var appTime = 0L
    }
    
    override fun show() {
        
        ui.install()
//        PaletteUtils
        Gdx.input.inputProcessor = module.stage
        
        
        MainMenu()
        
        UserPalette.addPalette<UserPalette4bppSprites>("4BPP Sprite palette", UserPalette4bppSprites::class.java)
        UserPalette.addPalette<UserPalette4bppTiles>("4BPP Tile palette", UserPalette4bppTiles::class.java)
//        UserPalette.addPalette<UserPalette9bppTiles>("9BPP Tile palette", UserPalette9bppTiles::class.java)
//        UserPalette.addPalette<UserPalette9bppSprites>("9BPP Sprite palette", UserPalette9bppSprites::class.java)
        
        
        SheetsTest()
        
        UserSheets()
        
        module.stage.addActor(AppToolTip())
        
        super.show()
        
    }
    
    
    override fun render(delta: Float) {
        
        val d = 1f / 60f
        appTime = measureTimeMillis {
            
            
            Gdx.gl.glClearColor(color.r, color.g, color.b, color.a)
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)
            
            History.update()
            sensor.screenMouseYUp.set(Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
            sensor.screenMouseYDown.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            if (outActor()) {
                sensor.preMouse.set(sensor.worldMouse)
                sensor.mouseRefresh()
                cameraControl.update(d)
                system.update(d)
            } else {
                cameraControl.amount = 0f
            }
            batch.projectionMatrix = camera.combined
            batch.begin()
            system.draw()
            
            batch.draw(res.texture, 0f, 0f)
            batch.end()
            
            shapes.update()
            ui.update(d)
            
            super.render(d)
        }
        
    }
    
    override fun resize(width: Int, height: Int) {
        module.worldViewport.update(width, height, true)
        module.uiViewport.update(width, height, true)
        appSheets.viewport.update(width, height, true)
        sensor.resizeBounds()
        super.resize(width, height)
    }
}

