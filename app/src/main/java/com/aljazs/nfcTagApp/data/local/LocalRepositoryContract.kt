package com.aljazs.nfcTagApp.data.local

interface LocalRepositoryContract {

    fun saveAlgorithmType(algorithmType: String)
    fun getAlgorithmType(): String?
}