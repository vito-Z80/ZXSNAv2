package ru.serdjuk.zxsna.app.system.levelEditorSystem.resources

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ru.serdjuk.zxsna.app.component.ui.UI
import ru.serdjuk.zxsna.app.component.ui.palette.PaletteData
import ru.serdjuk.zxsna.app.component.ui.palette.PaletteUtils
import ru.serdjuk.zxsna.app.system.module
import ru.serdjuk.zxsna.app.system.res

class LevelMaps {

    // изначально массив пустой
    // апгрейд массива текущей карты каждые N секунд
    // апгрейд просчитывает новый размер карты, если он меньше предыдущего + переформируется в такой новый массив
    // идея пизданутая все встанет колом если грубануть по размеру карты

    // вариант 2

    // для каждой карты пользователем вводится ее размер в тайлах 8х8
    // в процессе заполнения карты будет возможность ее расширить по 4 направлениям, тогда будет преобразование массивов
    // + функция определяющая новый размер карты по ее крайним элементам. (функция при сохранении или кнопкой в тулзах)

    // TODO res.texture тут не должна быть. В каждом слое спрайта в редакторе должен быть выделен ректангл под пустой регион 16х16
    // TODO что то РЕШИТЬ с палитрой для тайлов (копия спрайтовой палитры/создание новой 4bpp в 16 вариациях/8bpp палитра)

    // TODO пробовать насиловать эту затею дальше: 1) добавить пустышку 16х16 в слои спрайтов с запретом на рисование в пустышке
    // TODO 2) окно создания карты
    // TODO 3) отрисовка текущей карты
    // TODO 4) добавление/удаление/вращение/ипрочпоебень тайлов на карту
    // из него так же берем 8х8 пустышку
    private val emptyRegion = let {
        val position = module.atlasUtils.insertRectangle(Color(0.1f, 0.1f, 0.1f, 1f), true, Pair(8, 8))
        val textureRegion = TextureRegion(res.texture, position.x.toInt(), position.y.toInt(), 8, 8)
        res.atlas.addRegion(UI.EMPTY_8X8, textureRegion)
        textureRegion
    }
    var currentMapId = -1

    val maps = ArrayList<LevelMapData>()

    private fun showSizerWindow() {

    }

    fun createNewMap() {
        showSizerWindow()
        addMap(0, 0)
    }

    private fun addMap(width: Int, height: Int) {
        maps.add(LevelMapData(width, height))
    }

}