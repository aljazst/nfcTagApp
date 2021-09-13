package com.aljazs.nfcTagApp.model

class NfcTag(message :String, UTF: String) {
    val messageKey = message.uppercase()
    val utfKey = UTF.uppercase()
}