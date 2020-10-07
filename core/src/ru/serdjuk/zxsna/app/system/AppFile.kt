package ru.serdjuk.zxsna.app.system

import ru.serdjuk.zxsna.app.component.ui.palette.PaletteUtils
import java.io.File
import javax.swing.JFileChooser

@ExperimentalUnsignedTypes
class AppFile {

    private val chooser = lazy { JFileChooser() }.value

    @ExperimentalUnsignedTypes
    fun loadPalette4bpp() {
        lazy {
            chooser.isMultiSelectionEnabled = false
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                PaletteUtils.convertLoadPalette(chooser.selectedFile.readBytes())
            }
        }.value
    }

    fun savePalette4bpp() {

        lazy {
            chooser.isMultiSelectionEnabled = false
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                chooser.selectedFile.writeBytes(PaletteUtils.convertSavePalette())
            }
        }.value


    }

    fun loadImagesSequence() {
        val imageSystem = system.get<ImageSystem>() ?: return
        lazy {
            val list = openSelectedFiles()
            if (list != null) {
                imageSystem.sequence = list
                imageSystem.sequenceSize = imageSystem.sequence!!.size
            } else {
                imageSystem.sequence = null
                imageSystem.sequenceSize = 0
                imageSystem.sequenceCounter = 0
            }
        }.value
    }


    private fun openSelectedFiles(): Array<File?>? {
        chooser.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
        chooser.isMultiSelectionEnabled = true
        return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            val files = chooser.selectedFiles
            files
        } else {
            null
        }
    }


    /*
    сохранение спрайтов 16x16
    так-же должен быть сохранен номер (смещение) палитры 4bpp из 512 цветовой палитры (он-же номер слоя) + сама палитра
     */
    fun saveData(data: ByteArray, additionToFileName: String) {
        lazy {
            chooser.isMultiSelectionEnabled = false
            if (chooser.showSaveDialog(null) == JFileChooser.FILES_ONLY) {
                val newFileName = File("${chooser.selectedFile.nameWithoutExtension}$additionToFileName").absoluteFile
                //FIXME не могу дополнить имя файла при сохранении - РЕШИТЬ !!!!
//                chooser.selectedFile.renameTo(newFileName)
                chooser.selectedFile.renameTo(newFileName)
                chooser.selectedFile.writeBytes(data)
                val name = chooser.selectedFile.name
                println(name)
                println(newFileName.name)
            }
        }.value
    }

    private fun saveMultiFilesData(data: ByteArray, fileSize: Int) {
//        val filesData = data.slic
        // TODO create
        lazy {
            chooser.isMultiSelectionEnabled = false
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
//                chooser.selectedFile.writeBytes(data.)
                val name = chooser.selectedFile.nameWithoutExtension
                println(name)
            }
        }.value

    }


}

@ExperimentalUnsignedTypes
val file = AppFile()