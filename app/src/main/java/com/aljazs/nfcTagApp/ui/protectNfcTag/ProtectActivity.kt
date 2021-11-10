package com.aljazs.nfcTagApp.ui.protectNfcTag

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.nfc.FormatException
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.os.Bundle
import android.os.Handler

import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.WritableTag
import com.aljazs.nfcTagApp.extensions.*
import com.example.awesomedialog.*
import kotlinx.android.synthetic.main.activity_protect.*
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.activity_write.ivBack

import java.util.*


class ProtectActivity : AppCompatActivity() {


    private var adapter: NfcAdapter? = null
    var tag: WritableTag? = null
    var tagId: String? = null

    enum class Type {
        READ_ONLY, SET_PASSWORD, REMOVE_PASSWORD,NONE;
    }

    var NFC_PROTECTION_TYPE : Type = Type.NONE


    private val protectViewModel: ProtectViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protect)

        initNfcAdapter()

        ivBack.extClick {
            finish()
        }

        clReadOnly.extClickOnce {
            NFC_PROTECTION_TYPE = Type.READ_ONLY

            var protectTagDialog = AwesomeDialog.build(this)

            protectTagDialog.title(
                    getString(R.string.dialog_read_only_title),
                    null,
                    getColor(R.color.independance)
                )
                .icon(R.drawable.ic_nfc_signal, true)
                .body(getString(R.string.dialog_read_only_sub))
                .onNegative(getString(R.string.close)) {
                    Log.d("TAG", "negative ")
                    NFC_PROTECTION_TYPE = Type.NONE}
                .position(AwesomeDialog.POSITIONS.CENTER)

            protectViewModel.protectSuccess.observe(this, Observer{
                if (it) {
                    protectTagDialog.icon(R.drawable.ic_congrts)
                        .title(getString(R.string.dialog_success_readOnly),null,
                            getColor(R.color.independance))
                        .body("")

                    Handler().postDelayed({
                        protectTagDialog.dismiss()
                    }, 2000)

                } else {
                    protectTagDialog.icon(R.drawable.ic_error)
                        .title(getString(R.string.dialog_error_readOnly),null,
                            getColor(R.color.independance))
                        .body("")

                    Handler().postDelayed({
                        protectTagDialog.dismiss()
                    }, 2000)

                }
            })
        }

        ivInfoReadOnly.extClick {
            AwesomeDialog.build(this)
                .title(
                    getString(R.string.dialog_read_only_title_info),
                    null,
                    getColor(R.color.independance)
                )
                .icon(R.drawable.ic_help_black, true)
                .body(getString(R.string.dialog_read_only_sub_info))
                .onNegative(getString(R.string.close)) {
                    Log.d("TAG", "negative ")
                }

        }

        clOtherOptions.extClickOnce {

            AwesomeDialog.build(this)
                .title(
                    getString(R.string.dialog_other_options_title),
                    null,
                    getColor(R.color.independance)
                )
                .icon(R.drawable.ic_help_black, true)
                .body(getString(R.string.dialog_other_options_sub))
                .onNegative(getString(R.string.close)) {
                    Log.d("TAG", "negative ") }
                .onPositive(getString(R.string.goto_play_store),textColor = ContextCompat.getColor(this, R.color.google_play_blue)){
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.wakdev.wdnfc")))
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps")))
                    }
                }
                .position(AwesomeDialog.POSITIONS.BOTTOM)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {


        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        //val mifare: MifareUltralight = MifareUltralight.get(tagFromIntent)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e(this.javaClass.simpleName, "Unsupported tag tapped", e)
            return
        }

        if (tagFromIntent != null ) {
            when (NFC_PROTECTION_TYPE) {
                Type.READ_ONLY -> makeReadOnly(tagFromIntent)
                //Type.SET_PASSWORD -> writePassword(mifare)
                //Type.REMOVE_PASSWORD ->  deletePassword(mifare)
                else -> extShowToast(getString(R.string.toast_text_select_protection_type))
            }
        }
    }


    private fun makeReadOnly(tagFromIntent: Tag) {
        //val mifare = MifareUltralight.get(tagFromIntent)
        val ndefTag = Ndef.get(tagFromIntent)
        try {
            //mifare.connect()
            ndefTag.connect()

            if (ndefTag.canMakeReadOnly()) {
                ndefTag.makeReadOnly()
               // extShowToast("Tag is read only now.")
                protectViewModel._protectSuccess.value = true
            }

        } catch (e: Exception) {
            println("exception e $e")
            extShowToast("Error making tag read only.")
            protectViewModel._protectSuccess.value = false

        } finally {
            try {
                //mifare.close()
                ndefTag.close()
            } catch (e: Exception) {
                println("exception e $e")

            }
        }

    }

