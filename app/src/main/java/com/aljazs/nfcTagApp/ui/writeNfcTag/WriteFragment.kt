package com.aljazs.nfcTagApp.ui.writeNfcTag

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.extensions.TAG
import com.aljazs.nfcTagApp.extensions.extClick
import kotlinx.android.synthetic.main.fragment_write.*
import java.nio.charset.Charset

class WriteFragment : Fragment(R.layout.fragment_write) {

    private val writeViewModel: WriteViewModel by activityViewModels()

    companion object {
        fun newInstance(): WriteFragment {
            return WriteFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_write.text = "THIS IS THE WRITE FRAGMENT"
        writeViewModel.isWriteTagOptionOn = true

        button_write.extClick {
            writeViewModel.isWriteTagOptionOn = true
            Log.i(TAG,"Testing")
        }

    }

}