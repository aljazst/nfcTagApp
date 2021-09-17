package com.aljazs.nfcTagApp.ui.main

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag
import com.aljazs.nfcTagApp.extensions.extClick
import kotlinx.android.synthetic.main.activity_main.*
import android.nfc.*
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.provider.Settings
import android.util.Base64
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.aljazs.nfcTagApp.Decryptor
import com.aljazs.nfcTagApp.NfcUtils
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.WritableTag
import com.aljazs.nfcTagApp.common.Animation
import com.aljazs.nfcTagApp.common.Constants.INIT_VECTOR
import com.aljazs.nfcTagApp.domain.DomainMenuNavigation
import com.aljazs.nfcTagApp.extensions.extReplaceFragmentWithAnimation
import com.aljazs.nfcTagApp.model.MenuNavigationItem
import com.aljazs.nfcTagApp.model.NfcTag
import com.aljazs.nfcTagApp.ui.main.adapter.MenuNavigationAdapter
import com.aljazs.nfcTagApp.ui.readNfcTag.ReadFragment
import com.aljazs.nfcTagApp.ui.readNfcTag.ReadViewModel
import com.aljazs.nfcTagApp.ui.writeNfcTag.WriteFragment
import com.aljazs.nfcTagApp.ui.writeNfcTag.WriteViewModel
import kotlinx.android.synthetic.main.fragment_read.*
import java.lang.RuntimeException
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and

class MainActivity : AppCompatActivity() {

    private var adapter: NfcAdapter? = null
    var tag: WritableTag? = null
    var tagId: String? = null

    private val viewModel = MainViewModel()

    private lateinit var readViewModel: ReadViewModel
    private lateinit var writeViewModel: WriteViewModel


    private val menuAdapter by lazy {
        MenuNavigationAdapter { titleId ->
            when (titleId) {
                R.string.menu_item_read -> onReadSelected()
                R.string.menu_item_write -> onWriteSelected()
                R.string.menu_item_encode -> onEncodeSelected()
                R.string.menu_item_decode -> onDecodeSelected()
                R.string.menu_item_settings -> onSettingsSelected()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        writeViewModel = ViewModelProvider(this).get(WriteViewModel::class.java)
        readViewModel = ViewModelProvider(this).get(ReadViewModel::class.java)

        initNfcAdapter()

        initAdapter()
        onReadSelected()
    }

    private fun initAdapter() {

        rvMainNavigationOptions.adapter = menuAdapter
        menuAdapter.menuItems = viewModel.getMenuItems()

    }

    private fun getAndroidId() : String {

        var androidID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) // Android id is a 64-bit number as hex. hex digit = 4 bits. so we get 4x16 = 64. android_id is 16 characters long.

        return androidID.take(12) //iv needs 12
    }

    private fun onReadSelected() {

        writeViewModel?.isWriteTagOptionOn = false
        extReplaceFragmentWithAnimation(
            ReadFragment.newInstance(),
            Animation.RIGHT,
            R.id.content_container,
            addToBackStack = true,
            popBackStackInclusive = true
        )
    }

    private fun onWriteSelected() {
        extReplaceFragmentWithAnimation(
            WriteFragment.newInstance(),
            Animation.RIGHT,
            R.id.content_container,
            addToBackStack = true,
            popBackStackInclusive = true
        )
    }

    private fun onEncodeSelected() {

    }

    private fun onDecodeSelected() {

    }

    private fun onSettingsSelected() {

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

    private fun getTag() = "MainActivity"

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
        /* val NFCa : NfcA = NfcA.get(tagFromIntent) https://stackoverflow.com/questions/22719465/ntag212-mifare-ultralight-with-authentication/22723250#22723250
        https://www.tabnine.com/code/java/methods/android.nfc.tech.NfcA/transceive
          NFCa.connect() //TODO: password protect tag
          NFCa.transceive() */

        val tagSize = Ndef.get(tagFromIntent).maxSize
        // val makeReadOnly : Boolean = Ndef.get(tagFromIntent).makeReadOnly()
        val makeReadOnly = Ndef.get(tagFromIntent)
        val makeReadOnlya = tagFromIntent?.techList

        println("test1 techlist ${makeReadOnlya?.get(2)}")

        val type  = Ndef.get(tagFromIntent)
        //showToast("Tag tapped type: $makeReadOnlya")


        if (writeViewModel?.isWriteTagOptionOn) {
            val messageWrittenSuccessfully = NfcUtils.createNFCMessage(writeViewModel.messageToSave, intent)
            writeViewModel?.isWriteTagOptionOn = false
            writeViewModel?._closeDialog.value = true

            if (messageWrittenSuccessfully) {
                showToast("Message has been saved successfully")
            } else {
                showToast("Failed to save message. Please try again")
            }
        } else {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                    ?.also { rawMessages ->
                        val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                        val inNdefRecords = messages[0].records
                        val length = messages[0].byteArrayLength
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






                        readViewModel?.setTagMessage(NfcTag(inMessage1,charset.toString(),tagId,tagSize.toString()))


                    }
            }


        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}