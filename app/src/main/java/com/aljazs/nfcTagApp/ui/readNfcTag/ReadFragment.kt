package com.aljazs.nfcTagApp.ui.readNfcTag


import android.content.Intent
import android.nfc.*
import android.os.Bundle
import android.util.Base64

import android.view.View

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.aljazs.nfcTagApp.Decryptor

import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.common.Constants
import com.aljazs.nfcTagApp.extensions.extClick
import com.aljazs.nfcTagApp.extensions.extGone
import com.aljazs.nfcTagApp.extensions.extVisible
import com.aljazs.nfcTagApp.extensions.setAsterisk
import com.aljazs.nfcTagApp.model.NfcTag

import kotlinx.android.synthetic.main.fragment_read.*

class ReadFragment : Fragment(R.layout.fragment_read) {

    private val readViewModel: ReadViewModel by activityViewModels()

    private lateinit var decryptor: Decryptor

    companion object {
        fun newInstance(): ReadFragment {
            return ReadFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        decryptor = Decryptor()

        var encryptedString : String = ""
        var decryptedString : String = ""

        readViewModel.tag.observe(viewLifecycleOwner, Observer {
            if(it.message.isNullOrBlank()){
                btnDecrypt.visibility =View.GONE
                etPasswordRead.visibility = View.GONE
                ivAsterisk.visibility = View.GONE
                tvPassword.visibility = View.GONE
                ivLineArrowItem.visibility = View.GONE
            }else {
                tvMessageData.text = it.message
                encryptedString = it.message
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

        ivAsterisk.extClick {
            if(decryptedString.isNotEmpty()){
                tvMessageData.setAsterisk(decryptedString)
            }

        }

        btnDecrypt.extClick {
            val decodedBytes = Base64.decode(encryptedString, Base64.NO_WRAP)

            decryptedString =  decryptor.decryptData(etPasswordRead.text.toString(), decodedBytes, Constants.INIT_VECTOR.toByteArray())
            tvMessageData.text = decryptedString

        }
        ivLineArrowItem.extClick {
            readViewModel.tag.observe(viewLifecycleOwner, Observer {
              it.isExtended = !it.isExtended

                if(it.isExtended){
                    clTagInfoExtended.extVisible()
                }else{
                    clTagInfoExtended.extGone()
                }
            })
        }
    }


}