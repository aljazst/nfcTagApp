package com.aljazs.nfcTagApp.ui.main

import androidx.lifecycle.ViewModel
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.model.MenuNavigationItem

class MainViewModel : ViewModel() {

    fun getMenuItems(): List<MenuNavigationItem> {
        return navigationOptions
    }

    private val navigationOptions = listOf(
        MenuNavigationItem(
            title = R.string.menu_item_read,
            imageRes = R.drawable.ic_read_nfc,
            background = R.drawable.bg_button_circle_ox_blue_light
        ),
        MenuNavigationItem(
            title = R.string.menu_item_write,
            imageRes = R.drawable.ic_write_nfc,
            background = R.drawable.bg_button_circle_ox_blue_lightest
        ),
        MenuNavigationItem(
            title = R.string.menu_item_encode,
            imageRes = R.drawable.ic_lock,
            background = R.drawable.bg_button_circle_cornflower_blue
        ),
        MenuNavigationItem(
            title = R.string.menu_item_decode,
            imageRes = R.drawable.ic_settings,
            background = R.drawable.bg_button_circle_usafa_blue
        ),
        MenuNavigationItem(
            title = R.string.menu_item_settings,
            imageRes = R.drawable.ic_help,
            background = R.drawable.bg_button_circle_true_blue
        )

    )
}