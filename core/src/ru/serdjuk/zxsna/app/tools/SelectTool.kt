package ru.serdjuk.zxsna.app.tools

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.keys.KEYS
import ru.serdjuk.zxsna.app.keys.keys
import ru.serdjuk.zxsna.app.menus.appMenu
import ru.serdjuk.zxsna.app.system.UR
import ru.serdjuk.zxsna.app.system.cursor
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.sensor
import ru.serdjuk.zxsna.app.utils.buttonHold
import ru.serdjuk.zxsna.app.utils.buttonOnce
import ru.serdjuk.zxsna.app.utils.keyHold
import ru.serdjuk.zxsna.app.utils.keyOnce
import kotlin.math.abs

@ExperimentalUnsignedTypes
class SelectTool : ITools() {
    // TODO display label (position info) be correctly
    private val positionInfo = Label("000\n000", module.skin, UI.LABEL_BN_LIGHT_BLACK).also {
        it.pack()
        it.touchable = Touchable.disabled
        it.isVisible = false
    }
    
    
    override var toolName = ToolName.SELECT
    override fun undo(data: UR?) {
    
    }
    
    init {
        module.stage.addActor(positionInfo) //        createMenu()
    }
    
    
    override fun update(delta: Float) {
        
        if (toolName == AppToolsSystem.usedTool) {
            select() //            showMenu()
            showMenu()
        }
        
        // remove selection
        if (keys.isPressed(KEYS.CLEAR_SELECTION)) {
            sensor.selectRectangle.set(0f, 0f, 0f, 0f)
            positionInfo.isVisible = false
        }
    }
    
    private fun showMenu() {
        if (buttonOnce(1)) {
            val x = if (sensor.selectRectangle.width < 0) (sensor.selectRectangle.x + sensor.selectRectangle.width).toInt()
            else sensor.selectRectangle.x.toInt()
            val y = if (sensor.selectRectangle.height < 0) (sensor.selectRectangle.y + sensor.selectRectangle.height).toInt()
            else sensor.selectRectangle.y.toInt()
            val width = abs(sensor.selectRectangle.width).toInt()
            val height = abs(sensor.selectRectangle.height).toInt()
            if (Rectangle.tmp.set(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat()).contains(sensor.worldMouse)) {
                appMenu.highlightedArea.show()
            }
        }
        
    }
    
    //    val layer = system.set<AppLayersSystem>(true)
    //    val regions = layer.getLayerSlices(sensor.selectRectangle,)
    
    
    override fun draw(batch: SpriteBatch) {
    
    }
    
    
    override fun draw(shape: ShapeRenderer) {
    
    }
    
    private var firstX = 0f
    private var firstY = 0f
    private fun select() {
        if (buttonOnce(0)) {
            firstX = sensor.worldMouse.x.toInt().toFloat()
            firstY = sensor.worldMouse.y.toInt().toFloat()
            sensor.selectRectangle.set(firstX, firstY, 0f, 0f)
            positionInfo.setPosition(sensor.screenMouseYUp.x, sensor.screenMouseYUp.y)
            positionInfo.isVisible = true
        }
        if (buttonHold(0)) {
            cursor.setSystem(Cursor.SystemCursor.Crosshair)
            positionInfo.setPosition(sensor.screenMouseYUp.x, sensor.screenMouseYUp.y)
            positionInfo.setText("W:${abs(sensor.selectRectangle.width).toInt()}\nH:${abs(sensor.selectRectangle.height).toInt()}")
            val n = 1
            
            if (sensor.worldMouse.x > sensor.selectRectangle.x) {
                sensor.selectRectangle.width = sensor.worldMouse.x.toInt().toFloat() - sensor.selectRectangle.x + n
                sensor.selectRectangle.x = firstX
            } else {
                sensor.selectRectangle.width = sensor.worldMouse.x.toInt().toFloat() - sensor.selectRectangle.x
                sensor.selectRectangle.x = firstX + n
            }
            if (sensor.worldMouse.y > sensor.selectRectangle.y) {
                sensor.selectRectangle.height = sensor.worldMouse.y.toInt().toFloat() - sensor.selectRectangle.y + n
                sensor.selectRectangle.y = firstY
            } else {
                sensor.selectRectangle.height = sensor.worldMouse.y.toInt().toFloat() - sensor.selectRectangle.y
                sensor.selectRectangle.y = firstY + n
            }
        }
    }
    
    //---------------------------------------------------------------------------
    
    
}