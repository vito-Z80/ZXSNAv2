package ru.serdjuk.zxsna.app.system.tiles

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.StretchViewport
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.utils.RectInt
import ru.serdjuk.zxsna.app.utils.keyOnce
import kotlin.math.sin


@ExperimentalUnsignedTypes
class AppSheets : Window(" Sheets", module.skin) {

    private val movedSprite = AppSprite(res.atlas.findRegion(UI.SELECTOR)).also {
        it.isVisible = false
    }
    private val selectedSprite = AppSprite(res.atlas.findRegion(UI.SELECTOR)).also {
        it.isVisible = false
        it.setColor(0f, 1f, 0f, 1f)
    }

    private var selectedSpriteId = -1 // ID выбранного спрайта
    private var cursorSpriteId = -1 // ID спрайта над которым находится курсор

    private var time = 0f

    // indent from window edges
    val borderBounds = res.atlas.findRegion(UI.APP_WINDOW).splits.let {
        RectInt(it[0], it[1], it[2], it[3])
    }
    val indentWidth = borderBounds.x + borderBounds.y
    val indentHeight = borderBounds.width + borderBounds.height
    val unitSize = 32f
    val bufferWidth = 1024f
    val bufferHeight = 1024f

    private final val MAX_COLUMNS = 16
    private final val MAX_ROWS = 8

    private val emptySprite = res.atlas.findRegion(UI.BORDER)
    val storage = Array<AppSprite>(128) {
        AppSprite(emptySprite).also {spr->
            spr.flip(false, true)
            spr.setBounds(0f, 0f, unitSize, unitSize)
        }
    }

    val viewport = StretchViewport(bufferWidth, bufferHeight, OrthographicCamera())


    private val frameBufferBuilder = GLFrameBuffer.FrameBufferBuilder(bufferWidth.toInt(), bufferHeight.toInt())
            .also {
                it.addColorTextureAttachment(GL30.GL_RGBA, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE)
            }
    val buffer = frameBufferBuilder.build()

    val region = TextureRegion().also {
        it.setRegion(TextureRegion(buffer.colorBufferTexture))
//        it.flip(false, true)
    }

    val containerRegion = TextureRegion(region.texture, 0, 0, 256, 256)
    val containerSprite = Sprite(containerRegion).also { it.setBounds(0f, 0f, 256f, 256f) }
    val containerImage = Image()

    init {


        isResizable = true
        setResizeBorder(12)
        setPosition(300f, 300f)

        // FIXME убрать все флипы в классе - построить хранилище слевасверху

        containerImage.drawable = SpriteDrawable(containerSprite)
        containerImage.align = Align.topLeft
        add(containerImage).align(Align.topLeft).grow()
        pack()
        addListener(listener())


    }


    private fun setBounds() {
        if (width < unitSize + indentWidth) {
            setSize(unitSize + indentWidth, height)
        }
        if (width > indentWidth + unitSize * MAX_COLUMNS) {
            setSize(indentWidth + unitSize * MAX_COLUMNS, height)
        }

        if (height < indentHeight + unitSize) {
            setSize(width, indentHeight + unitSize)
        }

        if (height > indentHeight + unitSize * MAX_ROWS) {
            setSize(width, indentHeight + unitSize * MAX_ROWS)
        }
    }


    override fun act(delta: Float) {

        time += delta
        movedSprite.color.a = (sin(time * 3) + 1f) / 2f

        addSprite()
        setBounds()
        containerImage.setBounds(borderBounds.x.toFloat(), borderBounds.y.toFloat(), width - indentWidth, height - indentHeight)
        containerSprite.setRegion(0, 0, children[0].width.toInt(), height.toInt() - children[0].height.toInt())
//        containerSprite.flip(false, true)

        super.act(delta)
    }


    /**
     * заполнение контейнера спрайтами относительно размера контерйнера
     * спрайты не вошедшие в контейнер не должны печататься
     */
    private fun fillContainer(batch: Batch?) {

        var x = 0f
        var y = 0f
        storage.forEachIndexed { id, sprite ->
            if (x + unitSize < borderBounds.x + containerImage.width || id == 0) {
                sprite.setPosition(x, y)
                x += unitSize
            } else {
                x = 0f
                y += unitSize
                sprite.setPosition(x, y)
                x += unitSize
                if (y > containerSprite.height) return
            }
            sprite.draw(batch)
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {


        buffer.begin()
        batch?.projectionMatrix = viewport.camera.combined

//        applyTransform(batch,computeTransform())

        Gdx.gl20.glClearColor(0.4f, 0.5f, 0.43f, 1f);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fillContainer(batch)

        if (movedSprite.isVisible) {
            movedSprite.draw(batch)
        }

        if (selectedSprite.isVisible) {
            selectedSprite.draw(batch)
        }


        batch?.flush()      //  FIXME смена текстуры = лишний drawCall

        buffer.end()


        // вернуть проекцию UI
        batch?.projectionMatrix = module.uiViewport.camera.combined
//        batch?.draw(region, 0f, 0f)   // debug
//        batch?.flush()      //  FIXME смена текстуры = лишний drawCall
        super.draw(batch, parentAlpha)

    }


    val newSprite = {
        val id = MathUtils.random(res.atlas.regions.size - 1)
        AppSprite(res.atlas.regions[id]).also { s ->
            s.flip(false, true)
            s.setBounds(0f, 0f, unitSize, unitSize)
        }
    }

    fun addSprite() {
        if (keyOnce(Input.Keys.S)) {
            storage[MathUtils.random(MAX_ROWS * MAX_COLUMNS - 1)] = newSprite.invoke()
        }
    }




    private fun listener() = object : InputListener() {


        override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
            // FIXME не прогонять весь массив, определять начало и конец массива только из видимых спрайтов
            storage.forEachIndexed { id, sprite ->
                if (sprite.boundingRectangle.contains(x - borderBounds.x, y - borderBounds.height)) {
                    movedSprite.setBounds(sprite.x, sprite.y, sprite.width, sprite.height)
                    movedSprite.isVisible = true
                    cursorSpriteId = id

                    return super.mouseMoved(event, x, y)
                }
            }
            movedSprite.isVisible = false
            return super.mouseMoved(event, x, y)
        }

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {

            storage.forEachIndexed { id, sprite ->
                if (sprite.boundingRectangle.contains(x - borderBounds.x, y - borderBounds.height)) {
                    selectedSpriteId = id
                    selectedSprite.isVisible = true
                    selectedSprite.setBounds(sprite.x, sprite.y, sprite.width, sprite.height)
                    return super.touchDown(event, x, y, pointer, button)
                }
            }
            return super.touchDown(event, x, y, pointer, button)
        }

    }


}


//@ExperimentalUnsignedTypes
//val appSheets = AppSheets()