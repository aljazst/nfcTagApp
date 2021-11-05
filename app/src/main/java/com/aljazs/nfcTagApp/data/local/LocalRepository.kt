package com.aljazs.nfcTagApp.data.local

import android.content.Context
import com.aljazs.nfcTagApp.data.local.sharedpreferences.SharedPreferencesContract

class LocalRepository : LocalRepositoryContract{

    private lateinit var sharedPreferences: SharedPreferencesContract


    override fun saveNumberTest(serverTime: Int) {
        sharedPreferences.saveInt("TEST", serverTime)
    }
}