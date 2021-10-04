package com.aljazs.nfcTagApp.ui.readNfcTag

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.*
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.aljazs.nfcTagApp.NfcUtils
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.WritableTag
import com.aljazs.nfcTagApp.extensions.extClick
import com.aljazs.nfcTagApp.extensions.extGone
import com.aljazs.nfcTagApp.extensions.extShowToast
import com.aljazs.nfcTagApp.extensions.extVisible
import com.aljazs.nfcTagApp.model.NfcTag
import kotlinx.android.synthetic.main.activity_read.*
import kotlinx.android.synthetic.main.activity_read.btnDecrypt
import kotlinx.android.synthetic.main.activity_read.etPasswordRead
import kotlinx.android.synthetic.main.activity_read.ivAsterisk
import kotlinx.android.synthetic.main.activity_read.ivLineArrowItem
import kotlinx.android.synthetic.main.activity_read.tvMessageData
import kotlinx.android.synthetic.main.activity_read.tvPassword
import kotlinx.android.synthetic.main.activity_read.tvTagIdData
import kotlinx.android.synthetic.main.activity_read.tvTagSizeData
import kotlinx.android.synthetic.main.activity_read.tvUtfData
import kotlinx.android.synthetic.main.fragment_read.*
import java.lang.Exception
import java.nio.charset.Charset
import kotlin.experimental.and

class ReadActivity : AppCompatActivity() {

    private var adapter: NfcAdapter? = null
    var tag: WritableTag? = null
    var tagId: String? = null

    private fun getTag() = "ReadActivity"

    private val readViewModel: ReadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)


        initNfcAdapter()

        iv_back.extClick {
            finish()
        }

        readViewModel.tag.observe(this, Observer {
            if(it.message.isNullOrBlank()){
                btnDecrypt.visibility =View.GONE
                etPasswordRead.visibility = View.GONE
                ivAsterisk.visibility = View.GONE
                tvPassword.visibility = View.GONE
                ivLineArrowItem.visibility = View.GONE
            }else {
                tvMessageData.text = it.message
                //encryptedString = it.message
                tvUtfData.text = it.UTF
                tvTagIdData.text = it.tagID
                tvTagSizeData.text = it.tagUsedMemory + "/" + it.tagSize.toString() + getString(R.string.message_size_bytes)

                btnDecrypt.visibility =View.VISIBLE
                etPasswordRead.visibility = View.VISIBLE
                ivAsterisk.visibility = View.VISIBLE
                tvPassword.visibility = View.VISIBLE
                ivLineArrowItem.visibility = View.VISIBLE
            }

        })

        ivLineArrowItem.extClick {
            readViewModel.tag.observe(this, Observer {
                it.isExtended = !it.isExtended

                if(it.isExtended){
                    clTagInfoExtended.extVisible()
                }else{
                    clTagInfoExtended.extGone()
                }
            })
        }


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

    /** Called when the user taps the Send button */
    fun sendMessage(view: View) {
        // Do something in response to button
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
            Log.e(getTag(), "Error enabling NFC foreground dispatch", ex)
        }
    }

    private fun disableNfcForegroundDispatch() {
        try {
            adapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e(getTag(), "Error disabling NFC foreground dispatch", ex)
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
            Log.e(getTag(), "Unsupported tag tapped", e)
            return
        }
        tagId = tag!!.tagId

       var miki = tagFromIntent?.techList  // android.nfc.tech.NfcA   android.nfc.tech.MifareUltralight android.nfc.tech.Ndef



        val tagSize = Ndef.get(tagFromIntent).maxSize
        val type  = Ndef.get(tagFromIntent)
        extShowToast("Tag tapped type: $miki")


      /*  if (writeViewModel?.isWriteTagOptionOn) {
            val messageWrittenSuccessfully = NfcUtils.createNFCMessage(writeViewModel.messageToSave, intent)
            writeViewModel?.isWriteTagOptionOn = false
            writeViewModel?._closeDialog.value = true

            if (messageWrittenSuccessfully) {
                extShowToast("Message has been saved successfully.")
            } else {
                extShowToast("Failed to save message. Maybe the message is too long.")
            }
        } else { */
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                    ?.also { rawMessages ->
                        val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                        val inNdefRecords = messages[0].records
                        val usedMemory = messages[0].byteArrayLength
                        val ndefRecord_0 = inNdefRecords[0]
                        var inMessage1 = String(ndefRecord_0.payload)
                        val payloadArray: Byte = ndefRecord_0.payload[0]
                        val utfBitMask: Byte =
                            payloadArray and 0x80.toByte() // mask 7th bit that shows utf encoding https://dzone.com/articles/nfc-android-read-ndef-tag
                        val lanLength: Byte =
                            payloadArray and 0x3F.toByte() // mask bits 0 to 5 that shows the language

                        var charset: Charset = if (utfBitMask.toString() == "0")
                            Charsets.UTF_8
                        else
                            Charsets.UTF_16
                        /*
                       val inMessage = String(
                            ndefRecord_0.payload,
                            lanLength + 1,
                            ndefRecord_0.payload.size - 1 - lanLength,
                            charset
                        ) */

                        //showToast("Tag tapped type: $length")

                        readViewModel?.setTagMessage(NfcTag(inMessage1,charset.toString(),tagId,tagSize.toString(),usedMemory.toString()))


                    }
            }


        }

}