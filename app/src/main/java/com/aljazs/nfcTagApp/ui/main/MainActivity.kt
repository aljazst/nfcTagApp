package com.aljazs.nfcTagApp.ui.main

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.nfc.*

import android.nfc.tech.Ndef
import android.provider.Settings
import androidx.lifecycle.ViewModelProvider
import com.aljazs.nfcTagApp.NfcUtils
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.WritableTag
import com.aljazs.nfcTagApp.extensions.extShowToast
import com.aljazs.nfcTagApp.model.NfcTag
import com.aljazs.nfcTagApp.ui.main.adapter.MenuNavigationAdapter
import com.aljazs.nfcTagApp.ui.readNfcTag.ReadViewModel
import com.aljazs.nfcTagApp.ui.writeNfcTag.WriteViewModel
import java.nio.charset.Charset
import kotlin.experimental.and
import android.nfc.tech.MifareUltralight
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aljazs.nfcTagApp.ui.readNfcTag.ReadActivity
import com.aljazs.nfcTagApp.ui.writeNfcTag.WriteActivity
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.experimental.or
import android.nfc.NfcAdapter
import android.view.MotionEvent
import android.view.View
import com.aljazs.nfcTagApp.extensions.extInvisible
import com.aljazs.nfcTagApp.extensions.extVisible
import com.suke.widget.SwitchButton


class MainActivity : AppCompatActivity() {

    private var adapter: NfcAdapter? = null
    var tag: WritableTag? = null
    var tagId: String? = null

    var openActionNfcSettings: Boolean =
        true //workaround for library issue: click listener not implemented https://github.com/zcweng/SwitchButton/issues/27

    private val viewModel = MainViewModel()

    private lateinit var readViewModel: ReadViewModel
    private lateinit var writeViewModel: WriteViewModel

    private lateinit var layoutManager: StaggeredGridLayoutManager

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