/*
    private fun writePassword(mfc: MifareUltralight) {


        val sp = getSharedPreferences("pwd", Context.MODE_PRIVATE)
        val pwdstr = "pass"

        val pwd = Array<Byte>(4) { ((0).toByte()) }
        val temp = pwdstr?.toByteArray()
        if (temp != null) {
            for ((index, e) in temp.withIndex()) {
                pwd[index] = temp[index]
            }
        }


        mfc.connect()

        val pwd_default = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())
        val pack = byteArrayOf(0.toByte(), 0.toByte())


        try {

            //Ask with the default password first
            val response = mfc.transceive(
                byteArrayOf(
                    0x1B  //PWD_AUTH
                    , pwd_default[0], pwd_default[1], pwd_default[2], pwd_default[3]
                )
            )

            // Check if PACK is matching expected PACK
            // This is a (not that) secure method to check if tag is genuine
            if ((response != null) && (response.size >= 2)) {
                val packResponse = Arrays.copyOf(response, 2);
                if (!(pack[0] == packResponse[0] && pack[1] == packResponse[1])) {
                    Toast.makeText(
                        this@ProtectActivity,
                        "Tag could not be authenticated:\n$packResponse≠$pack",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }

            // set PACK:
            mfc.transceive(
                byteArrayOf(
                    0xA2.toByte(),
                    0x2C, /*PAGE 44*/
                    pack[0], pack[1], 0, 0  // Write PACK into first 2 Bytes and 0 in RFUI bytes
                )
            )

            // set PWD:  Set password The password set for the user
            mfc.transceive(
                byteArrayOf(
                    0xA2.toByte(),
                    0x2B,  /*PAGE 43*/
                    pwd[0],
                    pwd[1],
                    pwd[2],
                    pwd[3]  // Write PACK into first 2 Bytes and 0 in RFUI bytes
                )
            )


            // set AUTHLIM: Set a limit on the number of errors
            val responseAuthLim = mfc.readPages(42)
            if (responseAuthLim != null && responseAuthLim.size >= 16) {
                val prot =
                    false  // false = PWD_AUTH for write only, true = PWD_AUTH for read and write
                val authLim = 0;  //0-7

                mfc.transceive(
                    byteArrayOf(
                        0xA2.toByte(),
                        42,
                        (responseAuthLim[0] and 0x078 or (if (prot) 0x080.toByte() else 0x000) or ((authLim and 0x007).toByte())).toByte(),
                        responseAuthLim[1],
                        responseAuthLim[2],
                        responseAuthLim[3]

                        //Write 1-3 bits as the original data
                    )
                )
            }

            //Set Auth0 auth0 actual control whether to enable password protection
            val responseAuth0 = mfc.readPages(41)

            if (responseAuth0 != null && responseAuth0.size >= 16) {
                val prot =
                    false;  // false = PWD_AUTH for write only, true = PWD_AUTH for read and write
                val auth0 = 0;


                mfc.transceive(
                    byteArrayOf(
                        0xA2.toByte(),
                        41,
                        responseAuthLim[0],
                        responseAuthLim[1],
                        responseAuthLim[2],

                        //Write 0-2 bits as the original data
                        (auth0 and 0x0ff).toByte()
                    )
                )
            }
            protectViewModel._writeSuccess.value = true
            Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: FormatException) {
            e.printStackTrace()
        } finally {
            mfc.close()

        }


    }



    private fun deletePassword(mfc: MifareUltralight) {

        val sp = getSharedPreferences("pwd", Context.MODE_PRIVATE)

        val pwdstr = "pass"
        val pwd = Array<Byte>(4) { ((0).toByte()) }
        val temp = pwdstr?.toByteArray()
        if (temp != null) {
            for ((index, e) in temp.withIndex()) {
                pwd[index] = temp[index]
            }
        }


        //The obtained PWD is the password set by the user


        mfc.connect()

        val pwd_default = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())
        val pack = byteArrayOf(0.toByte(), 0.toByte())

        try {
            val response = mfc.transceive(
                byteArrayOf(
                    0x1B, pwd[0], pwd[1], pwd[2], pwd[3]
                )
            )

            // Check if PACK is matching expected PACK
            // This is a (not that) secure method to check if tag is genuine
            if ((response != null) && (response.size >= 2)) {
                val packResponse = Arrays.copyOf(response, 2);
                if (!(pack[0] == packResponse[0] && pack[1] == packResponse[1])) {
                    Toast.makeText(
                        this@ProtectActivity,
                        "Tag could not be authenticated:\n$packResponse≠$pack",
                        Toast.LENGTH_LONG
                    ).show();
                } else {
                    Toast.makeText(
                        this@ProtectActivity,
                        "Password verification is correct",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {

            }

            mfc.transceive(
                byteArrayOf(
                    0xA2.toByte(),
                    0x2C, /*PAGE 44*/
                    pack[0], pack[1], 0, 0  // Write PACK into first 2 Bytes and 0 in RFUI bytes
                )
            )

            mfc.transceive(
                byteArrayOf(
                    0xA2.toByte(),
                    0x2B,  /*PAGE 43*/
                    pwd_default[0],
                    pwd_default[1],
                    pwd_default[2],
                    pwd_default[3]  // Write PACK into first 2 Bytes and 0 in RFUI bytes
                )
            )

            // set AUTHLIM:
            //Set AUTHLIM (page 42, byte 0, bits 2-0) to the maximum number of failed password verification attempts
            val responseAuthLim = mfc.readPages(42)
            if (responseAuthLim != null && responseAuthLim.size >= 16) {
                val prot =
                    false  // false = PWD_AUTH for write only, true = PWD_AUTH for read and write
                val authLim = 0;  //0-7

                mfc.transceive(
                    byteArrayOf(
                        0xA2.toByte(),
                        42,
                        (responseAuthLim[0] and 0x078 or (if (prot) 0x080.toByte() else 0x000) or ((authLim and 0x007).toByte())).toByte(),
                        responseAuthLim[1],
                        responseAuthLim[2],
                        responseAuthLim[3]

                    )
                )
            }

            //Set Auth0 If auth0 is set to FF, password protection is disabled
            val responseAuth0 = mfc.readPages(41)

            if (responseAuth0 != null && responseAuth0.size >= 16) {

                mfc.transceive(
                    byteArrayOf(
                        0xA2.toByte(),
                        41,
                        responseAuthLim[0],
                        responseAuthLim[1],
                        responseAuthLim[2],

                        0x0ff.toByte()
                    )
                )
            }

            Toast.makeText(this, "Password cleared successfully", Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            e.printStackTrace()


        } catch (e: FormatException) {
            e.printStackTrace()

        } finally {
            mfc.close()

        }

    }*/

}
