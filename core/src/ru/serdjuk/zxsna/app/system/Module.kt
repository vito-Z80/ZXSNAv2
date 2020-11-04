package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import ru.serdjuk.zxsna.app.utils.AtlasUtils

@ExperimentalUnsignedTypes
class Module() {
    val worldCamera = OrthographicCamera()
    val uiCamera = OrthographicCamera()
    val worldViewport = ExtendViewport(20f, 20f, worldCamera)
    val uiViewport = ScreenViewport(uiCamera)
    val worldBatch = SpriteBatch()
    val uiBatch = SpriteBatch()
    val stage = lazy { Stage(uiViewport, uiBatch) }.value
    val skin = lazy { Skin(res.atlas) }.value
    val atlasUtils = lazy { AtlasUtils(res.pixmap) }.value
    private val vui = lazy { VisUI.load(skin) }.value

}

@ExperimentalUnsignedTypes
val module = lazy { Module() }.value

