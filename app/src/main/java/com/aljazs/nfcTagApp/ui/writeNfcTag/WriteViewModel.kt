package com.aljazs.nfcTagApp.ui.writeNfcTag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aljazs.nfcTagApp.data.local.LocalRepositoryContract
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject

class WriteViewModel : ViewModel(),KoinComponent {

    private val localRepository : LocalRepositoryContract by inject()

    var isWriteTagOptionOn: Boolean = false
    var messageToSave: String = ""

    private val _text = MutableLiveData<String>().apply {
        value = "Sample message for NFC encode app."
    }
    val text: LiveData<String> = _text

    val _writeSuccess = MutableLiveData<Boolean>().apply {

    }
    val writeSuccess: LiveData<Boolean> = _writeSuccess



    fun getSelectedAlgorithm() : String? {
       return localRepository.getAlgorithmType()
    }
}