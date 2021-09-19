package com.aljazs.nfcTagApp.ui.writeNfcTag

import android.app.Dialog
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.aljazs.nfcTagApp.Encryptor
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.common.Constants.INIT_VECTOR
import com.aljazs.nfcTagApp.extensions.TAG
import com.aljazs.nfcTagApp.extensions.extClick
import kotlinx.android.synthetic.main.fragment_write.*
import java.nio.charset.Charset

class WriteFragment : Fragment(R.layout.fragment_write) {

    private val writeViewModel: WriteViewModel by activityViewModels()

    private lateinit var encryptor: Encryptor

    private lateinit var dialog: Dialog

    companion object {
        fun newInstance(): WriteFragment {
            return WriteFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            dialog = Dialog(it)
        } ?: throw IllegalStateException("Context cannot be null")

        encryptor = Encryptor()




        writeViewModel.text.observe(viewLifecycleOwner, Observer {
            writeViewModel.messageToSave = it
        })
        writeViewModel.closeDialog.observe(viewLifecycleOwner, Observer {
            if(it && dialog.isShowing == true){
                dialog.dismiss()
            }
        })

        et_message.doOnTextChanged { text, start, before, count ->

            tv_message_size_data.text = count.plus(7).toString() // add 7bytes for basic nfc data
        }


        button_write.extClick {
            showDialog()
            writeViewModel.isWriteTagOptionOn = true
            writeViewModel.messageToSave = et_message.text.toString()
            Log.i(TAG,"Write button was clicked.")
            val encryptedText = encryptor.encryptText(et_password.text.toString(), writeViewModel.messageToSave,INIT_VECTOR)
            writeViewModel.messageToSave = encryptedText


        }

    }
    private fun showDialog() {
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_nfc_write_tag)
        dialog.show()
    }

}