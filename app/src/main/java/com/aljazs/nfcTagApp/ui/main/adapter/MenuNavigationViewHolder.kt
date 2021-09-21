package com.aljazs.nfcTagApp.ui.main.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.extensions.extClick
import com.aljazs.nfcTagApp.extensions.extInflate
import com.aljazs.nfcTagApp.model.MenuNavigationItem
import kotlinx.android.synthetic.main.item_rv_menu_navigation.view.*

class MenuNavigationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(menuItem: MenuNavigationItem, callbackItem: (Int) -> Unit) = with(itemView) {
        ivShortcutIcon.setImageResource(menuItem.imageRes)
        tvShortcutTitle.setText(menuItem.title)

        extClick {
            callbackItem(menuItem.title)
            isSelected = !isSelected

            if (isSelected) {
                ivShortcutIcon.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_button_circle_blue);
            }else{
                ivShortcutIcon.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_button_circle_white);
            }

        }
    }

    companion object {
        fun create(parent: ViewGroup): MenuNavigationViewHolder {
            val view = parent.extInflate(R.layout.item_rv_menu_navigation)
            return MenuNavigationViewHolder(view)
        }
    }
}