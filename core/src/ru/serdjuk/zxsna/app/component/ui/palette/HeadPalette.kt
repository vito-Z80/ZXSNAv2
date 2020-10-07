package ru.serdjuk.zxsna.app.component.ui.palette

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kotcrab.vis.ui.widget.VisImage
import ru.serdjuk.zxsna.app.component.ui.DraggedPane
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res
import ru.serdjuk.zxsna.app.component.ui.UI

@ExperimentalUnsignedTypes
class HeadPalette(private val manager: PaletteManager) : DraggedPane(true) {

    private val pixmap = res.pixmap
    private val texture = res.texture

    val tWidth = PaletteData.tableWidth()
    val tHeight = 28

    val label = Label("fsgbf", module.skin)
    val items = HorizontalGroup()
    val enter = Image(module.skin.getDrawable(UI.SQUAD_ICON)).also {
        it.pack()
        it.touchable = Touchable.disabled
        it.isVisible = false
        it.color.a = 0.4f
    }

    init {

        label.touchable = Touchable.disabled
        name = javaClass.simpleName
//        val w = PaletteData.tableWidth()

//        drawable = TextureRegionDrawable(manager.bitmap.findRegion(name))

        // TODO DEBUG - FILL BITMAP - REMOVE LATER
//        texture.bitmap.insertColorAreas(
//                texture.texture,
//                true,
//                *Array<Triple<Int, Int, Color>>(100) {
//                    Triple(MathUtils.random(16, 128), MathUtils.random(16, 128), Color(
//                            MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f
//                    ))
//                }
//        )


        addListener(object : DragListener() {
            val position = Vector2()
            val offset = Vector2()

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                position.set(x, y)
                localToScreenCoordinates(position)
                offset.set(this@HeadPalette.x - position.x, this@HeadPalette.y - (Gdx.graphics.height - position.y))
                return super.touchDown(event, x, y, pointer, button)
            }


            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                position.set(x, y)
                localToScreenCoordinates(position)
                position.y = (Gdx.graphics.height - position.y) + offset.y
                position.x += offset.x
                this@HeadPalette.setPosition(position.x, position.y)
                super.touchDragged(event, x, y, pointer)
            }

        })

        pack()
        createItems()
        setPosition(200f, 200f)
    }


    @ExperimentalUnsignedTypes
    override fun act(delta: Float) {
        if (y >= Gdx.graphics.height - height) y = Gdx.graphics.height - height
//        label.setPosition(x, y + tHeight / 2)
        manager.userPaletteImage.setPosition(x, y - manager.userPaletteImage.height)
        manager.mainPaletteImage.setPosition(x - manager.mainPaletteImage.width, y - manager.mainPaletteImage.height)
        manager.overlayImage.setPosition(x+manager.userPaletteImage.actor.x, y - manager.overlayImage.height - manager.userPaletteImage.actor.y-2)
        super.act(delta)
    }


    private fun itemListener(actor: Actor, function: () -> Unit) = {
        object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                enter.isVisible = true
                enter.setPosition(actor.x + items.x, actor.y + items.y)
                enter.toFront()
                actor.color.set(Color.GREEN)
                super.enter(event, x, y, pointer, fromActor)
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                enter.isVisible = false
                actor.color.set(Color.WHITE)
                super.exit(event, x, y, pointer, toActor)
            }

            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                function.invoke()
                super.clicked(event, x, y)
            }
        }
    }.invoke()

    private fun createItems() {
        items.space(5f)
        val hideShowPalette = VisImage(module.skin.getDrawable(UI.DOUBLE_ARROW_LEFT))
        hideShowPalette.addListener(itemListener(hideShowPalette) {
            val r = (hideShowPalette.drawable as TextureRegionDrawable).region
            r.flip(true, false)
//            manager.mainTable.isVisible = r.isFlipX
        })

        items.addActor(hideShowPalette)

        val minimizeSprite = SpriteDrawable(Sprite(res.atlas.findRegion(UI.DOUBLE_ARROW_LEFT)))
        val paletteMinimize = Image(SpriteDrawable(minimizeSprite))
        minimizeSprite.sprite.rotate90(false)
        paletteMinimize.addListener(itemListener(paletteMinimize) {
            minimizeSprite.sprite.flip(true, false)
        })
        items.addActor(paletteMinimize)


//        items.addActor(VisImage(Res.data.skin.getDrawable(AppSkin.LEVITATION_CELL)))
        items.addActor(VisImage(module.skin.getDrawable(UI.SQUAD_ICON)))
        items.addActor(VisImage(module.skin.getDrawable(UI.SQUAD_ICON)))
        items.addActor(VisImage(module.skin.getDrawable(UI.CHECK_ON)))

        val dmSprite = SpriteDrawable(Sprite(res.atlas.findRegion(UI.ARROW_DOWN)))
        val upMove = Image(dmSprite)
        dmSprite.sprite.flip(false, true)
        upMove.addListener(itemListener(upMove) {
            manager.overlayImage.setRegionY(-1)
        })
        items.addActor(upMove)

        val downMove = Image(module.skin.getDrawable(UI.ARROW_DOWN))
        downMove.addListener(itemListener(downMove) {
            manager.overlayImage.setRegionY(1)
        })
        downMove.setPosition(downMove.x, downMove.y + downMove.height)
        items.addActor(downMove)



        items.addActor(label)


        items.pack()
        items.width *= 2
        content = items
        contentPack()
    }


    private fun noFun() = Unit


}