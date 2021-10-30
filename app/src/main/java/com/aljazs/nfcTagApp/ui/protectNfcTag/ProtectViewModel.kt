package com.aljazs.nfcTagApp.ui.protectNfcTag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class ProtectViewModel : ViewModel() {



    val _writeSuccess = MutableLiveData<Boolean>().apply {

    }
    val writeSuccess: LiveData<Boolean> = _writeSuccess


/*    //https://developer.android.com/kotlin/coroutines/coroutines-adv#coroutinescope
    val scope = CoroutineScope(Job() + Dispatchers.Main)


    fun exampleMethod() {
        // Starts a new coroutine within the scope
        scope.launch {
            // New coroutine that can call suspend functions
            //fetchDocs()
        }
    }

    fun cleanUp() {
        // Cancel the scope to cancel ongoing coroutines work
        scope.cancel()
    }*/

}