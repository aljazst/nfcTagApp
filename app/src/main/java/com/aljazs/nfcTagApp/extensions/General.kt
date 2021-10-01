package com.aljazs.nfcTagApp.extensions

import android.content.Context
import android.widget.Toast

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun Context.extShowToast(message: String, duration: Int = Toast.LENGTH_LONG){
    Toast.makeText(this, message , duration).show()
}