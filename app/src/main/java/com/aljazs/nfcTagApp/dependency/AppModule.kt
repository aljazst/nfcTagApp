package com.aljazs.nfcTagApp.dependency

import android.content.Context
import android.content.SharedPreferences
import com.aljazs.nfcTagApp.common.Constants
import com.aljazs.nfcTagApp.data.local.LocalRepository
import com.aljazs.nfcTagApp.data.local.LocalRepositoryContract
import com.aljazs.nfcTagApp.data.local.sharedpreferences.SharedPreferencesContract
import com.aljazs.nfcTagApp.data.local.sharedpreferences.SharedPreferencesStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.dsl.single

val appModule = module {

    // single instance of LocalRepository
    single<LocalRepositoryContract> { LocalRepository() }

    single<SharedPreferencesContract> {SharedPreferencesStorage(get()) }

    single<SharedPreferences> { androidContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE) }

}