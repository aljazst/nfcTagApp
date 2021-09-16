package com.aljazs.nfcTagApp.ui.writeNfcTag

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.aljazs.nfcTagApp.Encryptor
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.extensions.TAG
import com.aljazs.nfcTagApp.extensions.extClick
import kotlinx.android.synthetic.main.fragment_write.*
import java.nio.charset.Charset

class WriteFragment : Fragment(R.layout.fragment_write) {

    private val writeViewModel: WriteViewModel by activityViewModels()

    private lateinit var encryptor: Encryptor

    companion object {
        fun newInstance(): WriteFragment {
            return WriteFragment()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        encryptor = Encryptor()

        var INIT_VECTOR = "abcdefghijkl"
        var stringtowrite = ""



        et_message.doAfterTextChanged { s -> writeViewModel.messageToSave = s.toString() }


        writeViewModel.text.observe(viewLifecycleOwner, Observer {
            tv_write.text = it
            writeViewModel.messageToSave = it
        })


        button_write.extClick {
            writeViewModel.isWriteTagOptionOn = true
            Log.i(TAG,"Write button was clicked.")
            val encryptedText = encryptor.encryptText("geslo123", writeViewModel.messageToSave,INIT_VECTOR)
            writeViewModel.messageToSave = encryptedText
        }

    }

}