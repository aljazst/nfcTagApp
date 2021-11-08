package com.aljazs.nfcTagApp.ui.protectNfcTag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class ProtectViewModel : ViewModel() {



    val _writeSuccess = MutableLiveData<Boolean>().apply {

    }
    val writeSuccess: LiveData<Boolean> = _writeSuccess


}