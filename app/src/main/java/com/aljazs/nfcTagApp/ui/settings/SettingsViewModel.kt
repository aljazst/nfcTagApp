package com.aljazs.nfcTagApp.ui.settings

import androidx.lifecycle.ViewModel
import com.aljazs.nfcTagApp.data.local.LocalRepository
import com.aljazs.nfcTagApp.data.local.LocalRepositoryContract
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject

class SettingsViewModel : ViewModel(), KoinComponent {

    private val localRepository : LocalRepositoryContract by inject()

    fun saveSelectedAlgorithm(algorithmType: String) {
        localRepository.saveAlgorithmType(algorithmType)
    }

    fun getSelectedAlgorithm() :String? {
      return localRepository.getAlgorithmType()
    }

}