        switchNfc.setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener { view, isChecked ->
            if (openActionNfcSettings) {
                if (isChecked) {
                    openNfcSettings()
                }
            }
        })
    }

    private val mNfcReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) {
                openActionNfcSettings = false
                initNfcAdapter()
            }

        }
    }


    private fun initAdapter() {

        rvMainNavigationOptions.adapter = menuAdapter


        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvMainNavigationOptions.layoutManager = layoutManager
        menuAdapter.menuItems = viewModel.getMenuItems()
    }

    private fun openNfcSettings() {
        startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidId(): String {

        var androidID = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        ) // Android id is a 64-bit number as hex. hex digit = 4 bits. so we get 4x16 = 64. android_id is 16 characters long.

        return androidID.take(12) //iv needs 12
    }

    private fun onReadSelected() {

        writeViewModel?.isWriteTagOptionOn = false

        val intent = Intent(this, ReadActivity::class.java)

        startActivity(intent)
        /* extReplaceFragmentWithAnimation(
             ReadFragment.newInstance(),
             Animation.RIGHT,
             R.id.content_container,
             addToBackStack = true,
             popBackStackInclusive = true
         ) */
    }

    private fun onWriteSelected() {
        val intent = Intent(this, WriteActivity::class.java)

        startActivity(intent)
    }

    private fun onEncodeSelected() {

    }

    private fun onDecodeSelected() {

    }

    private fun onSettingsSelected() {
        switchNfc.setEnabled(true)
        switchNfc.setEnableEffect(true)
        switchNfc.isChecked = false

    }


    private fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        adapter = nfcManager.defaultAdapter

        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!nfcAdapter.isEnabled) {


            switchNfc.extVisible()
            tvNfcText.extVisible()
            switchNfc.setEnabled(true)
            switchNfc.setEnableEffect(true)
            switchNfc.isChecked = false
            openActionNfcSettings = true

        } else {
            openActionNfcSettings = false

            switchNfc.setChecked(true)
            switchNfc.setEnabled(false)
            switchNfc.setEnableEffect(false);//disable the switch animation

            switchNfc.postDelayed(Runnable {
                switchNfc.extInvisible()
                switchNfc.setEnabled(true)
                switchNfc.setEnableEffect(true)
                switchNfc.setChecked(false)
            }, 3000)


            tvNfcText.postDelayed(Runnable { tvNfcText.extInvisible() }, 3000)

        }

        if (adapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(
                this,
                "This device doesn't support NFC. Get a better phone.",
                Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }
    }


    override fun onResume() {
        super.onResume()

        registerReceiver(mNfcReceiver, IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED))

        initNfcAdapter()

        //enableNfcForegroundDispatch()
    }

    override fun onPause() {

        super.onPause()
        unregisterReceiver(mNfcReceiver)

        //disableNfcForegroundDispatch()
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
        https://stackoverflow.com/questions/33100674/setting-password-for-ntag213

          NFCa.connect() //TODO: password protect tag
          NFCa.transceive()

        val nfcA = NfcA.get(tagFromIntent)

        val result1 = nfcA.transceive(
            byteArrayOf(
                0xA2.toByte(),  /* CMD = WRITE */
                0x02.toByte(),  /* PAGE = 2    */
                0x00.toByte(), 0x00.toByte(), 0xFF.toByte(),
                0xFF.toByte() /* DATA = lock pages 3..15 */
            )
        )



        val mifare = MifareUltralight.get(tagFromIntent)

        try {
        mifare.connect()


        val pwd = byteArrayOf(
            0x70.toByte(),
            0x61.toByte(), 0x73.toByte(), 0x73.toByte()
        )
        val pack = byteArrayOf(0x98.toByte(), 0x76.toByte())

// write PACK:

// write PACK:
         mifare.transceive(
            byteArrayOf(
                0xA2.toByte(),  /* CMD = WRITE */
                0x2C.toByte(),  /* PAGE = 44 */
                pack[0], pack[1], 0, 0
            )
        )
            // write PWD:

// write PWD:
            mifare.transceive(
                byteArrayOf(
                    0xA2.toByte(),  /* CMD = WRITE */
                    0x2B.toByte(),  /* PAGE = 43 */
                    pwd[0], pwd[1], pwd[2], pwd[3]
                )
            )
        }catch (e : Exception){
            println("exception e $e")

        }finally {
            try {
                mifare.close()
            }catch (e : Exception){
                println("exception e $e")

            }
        }
*/


        val tagSize = Ndef.get(tagFromIntent).maxSize
        // val makeReadOnly : Boolean = Ndef.get(tagFromIntent).makeReadOnly()
        val makeReadOnly = Ndef.get(tagFromIntent)
        val makeReadOnlya: Array<String> = tagFromIntent?.techList as Array<String>


        val mifare: MifareUltralight = MifareUltralight.get(tagFromIntent)

        writePassword(mifare)
        try {


/*
            println("read pages ${mifare.readPages(0x2C)}")
            val pwd = byteArrayOf(
                0x70.toByte(), 0x61.toByte(), 0x73.toByte(),
                0x73.toByte()
            )
            val pack = byteArrayOf(0x98.toByte(), 0x76.toByte(),0,0)

            val pwd1 = byteArrayOf(
                0x04.toByte(), 0x00.toByte(), 0x00.toByte(),
                0xFF.toByte()
            )

           // mifare.writePage(0x29, pwd1);
var response : ByteArray

            response = mifare.transceive(
                byteArrayOf(
                    0x1B.toByte(),  // PWD_AUTH
                    pwd[0], pwd[1], pwd[2], pwd[3]
                )
            )
            var packResponse: ByteArray

            response = mifare.transceive(
                byteArrayOf(
                    0x30.toByte(),  // READ
                    0x29.toByte() // page address
                )
            )

            val auth0 = 255

            mifare.transceive(
                byteArrayOf(
                    0xA2.toByte(),  // WRITE
                    0x29.toByte(),  // page address
                    response.get(0),
                    0,
                    response.get(2),  // Keep old mirror values and write 0 in RFUI byte as stated in datasheet
                    (auth0 and 0x0ff) as Byte
                )
            )

           mifare.transceive(
                byteArrayOf(
                    0xA2.toByte(),  /* CMD = WRITE */
                    0x2C.toByte(),  /* PAGE = 44 */
                    pack[0], pack[1], 0, 0
                )
            )


            mifare.transceive(
                byteArrayOf(
                    0xA2.toByte(),  /* CMD = WRITE */
                    0x2B.toByte(),  /* PAGE = 43 */
                    pwd[0], pwd[1], pwd[2], pwd[3]
                )
            )*/
        } catch (e: Exception) {
            println("exceptionn ${e}")
        } finally {

            // makeReadOnly.close()
        }

        println("test1 techlist ${makeReadOnlya.contentToString()}")

        val type = Ndef.get(tagFromIntent)
        //showToast("Tag tapped type: $makeReadOnlya")


        if (writeViewModel?.isWriteTagOptionOn) {
            val messageWrittenSuccessfully =
                NfcUtils.createNFCMessage(writeViewModel.messageToSave, intent)
            writeViewModel?.isWriteTagOptionOn = false
           // writeViewModel?._closeDialog.value = true

            if (messageWrittenSuccessfully) {
                extShowToast("Message has been saved successfully.")
            } else {
                extShowToast("Failed to save message. Maybe the message is too long.")
            }
        } else {
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

                        readViewModel?.setTagMessage(
                            NfcTag(
                                inMessage1,
                                charset.toString(),
                                tagId,
                                tagSize.toString(),
                                usedMemory.toString()
                            )
                        )


                    }
            }


        }
    }

    //https://www.nxp.com/docs/en/data-sheet/NTAG213_215_216.pdf
    //https://github.com/yanjiepeng/TazanTagWritter/blob/master/app/src/main/java/com/tzsafe/tazantagwritter/MainActivity.kt

    //https://github.com/lionlancer/uiojklbnmz/blob/329d25a4fef0d20f82b4e33e0dd000d9037f6b00/src/android/MifarePlugin.java
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
                        this@MainActivity,
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
                        this@MainActivity,
                        "Tag could not be authenticated:\n$packResponse≠$pack",
                        Toast.LENGTH_LONG
                    ).show();
                } else {
                    Toast.makeText(
                        this@MainActivity,
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