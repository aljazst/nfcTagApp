package com.aljazs.nfcTagApp.ui.writeNfcTag

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.FormatException
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.aljazs.nfcTagApp.Encryptor
import com.aljazs.nfcTagApp.NfcUtils
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.WritableTag
import com.aljazs.nfcTagApp.common.Constants
import com.aljazs.nfcTagApp.extensions.*
import com.example.awesomedialog.*
import kotlinx.android.synthetic.main.activity_write.*
import java.lang.Exception

class WriteActivity : AppCompatActivity() {


    private val writeViewModel: WriteViewModel by viewModels()

    private var adapter: NfcAdapter? = null

    var tag: WritableTag? = null

    private lateinit var encryptor: Encryptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        initNfcAdapter()

        iv_back.extClick {
            finish()
        }

        encryptor = Encryptor()

        writeViewModel.text.observe(this, Observer {
            writeViewModel.messageToSave = it
        })

        etPassword.doOnTextChanged { text, start, before, count ->
            if (text != null) {
                if(text.length >= 4){ //count options is always returning 1, bug!
                    enableButton()
                }else{
                    btnWrite.isEnabled= false
                }
            }
    }

        btnWrite.extClick {
            var writeTagDialog = AwesomeDialog.build(this)

            writeTagDialog.title(
                getString(R.string.dialog_tap_tag),null, getColor(R.color.independance))
                .body(getString(R.string.dialog_tap_tag_sub))
                .icon(R.drawable.ic_nfc_signal, true)
                .position(AwesomeDialog.POSITIONS.BOTTOM)

            writeViewModel.messageToSave = etMessage.text.toString()
            Log.i(TAG, "Write button was clicked.")
            val encryptedText = encryptor.encryptText(
                etPassword.text.toString(), writeViewModel.messageToSave,
                Constants.INIT_VECTOR
            )
            writeViewModel.messageToSave = encryptedText

            writeViewModel.writeSuccess.observe(this, Observer {
                if (it && writeViewModel.isWriteTagOptionOn) {
                    writeTagDialog.icon(R.drawable.ic_congrts)
                        .title(getString(R.string.dialog_success_write),null,
                            getColor(R.color.independance))
                        .body("")

                    Handler().postDelayed({
                        writeTagDialog.dismiss()
                        writeViewModel.isWriteTagOptionOn = false
                        etMessage.text.clear()
                        etPassword.text?.clear()
                    }, 2000)

                } else if (writeViewModel.isWriteTagOptionOn) {
                    writeTagDialog.icon(R.drawable.ic_error)
                        .title(getString(R.string.dialog_error_write),null,
                            getColor(R.color.independance))
                        .body("")

                    Handler().postDelayed({
                        writeTagDialog.dismiss()
                        writeViewModel.isWriteTagOptionOn = false
                    }, 2000)

                }
            })
        }
    }

    private fun enableButton() {
        btnWrite.isEnabled = etMessage.text.isNotBlank() && etPassword.text?.isNotBlank() == true
    }


    private fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        adapter = nfcManager.defaultAdapter

        if (adapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    override fun onResume() {
        super.onResume()
        extEnableNfcForegroundDispatch(this, adapter)
    }

    override fun onPause() {
        super.onPause()
        extDisableNfcForegroundDispatch(this, adapter)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {

        writeViewModel.isWriteTagOptionOn = true

        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e(this.javaClass.simpleName, "Unsupported tag tapped", e)
            return
        }

        //Write the data
        val messageWrittenSuccessfully = NfcUtils.createNFCMessage(writeViewModel.messageToSave, intent)

        writeViewModel._writeSuccess.value = messageWrittenSuccessfully


    }
}