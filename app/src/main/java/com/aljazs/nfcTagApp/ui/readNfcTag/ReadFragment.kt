package com.aljazs.nfcTagApp.ui.readNfcTag


import android.content.Intent
import android.nfc.*
import android.os.Bundle
import android.util.Base64

import android.view.View

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.aljazs.nfcTagApp.Decryptor

import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.common.Constants
import com.aljazs.nfcTagApp.extensions.extClick

import kotlinx.android.synthetic.main.fragment_read.*

class ReadFragment : Fragment(R.layout.fragment_read) {

    private val readViewModel: ReadViewModel by activityViewModels()

    private lateinit var decryptor: Decryptor

    companion object {
        fun newInstance(): ReadFragment {
            return ReadFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        decryptor = Decryptor()

        var encryptedString : String = ""

        readViewModel.tag.observe(viewLifecycleOwner, Observer {

            tv_message_data.text = it.messageKey
            encryptedString = it.messageKey
            tv_utf_data.text = it.utfKey
            tv_tagId_data.text = it.tagIdKey
            tv_ttagSized_data.text = it.tagSize.toString()

        })

        btn_decrypt.extClick {
            val decodedBytes = Base64.decode(encryptedString, Base64.NO_WRAP)

            val string1 =  decryptor.decryptData(et_password.text.toString(), decodedBytes, Constants.INIT_VECTOR.toByteArray())
            tv_message_data.text = string1

        }
    }


}