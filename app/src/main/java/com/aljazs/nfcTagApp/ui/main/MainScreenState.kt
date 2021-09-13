package com.aljazs.nfcTagApp.ui.main

import com.aljazs.nfcTagApp.model.MenuNavigationItem

sealed class MainScreenState {

    class ShowMainItems(val item: List<MenuNavigationItem>) : MainScreenState()
}