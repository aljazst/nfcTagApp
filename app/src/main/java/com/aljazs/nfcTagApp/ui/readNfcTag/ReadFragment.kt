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
        const val INTENT = "INTENT"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_text1.text = "121212"

        readViewModel.tag.observe(viewLifecycleOwner, Observer {
            tv_text.text = it.utfKey
            tv_text1.text = it.messageKey
            tv_tagId.text = it.tagIdKey
            tv_tagSize.text = it.tagSize.toString()

        })
    }


}