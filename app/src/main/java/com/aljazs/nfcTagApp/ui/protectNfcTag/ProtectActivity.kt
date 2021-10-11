package com.aljazs.nfcTagApp.ui.protectNfcTag

import android.content.Context
import android.content.Intent
import android.nfc.FormatException
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.aljazs.nfcTagApp.Decryptor
import com.aljazs.nfcTagApp.NfcUtils
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.WritableTag
import com.aljazs.nfcTagApp.extensions.*
import com.aljazs.nfcTagApp.ui.readNfcTag.ReadViewModel
import com.example.awesomedialog.*
import kotlinx.android.synthetic.main.activity_protect.*
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.activity_write.iv_back
import java.io.IOException
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or

class ProtectActivity : AppCompatActivity() {

    private var adapter: NfcAdapter? = null
    var tag: WritableTag? = null
    var tagId: String? = null

    private val protectViewModel: ProtectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protect)

        iv_back.extClick {
            finish()
        }

        ivArrowSet.extClick {

            if (clEnterPw.visibility == View.GONE) {
                clEnterPw.extVisible()
                ivArrowSet.setImageResource(R.drawable.ic_arrow_up)
            } else {
                clEnterPw.extGone()
                ivArrowSet.setImageResource(R.drawable.ic_arrow_down)
            }
        }
        ivArrowRemove.extClick {

            if (clEnterPwRemove.visibility == View.GONE) {
                clEnterPwRemove.extVisible()
                ivArrowRemove.setImageResource(R.drawable.ic_arrow_up)
            } else {
                clEnterPwRemove.extGone()
                ivArrowRemove.setImageResource(R.drawable.ic_arrow_down)
            }
        }
        ivInfoReadOnly.extClick {
            AwesomeDialog.build(this)
                .title(
                    getString(R.string.dialog_read_only_title),
                    null,
                    getColor(R.color.independance)
                )
                .icon(R.drawable.ic_nfc_signal, true)
                .body(getString(R.string.dialog_read_only_sub))
                .onNegative(getString(R.string.close)) {
                    Log.d("TAG", "negative ")
                }

        }
        ivInfoSetPw.extClick {
            AwesomeDialog.build(this)
                .title(
                    getString(R.string.dialog_set_pw_title),
                    null,
                    getColor(R.color.independance)
                )
                .icon(R.drawable.ic_nfc_signal, true)
                .body(getString(R.string.dialog_set_pw_sub))
                .onNegative(getString(R.string.close)) {
                    Log.d("TAG", "negative ")
                }
        }
        ivInfoRemovePw.extClick {
            AwesomeDialog.build(this)
                .title(
                    getString(R.string.dialog_remove_pw_title),
                    null,
                    getColor(R.color.independance)
                )
                .icon(R.drawable.ic_nfc_signal, true)
                .body(getString(R.string.dialog_remove_pw_sub))
                .onNegative(getString(R.string.close)) {
                    Log.d("TAG", "negative ")
                }
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


        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e(this.javaClass.simpleName, "Unsupported tag tapped", e)
            return
        }

        if (tagFromIntent != null) {
            makeReadOnly(tagFromIntent)
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
            }

        } catch (e: Exception) {
            println("exception e $e")

        } finally {
            try {
                //mifare.close()
                ndefTag.close()
            } catch (e: Exception) {
                println("exception e $e")

            }
        }

    }


    private fun writePassword(mfc: MifareUltralight) {


        val sp = getSharedPreferences("pwd", Context.MODE_PRIVATE)
        val pwdstr = "pass"
        //创建默认为0的4字节数组
        val pwd = Array<Byte>(4) { ((0).toByte()) }
        val temp = pwdstr?.toByteArray()
        if (temp != null) {
            for ((index, e) in temp.withIndex()) {
                pwd[index] = temp[index]
            }
        }
        //得出的PWD即用户设置的密码


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

            Log.e("写密码完成", "写密码完成")
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
        //创建默认为0的4字节数组
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
            //用用户设置的密码询问登录
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

            //pack置为默认
            mfc.transceive(
                byteArrayOf(
                    0xA2.toByte(),
                    0x2C, /*PAGE 44*/
                    pack[0], pack[1], 0, 0  // Write PACK into first 2 Bytes and 0 in RFUI bytes
                )
            )

            //pwd置为默认
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

                        //将1-3位按原数据写会
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

                        //将0-2位按原数据写会
                        0x0ff.toByte()
                    )
                )
            }

            Toast.makeText(this, "清除密码成功", Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            e.printStackTrace()


        } catch (e: FormatException) {
            e.printStackTrace()

        } finally {
            mfc.close()

        }

    }

}
