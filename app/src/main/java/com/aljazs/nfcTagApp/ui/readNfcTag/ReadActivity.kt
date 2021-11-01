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
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.aljazs.nfcTagApp.*
import com.aljazs.nfcTagApp.Decryptor
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.common.Constants
import com.aljazs.nfcTagApp.extensions.*
import com.aljazs.nfcTagApp.model.NfcTag
import com.aljazs.nfcTagApp.ui.writeNfcTag.WriteActivity
import com.example.awesomedialog.*
import kotlinx.android.synthetic.main.activity_read.*
import kotlinx.android.synthetic.main.activity_read.btnDecrypt
import kotlinx.android.synthetic.main.activity_read.clTagInfo

import kotlinx.android.synthetic.main.activity_read.ivLineArrowItem
import kotlinx.android.synthetic.main.activity_read.iv_back
import kotlinx.android.synthetic.main.activity_read.tilPassword
import kotlinx.android.synthetic.main.activity_read.tvMessageData
import kotlinx.android.synthetic.main.activity_read.tvPassword
import kotlinx.android.synthetic.main.activity_read.tvTagIdData
import kotlinx.android.synthetic.main.activity_read.tvTagSizeData


import java.nio.charset.Charset
import kotlin.experimental.and

class ReadActivity : AppCompatActivity() {

    private var adapter: NfcAdapter? = null
    var tag: WritableTag? = null
    var tagId: String? = null

    var encryptedString: String = ""

    private lateinit var decryptor: Decryptor

    private val readViewModel: ReadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)

        decryptor = Decryptor()

        var readTagDialog = AwesomeDialog.build(this)
            .title(getString(R.string.dialog_tap_tag), null, getColor(R.color.independance))
            .body(getString(R.string.dialog_tap_tag_sub))
            .icon(R.drawable.ic_nfc_signal, true)
            .position(AwesomeDialog.POSITIONS.CENTER)

        var decryptedString: String = ""

        initNfcAdapter()

        iv_back.extClick {
            finish()
        }


        readViewModel.closeDialog.observe(this, {
            if (it) {
                readTagDialog.dismiss()
            }
        })

        readViewModel.tag.observe(this, Observer {
            if (it.message.isNullOrBlank()) {
                btnDecrypt.visibility = View.GONE
                tilPassword.visibility = View.GONE
                tvPassword.visibility = View.GONE
                ivLineArrowItem.visibility = View.GONE
            } else {
                tvMessageData.text = it.message
                tvReadOnlyData.text = it.isReadOnly
                tvTagIdData.text = it.tagId
                tvTagSizeData.text =
                    it.tagUsedMemory + "/" + it.tagSize.toString() + getString(R.string.message_size_bytes)

                btnDecrypt.visibility = View.VISIBLE
                tilPassword.visibility = View.VISIBLE
                //ivAsterisk.visibility = View.VISIBLE
                tvPassword.visibility = View.VISIBLE
                ivLineArrowItem.visibility = View.VISIBLE
            }

        })

        ivLineArrowItem.extClick {
            readViewModel.tag.observe(this, Observer {
                it.isExtended = !it.isExtended

                if (it.isExtended) {
                    clTagInfoExtended.extVisible()
                } else {
                    clTagInfoExtended.extGone()
                }
            })
        }

        etPasswordRead.doOnTextChanged { text, start, before, count ->
            if (text != null) {
                if (text.length >= 4) { //count options is always returning 1, bug!
                    enableButton()
                } else {
                    btnDecrypt.isEnabled = false
                }
            }
        }

        btnDecrypt.extClick {
            val decodedBytes = Base64.decode(encryptedString, Base64.NO_WRAP)

            decryptedString = decryptor.decryptData(
                etPasswordRead.text.toString(),
                decodedBytes,
                Constants.INIT_VECTOR.toByteArray()
            )
            if (decryptedString != "Exception") {
                tvMessageData.text = decryptedString
                tvMessage.text = getString(R.string.decoded_message_text)
                tvPassword.extGone()
                etPasswordRead.text?.clear()
                tilPassword.extGone()
                btnDecrypt.extGone()

            } else {
                tvMessageData.text = "Wrong pw"


            }

        }
    }

    private fun enableButton() {
        btnDecrypt.isEnabled = true
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
        extDisableNfcForegroundDispatch(this, adapter)
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {

        readViewModel?._closeDialog.value = true
        clTagInfo.extVisible()

        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e(this.javaClass.simpleName, "Unsupported tag tapped", e)
            return
        }
        tagId = tag!!.tagId
        var isReadOnly : Boolean = true

        val ndefTag = Ndef.get(tagFromIntent)

        try {
            ndefTag.connect()

            isReadOnly = ndefTag.canMakeReadOnly()

        } catch (e: Exception) {
            println("exception e $e")
            extShowToast("Error reading tag.")

        } finally {
            try {
                ndefTag.close()
            } catch (e: Exception) {
                println("exception e $e")

            }
        }
        var techlist = tagFromIntent?.techList  // android.nfc.tech.NfcA   android.nfc.tech.MifareUltralight android.nfc.tech.Ndef

        val tagSize = Ndef.get(tagFromIntent).maxSize


        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                ?.also { rawMessages ->
                    val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                    val inNdefRecords = messages[0].records
                    val usedMemory = messages[0].byteArrayLength
                    val ndefRecord_0 = inNdefRecords[0]
                    var receivedMessage = String(ndefRecord_0.payload)
                    val payloadArray: Byte = ndefRecord_0.payload[0]
                    val getType= String(ndefRecord_0.type)
                    extShowToast(getType)
                    val utfBitMask: Byte =
                        payloadArray and 0x80.toByte() // mask 7th bit that shows utf encoding https://dzone.com/articles/nfc-android-read-ndef-tag
                    val lanLength: Byte =
                        payloadArray and 0x3F.toByte() // mask bits 0 to 5 that shows the language

                    var charset: Charset = if (utfBitMask.toString() == "0")
                        Charsets.UTF_8
                    else
                        Charsets.UTF_16

                    encryptedString = receivedMessage

                    /*
                   val inMessage = String(
                        ndefRecord_0.payload,
                        lanLength + 1,
                        ndefRecord_0.payload.size - 1 - lanLength,
                        charset
                    ) */

                    //showToast("Tag tapped type: $length")

                    readViewModel?.setTagMessage(
                        NfcTag(
                            receivedMessage,
                            tagId,
                            tagSize.toString(),
                            usedMemory.toString(),
                            isReadOnly
                        )
                    )


                }
        }


    }

}