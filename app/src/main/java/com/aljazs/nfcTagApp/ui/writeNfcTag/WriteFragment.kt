package com.aljazs.nfcTagApp.ui.writeNfcTag

import android.app.Dialog
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.fragment_read.*
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

        etMessage.doOnTextChanged { text, start, before, count ->

            tvMessageSizeData.text = count.plus(7).toString() // add 7bytes for basic nfc data
            if(count >= 1){
                tvWritePasswordTitle.visibility =View.VISIBLE
                etPassword.visibility =View.VISIBLE
            }else{
                tvWritePasswordTitle.visibility = View.GONE
                etPassword.visibility =View.GONE
            }
        }
        etPassword.doOnTextChanged { text, start, before, count ->
            if(count >= 1){
                btnWrite.visibility =View.VISIBLE
            }else{
                btnWrite.visibility = View.GONE
            }

        }

        btnWrite.extClick {
            showDialog()
            writeViewModel.isWriteTagOptionOn = true
            writeViewModel.messageToSave = etMessage.text.toString()
            Log.i(TAG,"Write button was clicked.")
            val encryptedText = encryptor.encryptText(etPassword.text.toString(), writeViewModel.messageToSave,INIT_VECTOR)
            writeViewModel.messageToSave = encryptedText
        }

    }
    private fun showDialog() {
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_nfc_write_tag)
        dialog.show()

    }
    fun showKeyboard() =
        (context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as? InputMethodManager)!!
            .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

    fun hideKeyboard() =
        (context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as? InputMethodManager)!!
            .hideSoftInputFromWindow(view?.getWindowToken(), 0)

}