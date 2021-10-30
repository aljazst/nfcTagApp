package com.aljazs.nfcTagApp.model

import com.aljazs.nfcTagApp.extensions.isReadOnly

/*
data class NfcTag(val message :String, val UTF: String, val tagID : String?, val tagSize: String?, val tagUsedMemory : String?, var isExtended : Boolean = false)

 */
class NfcTag(message :String, tagID : String?, tagSize: String?, tagUsedMemory : String?,isReadOnly : Boolean, isExtended : Boolean = false) {
    val message = message
    //val utf = UTF.uppercase()
    val tagId = tagID
    val tagSize = tagSize
    val tagUsedMemory = tagUsedMemory
    var isExtended = isExtended
    var isReadOnly = isReadOnly.isReadOnly()
}
