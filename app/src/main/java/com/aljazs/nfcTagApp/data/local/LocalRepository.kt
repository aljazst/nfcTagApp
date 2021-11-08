package com.aljazs.nfcTagApp.data.local

import android.content.Context
import com.aljazs.nfcTagApp.common.Constants
import com.aljazs.nfcTagApp.common.Keys
import com.aljazs.nfcTagApp.data.local.sharedpreferences.SharedPreferencesContract
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject

class LocalRepository : LocalRepositoryContract, KoinComponent{

    private val context: Context by inject()
    private val sharedPreferences: SharedPreferencesContract by inject()


    override fun saveAlgorithmType(algorithmType: String) {
        sharedPreferences.saveString(Keys.KEY_ALGORITHM_TYPE, algorithmType)
    }

    override fun getAlgorithmType(): String? {
       return sharedPreferences.getString(Keys.KEY_ALGORITHM_TYPE,Constants.CIPHER_ALGORITHM)
    }
}