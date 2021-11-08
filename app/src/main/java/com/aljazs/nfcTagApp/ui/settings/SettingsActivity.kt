package com.aljazs.nfcTagApp.ui.settings

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.common.Constants
import com.aljazs.nfcTagApp.extensions.extClick
import com.aljazs.nfcTagApp.extensions.extShowToast
import com.aljazs.nfcTagApp.ui.writeNfcTag.WriteViewModel
import com.example.awesomedialog.*
import com.suke.widget.SwitchButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {


    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ivBack.extClick {
            finish()
        }
        if(settingsViewModel.getSelectedAlgorithm() == Constants.CIPHER_ALGORITHM){
            switchCipher.isChecked = true
            switchEcrypt.isChecked = false
        }else{
            switchEcrypt.isChecked = true
            switchCipher.isChecked = false
        }

        ivCipherInfo.extClick {
            AwesomeDialog.build(this)
                .title(
                    getString(R.string.dialog_cipher_info_title),
                    null,
                    getColor(R.color.independance)
                )
                .icon(R.drawable.ic_help_black, true)
                .body(getString(R.string.dialog_cipher_sub_info))
                .onNegative(getString(R.string.close)) {
                    Log.d("TAG", "negative ")
                }
        }

        ivEnryptInfo.extClick {
            AwesomeDialog.build(this)
                .title(
                    getString(R.string.dialog_ecrypt_info_title),
                    null,
                    getColor(R.color.independance)
                )
                .icon(R.drawable.ic_help_black, true)
                .body(getString(R.string.dialog_ecrypt_sub_info))
                .onNegative(getString(R.string.close)) {
                    Log.d("TAG", "negative ")
                }
        }

        switchCipher.setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener { view, isChecked ->
            switchEcrypt.isChecked = !isChecked
            if(isChecked){
                settingsViewModel.saveSelectedAlgorithm(Constants.CIPHER_ALGORITHM)
            }

        })
        switchEcrypt.setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener { view, isChecked ->
            switchCipher.isChecked = !isChecked
            if(isChecked){
                settingsViewModel.saveSelectedAlgorithm(Constants.ENCRYPTION_ALGORITHM)
            }
        })

    }
}