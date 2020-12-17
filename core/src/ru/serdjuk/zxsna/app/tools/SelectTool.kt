package ru.serdjuk.zxsna.app.tools

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ru.serdjuk.zxsna.app.layers.AppLayersSystem
import ru.serdjuk.zxsna.app.system.*
import ru.serdjuk.zxsna.app.tools.actors.SelectorMenuWindow
import ru.serdjuk.zxsna.app.utils.*
import kotlin.math.abs

@ExperimentalUnsignedTypes
class SelectTool : ITools() {
    private val menu = SelectorMenuWindow()
    private val positionInfo = TextField("000\n000", module.skin).also {
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
        
        // FIXME запретить выполнение если открыто меню слоя
        
        if (toolName == AppToolsSystem.usedTool) {
            select() //            showMenu()
            showMenu()
        }
        
        // remove selection
        if (keyHold(Input.Keys.CONTROL_LEFT) && keyOnce(Input.Keys.D)) {
            sensor.selectRectangle.set(0f, 0f, 0f, 0f)
            positionInfo.isVisible = false
            //            layerMenu.displayLayerMenuMethod()
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
                menu.showLayerMenu()
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
            positionInfo.text = "".intern()
            positionInfo.messageText = "W:${abs(sensor.selectRectangle.width).toInt()}\nH:${abs(sensor.selectRectangle.height).toInt()}"
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