package ru.serdjuk.zxsna.app.system.pack

interface Compressable {

    fun compress(): ByteArray
    fun uncompress(bytes: ByteArray)
}