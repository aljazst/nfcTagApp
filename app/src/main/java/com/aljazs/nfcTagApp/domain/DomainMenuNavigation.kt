package com.aljazs.nfcTagApp.domain

class DomainMenuNavigation(val id : Int, val name : String) {
    companion object {
        const val READ_TAG = 1
        const val WRITE_TAG = 2
        const val ENCRYPT_TAG = 3
        const val DECRYPT_TAG = 4
        const val SETTINGS = 5
    }
}