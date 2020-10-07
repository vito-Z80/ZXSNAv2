package ru.serdjuk.zxsna.app.system

import com.badlogic.gdx.utils.IntArray


interface UR
class URPointsData(val layerId: Int, val intColors: IntArray, val points: IntArray) : UR
class URFillData(val layerId: Int, val intColor: Int, val x: Int, val y: Int) : UR


