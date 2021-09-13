package com.aljazs.nfcTagApp.ui.main

import android.view.Menu
import androidx.lifecycle.ViewModel
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.model.MenuNavigationItem

class MainViewModel : ViewModel() {

    fun getMenuItems() : List<MenuNavigationItem> {
        return navigationOptions
    }
    private val navigationOptions = listOf(
        MenuNavigationItem(
            R.string.menu_item_read,
            R.drawable.ic_creditcard
        ),
        MenuNavigationItem(
            R.string.menu_item_write,
            R.drawable.ic_creditcard
        ),
        MenuNavigationItem(
            R.string.menu_item_encode,
            R.drawable.ic_creditcard
        ),
        MenuNavigationItem(
            R.string.menu_item_decode,
            R.drawable.ic_creditcard
        ),
        MenuNavigationItem(
            R.string.menu_item_settings,
            R.drawable.ic_creditcard
        ),
        MenuNavigationItem(
            R.string.info_recycle_send_preposition,
            R.drawable.ic_creditcard
        )
    )
}