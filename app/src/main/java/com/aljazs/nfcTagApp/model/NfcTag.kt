package com.aljazs.nfcTagApp.model

/*
class NfcTag(message :String, UTF: String, tagID : String?, tagSize: String?, tagUsedMemory : String?,isExtended : Boolean = false) {
    val messageKey = message
    val utfKey = UTF.uppercase()
    val tagIdKey = tagID
    val tagSize = tagSize
    val tagUsedMemory = tagUsedMemory
} */
data class NfcTag(val message :String, val UTF: String, val tagID : String?, val tagSize: String?, val tagUsedMemory : String?, var isExtended : Boolean = false)