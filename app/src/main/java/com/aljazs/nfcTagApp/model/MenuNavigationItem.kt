package com.aljazs.nfcTagApp.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MenuNavigationItem(@StringRes val title: Int, @DrawableRes val imageRes: Int, @DrawableRes val background : Int, val isSelected : Boolean = false)