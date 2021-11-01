package com.aljazs.nfcTagApp.ui.settings

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.extensions.extClick
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        iv_back.extClick {
            finish()
        }
        var message = "Miki3#99/?=";
        var generatedKey = generateKey(message,"key")
        var encodedMessage = encodeMessage(message,generatedKey)
        tv_settings.text = generatedKey
        tv_encoded.text = encodedMessage;
        tv_decoded.text = decodeMessage(encodedMessage,generatedKey)


    }

    external fun generateKey(message : String,key : String): String;
    external fun encodeMessage(message : String, key : String): String;
    external fun decodeMessage(encodedMessage : String, key : String): String;


    companion object {
        init {
            System.loadLibrary("vigenere-cipher");
        }
    }
}