package com.aljazs.nfcTagApp.ui.main.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aljazs.nfcTagApp.extensions.notifiableListOf
import com.aljazs.nfcTagApp.model.MenuNavigationItem

class MenuNavigationAdapter(
    private val callbackItem: (Int) -> Unit
) : RecyclerView.Adapter<MenuNavigationViewHolder>() {

    var menuItems by notifiableListOf<MenuNavigationItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuNavigationViewHolder {
        return MenuNavigationViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun onBindViewHolder(holderService: MenuNavigationViewHolder, position: Int) {
        holderService.bind(menuItems[position], callbackItem)
    }
}