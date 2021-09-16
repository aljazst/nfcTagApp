package com.aljazs.nfcTagApp.ui.writeNfcTag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WriteViewModel : ViewModel() {

    var isWriteTagOptionOn: Boolean = false
    var messageToSave: String = ""

    private val _text = MutableLiveData<String>().apply {
        value = "Please enter a text to write"
    }
    val text: LiveData<String> = _text

    val _closeDialog = MutableLiveData<Boolean>().apply {
        value = false
    }
}