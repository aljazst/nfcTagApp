package com.aljazs.nfcTagApp.ui.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.extensions.extClick
import kotlinx.android.synthetic.main.activity_write.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        ivBack.extClick {
            finish()
        }

    }
}