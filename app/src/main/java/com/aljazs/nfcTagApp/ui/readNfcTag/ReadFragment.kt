package com.aljazs.nfcTagApp.ui.readNfcTag


import android.content.Intent
import android.nfc.*
import android.os.Bundle

import android.view.View

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import com.aljazs.nfcTagApp.R

import kotlinx.android.synthetic.main.fragment_read.*

class ReadFragment : Fragment(R.layout.fragment_read) {

    private val readViewModel: ReadViewModel by activityViewModels()

    companion object {
        fun newInstance(): ReadFragment {
            return ReadFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        readViewModel.tag.observe(viewLifecycleOwner, Observer {

            tv_message_data.text = it.messageKey
            tv_utf_data.text = it.utfKey
            tv_tagId_data.text = it.tagIdKey
            tv_ttagSized_data.text = it.tagSize.toString()

        })
    }


}