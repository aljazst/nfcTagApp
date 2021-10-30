package com.aljazs.nfcTagApp.extensions

import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.text.toSpannable
import com.aljazs.nfcTagApp.R

fun Spannable.setColor(
    @ColorInt color: Int,
    startIdx: Int = 0,
    endIdx: Int = length,
    flags: Int = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
) = apply {
    val colorSpan = ForegroundColorSpan(color)
    setSpan(colorSpan, startIdx, endIdx, flags)
}

fun TextView.setAsterisk(message: String) {
    var asterisk = ""
    (0..message.length).forEach { _ ->
        asterisk = asterisk + "*"
    }
    text = if (message == this.text) {
        //this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, resources.getDimension(R.dimen.spacing_12))
        asterisk
    } else {
        message
    }
    //someTextView.text = asterisk
}
fun Boolean.isReadOnly() : String{
    return if (this){
        "No"
    }else{
        "Yes"
    }
}