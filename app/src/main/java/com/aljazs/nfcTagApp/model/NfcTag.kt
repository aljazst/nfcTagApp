package com.aljazs.nfcTagApp.model

class NfcTag(message :String, UTF: String, tagID : String?, tagSize: Int) {
    val messageKey = message
    val utfKey = UTF.uppercase()
    val tagIdKey = tagID
    val tagSize = tagSize
}