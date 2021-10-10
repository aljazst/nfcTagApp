package com.aljazs.nfcTagApp.model

/*
data class NfcTag(val message :String, val UTF: String, val tagID : String?, val tagSize: String?, val tagUsedMemory : String?, var isExtended : Boolean = false)

 */
class NfcTag(message :String, UTF: String, tagID : String?, tagSize: String?, tagUsedMemory : String?,isExtended : Boolean = false) {
    val message = message
    val utf = UTF.uppercase()
    val tagId = tagID
    val tagSize = tagSize
    val tagUsedMemory = tagUsedMemory
    var isExtended = isExtended
}
