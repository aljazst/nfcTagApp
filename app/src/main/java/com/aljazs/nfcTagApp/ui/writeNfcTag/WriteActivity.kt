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
import com.aljazs.nfcTagApp.extensions.TAG
import com.aljazs.nfcTagApp.extensions.extClick
import com.aljazs.nfcTagApp.extensions.extShowToast
import kotlinx.android.synthetic.main.activity_read.*
import kotlinx.android.synthetic.main.fragment_write.*
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
        writeViewModel.closeDialog.observe(this, Observer {

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

            writeViewModel.isWriteTagOptionOn = true
            writeViewModel.messageToSave = etMessage.text.toString()
            Log.i(TAG,"Write button was clicked.")
            val encryptedText = encryptor.encryptText(etPassword.text.toString(), writeViewModel.messageToSave,
                Constants.INIT_VECTOR
            )
            writeViewModel.messageToSave = encryptedText
        }
    }

    /** Called when the user taps the Send button */
    fun sendMessage(view: View) {
        // Do something in response to button
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
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        disableNfcForegroundDispatch()
        super.onPause()
    }

    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            adapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e("getTag()", "Error enabling NFC foreground dispatch", ex)
        }
    }

    private fun disableNfcForegroundDispatch() {
        try {
            adapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e("getTag()", "Error disabling NFC foreground dispatch", ex)
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {

        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e("getTag()", "Unsupported tag tapped", e)
            return
        }
        val tagId = tag!!.tagId


        val tagSize = Ndef.get(tagFromIntent).maxSize
        // val makeReadOnly : Boolean = Ndef.get(tagFromIntent).makeReadOnly()
        val makeReadOnly = Ndef.get(tagFromIntent)
        val makeReadOnlya: Array<String> = tagFromIntent?.techList as Array<String>


        val mifare: MifareUltralight = MifareUltralight.get(tagFromIntent)



        println("test1 techlist ${makeReadOnlya.contentToString()}")

        val type = Ndef.get(tagFromIntent)
        //showToast("Tag tapped type: $makeReadOnlya")


        if (writeViewModel?.isWriteTagOptionOn) {
            val messageWrittenSuccessfully =
                NfcUtils.createNFCMessage(writeViewModel.messageToSave, intent)
            writeViewModel?.isWriteTagOptionOn = false
            writeViewModel?._closeDialog.value = true

            if (messageWrittenSuccessfully) {
                extShowToast("Message has been saved successfully.")
            } else {
                extShowToast("Failed to save message. Maybe the message is too long.")
            }
        }
    }
}