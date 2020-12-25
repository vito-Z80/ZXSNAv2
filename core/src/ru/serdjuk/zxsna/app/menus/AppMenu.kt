package ru.serdjuk.zxsna.app.menus

import ru.serdjuk.zxsna.app.windows.popup.AppSelectionMenu

/**
 * all menus and popup menus of application
 */
@ExperimentalUnsignedTypes
class AppMenu {
    final val highlightedArea = AppSelectionMenu()
}

@ExperimentalUnsignedTypes
val appMenu = AppMenu()

