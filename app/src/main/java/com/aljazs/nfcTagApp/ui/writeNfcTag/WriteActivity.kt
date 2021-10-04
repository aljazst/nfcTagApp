package com.aljazs.nfcTagApp.ui.writeNfcTag

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aljazs.nfcTagApp.R

class WriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /** Called when the user taps the Send button */
    fun sendMessage(view: View) {
        // Do something in response to button
    }
}