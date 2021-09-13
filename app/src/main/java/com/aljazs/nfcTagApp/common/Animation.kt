package com.aljazs.nfcTagApp.common

import androidx.annotation.IntDef

object Animation {

    @IntDef(
        NONE,
        DOWN,
        UP,
        LEFT,
        RIGHT
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnimationType

    const val NONE = 0
    const val DOWN = 1
    const val UP = 2
    const val LEFT = 3
    const val RIGHT = 4
}