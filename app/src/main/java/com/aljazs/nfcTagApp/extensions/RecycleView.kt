package com.aljazs.nfcTagApp.extensions

import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

/**
* Use this if diffableListOf() is overkill.
*/
fun <T> RecyclerView.Adapter<*>.notifiableListOf(
    initialValue: List<T> = emptyList()
) = Delegates.observable(initialValue) { _, _, _ ->
    notifyDataSetChanged()
}