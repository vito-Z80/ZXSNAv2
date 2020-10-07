package ru.serdjuk.zxsna.app.system.levelEditorSystem.resources

import ru.serdjuk.zxsna.app.system.res

class LevelMapData(val width: Int, val height: Int) {

    val map = ArrayList<ArrayList<LevelMapCell>>()
    private val emptyRegion = res.atlas.findRegion("empty8x8")

    init {
        repeat(height) { y ->
            val row = ArrayList<LevelMapCell>()
            repeat(width) { x ->
                row.add(emptyCell(x))
            }
            map.add(row)
        }
    }

    private fun emptyCell(id: Int): LevelMapCell {
        return LevelMapCell(emptyRegion, id)
    }


    fun getTile(x: Int, y: Int) = map[x][y]

    fun setCompositeTiles(x: Int, y: Int, compositeTilesId: Int) {

    }


    fun setCompositeSprites(x: Int, y: Int, compositeSpritesId: Int) {

    }

    fun clearMap() {

    }

    fun drawTile(x: Int, y: Int, newCell: LevelMapCell) = map[x].set(y, newCell)
    fun fill() {

    }
}
