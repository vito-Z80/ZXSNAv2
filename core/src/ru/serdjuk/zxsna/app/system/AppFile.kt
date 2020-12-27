package ru.serdjuk.zxsna.app.system

import java.io.File
import javax.swing.JFileChooser

@ExperimentalUnsignedTypes
class AppFile {
    private final val projectExtension = ".snp".intern()
    private final val emptyString = "".intern()
    private final val chooser = lazy { JFileChooser() }.value

//    fun loadPalette4bpp() {
//        lazy {
//            chooser.isMultiSelectionEnabled = false
//            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//                PaletteUtils.convertPaletteFrom(chooser.selectedFile.readBytes())
//            }
//        }.value
//    }
//
//    fun savePalette4bpp() {
//        lazy {
//            chooser.isMultiSelectionEnabled = false
//            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
//                chooser.selectedFile.writeBytes(PaletteUtils.convertPaletteTo())
//            }
//        }.value
//    }

    fun loadData():ByteArray? {
        return lazy {
            chooser.isMultiSelectionEnabled = false
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                return@lazy chooser.selectedFile.readBytes()
            } else {
                return@lazy null
            }
        }.value
    }

    fun saveData(byteArray: ByteArray) {
        lazy {
            chooser.isMultiSelectionEnabled = false
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                chooser.selectedFile.writeBytes(byteArray)
            }
        }.value
    }

    fun loadProject(): ByteArray {
        return lazy {
            chooser.isMultiSelectionEnabled = false
            if (chooser.showOpenDialog(null) == JFileChooser.FILES_ONLY) {
                chooser.selectedFile.readBytes()
            } else throw NoSuchFileException(chooser.selectedFile)
        }.value
    }

    fun saveProject(data: ByteArray, additionToFileName: String = emptyString, extension: String = projectExtension) {
        lazy {
            chooser.isMultiSelectionEnabled = false
            if (chooser.showSaveDialog(null) == JFileChooser.FILES_ONLY) {
                chooser.selectedFile.writeBytes(data)

                val name = File("${chooser.selectedFile.absolutePath}$additionToFileName$extension")
//                println(name)
                chooser.selectedFile.renameTo(name)
//                println(chooser.selectedFile.name)
            }
        }.value
    }

    fun loadImagesSequence() {
//        val imageSystem = system.import<ImageSystem>() ?: return
//        lazy {
//            val list = openSelectedFiles()
//            if (list != null) {
//                imageSystem.sequence = list
//                imageSystem.sequenceSize = imageSystem.sequence!!.size
//            } else {
//                imageSystem.sequence = null
//                imageSystem.sequenceSize = 0
//                imageSystem.sequenceCounter = 0
//            }
//        }.value
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