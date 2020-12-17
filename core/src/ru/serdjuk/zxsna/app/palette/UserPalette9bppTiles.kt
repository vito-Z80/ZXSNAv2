package ru.serdjuk.zxsna.app.palette

@ExperimentalUnsignedTypes
open class UserPalette9bppTiles : UserPalette(false) {


    // TODO всетаки придеться переписать для 9бпп палитры б ольше чем одну функцию !!!

    override fun paletteOffsetRange(cellId: Int) = (cellId > -1 && cellId < storage.size)


    override fun collectData(): ByteArray {
        return super.collectData()
    }

}