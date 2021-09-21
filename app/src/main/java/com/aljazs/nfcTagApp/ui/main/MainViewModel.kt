package com.aljazs.nfcTagApp.ui.main

import android.view.Menu
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
            imageRes = R.drawable.ic_nfc,
            isSelected = false
        ),
        MenuNavigationItem(
            title = R.string.menu_item_write,
            imageRes = R.drawable.ic_nfc,
            isSelected = false
        ),
        MenuNavigationItem(
            title = R.string.menu_item_encode,
            imageRes = R.drawable.ic_lock,
            isSelected = false
        ),
        MenuNavigationItem(
            title = R.string.menu_item_decode,
            imageRes = R.drawable.ic_settings,
            isSelected = false
        ),
        MenuNavigationItem(
            title = R.string.menu_item_settings,
            imageRes = R.drawable.ic_help,
            isSelected = false
        )

    )
}