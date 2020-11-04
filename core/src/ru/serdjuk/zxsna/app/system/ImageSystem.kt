package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import ru.serdjuk.zxsna.app.component.ui.palette.PaletteUtils
import ru.serdjuk.zxsna.app.utils.currentBackgroundColor
import ru.serdjuk.zxsna.app.utils.isActorExists
import java.io.File

@ExperimentalUnsignedTypes
class ImageSystem : ISystem {
    override var isVisible = false
    val backgroundColor = Color.SKY

    var sequenceCounter = 0
    var sequenceSize = 0
    var sequence: Array<File?>? = null

    var play = true
    var isLoop = false

    override fun update(delta: Float) {
        if (isVisible) {
            if (play) {
                play = false
                File(File("CnDMini.nvi").absolutePath).createNewFile()

                sequence?.forEachIndexed { index, file ->
                    PaletteUtils.convertImage(file!!.readBytes(), 256)
                    val b = PaletteUtils.readyForSaveImage
                    Gdx.files.getFileHandle(File("CnDMini.nvi").absolutePath, Files.FileType.Absolute).writeBytes(b, true)
                }
            }
        }
    }

    override fun draw() {
        if (isVisible && res.uploadImageTexture != null) {
            module.worldBatch.draw(res.uploadImageTexture, 0f, 0f)
        }
    }





//    private fun imageSequenceToNextVideoFile() {
//        if (sequenceSize == 1) {
//            PaletteUtils.convertImage(sequence!![0]?.readBytes()!!, 256)
//            val bytes = PaletteUtils.convertImageArea(Rectangle(0f, 0f, 256f, 192f), 256)
////            converted.addAll(bytes.toTypedArray())
////            File(File("CnD.nvi").absolutePath).writeBytes(converted.toByteArray())
//            resetSequence()
//            return
//        }
//
//        if (sequenceCounter < sequenceSize) {
//            PaletteUtils.convertImage(sequence!![sequenceCounter++]?.readBytes()!!, 256)
//            val bytes = PaletteUtils.convertImageArea(Rectangle(0f, 0f, 256f, 192f), 256)
////            converted.addAll(bytes.toTypedArray())
//        }
//    }

    private fun resetSequence() {
        sequence = null
        sequenceSize = 0
        sequenceCounter = 0
    }

}