package com.aljazs.nfcTagApp.ui.main


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.nfc.*
import android.provider.Settings
import androidx.lifecycle.ViewModelProvider
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.WritableTag
import com.aljazs.nfcTagApp.ui.main.adapter.MenuNavigationAdapter
import com.aljazs.nfcTagApp.ui.readNfcTag.ReadViewModel
import com.aljazs.nfcTagApp.ui.writeNfcTag.WriteViewModel
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aljazs.nfcTagApp.ui.readNfcTag.ReadActivity
import com.aljazs.nfcTagApp.ui.writeNfcTag.WriteActivity
import android.nfc.NfcAdapter
import com.aljazs.nfcTagApp.extensions.extInvisible
import com.aljazs.nfcTagApp.extensions.extVisible
import com.aljazs.nfcTagApp.ui.about.AboutActivity
import com.aljazs.nfcTagApp.ui.protectNfcTag.ProtectActivity
import com.aljazs.nfcTagApp.ui.settings.SettingsActivity
import com.suke.widget.SwitchButton


class MainActivity : AppCompatActivity() {

    private var adapter: NfcAdapter? = null
    var tag: WritableTag? = null

    var openActionNfcSettings: Boolean = true //workaround for library issue: click listener not implemented https://github.com/zcweng/SwitchButton/issues/27

    private val viewModel = MainViewModel()

    private lateinit var readViewModel: ReadViewModel
    private lateinit var writeViewModel: WriteViewModel

    private lateinit var layoutManager: StaggeredGridLayoutManager

    private val menuAdapter by lazy {
        MenuNavigationAdapter { titleId ->
            when (titleId) {
                R.string.menu_item_read -> onReadSelected()
                R.string.menu_item_write -> onWriteSelected()
                R.string.menu_item_encode -> onProtectSelected()
                R.string.menu_item_decode -> onSettingsSelected()
                R.string.menu_item_settings -> onAboutAppSelected()
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

    private fun onProtectSelected() {
        val intent = Intent(this, ProtectActivity::class.java)

        startActivity(intent)
    }

    private fun onAboutAppSelected() {
        val intent = Intent(this, AboutActivity::class.java)

        startActivity(intent)
    }

    private fun onSettingsSelected() {

        val intent = Intent(this, SettingsActivity::class.java)

        startActivity(intent)
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


    //https://www.nxp.com/docs/en/data-sheet/NTAG213_215_216.pdf
    //https://github.com/yanjiepeng/TazanTagWritter/blob/master/app/src/main/java/com/tzsafe/tazantagwritter/MainActivity.kt

    //https://github.com/lionlancer/uiojklbnmz/blob/329d25a4fef0d20f82b4e33e0dd000d9037f6b00/src/android/MifarePlugin.java


}