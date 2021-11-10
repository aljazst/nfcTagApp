package com.aljazs.nfcTagApp


import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.util.Log
import java.util.*
import kotlin.coroutines.coroutineContext

class WritableTag @Throws(FormatException::class) constructor(tag: Tag) {
    private val NDEF = Ndef::class.java.canonicalName
    private val NDEF_FORMATABLE = NdefFormatable::class.java.canonicalName

    private val ndef: Ndef?
    private val ndefFormatable: NdefFormatable?

    val tagId: String?
        get() {
            if (ndef != null) {
                return bytesToHexString(ndef.tag.id)
            } else if (ndefFormatable != null) {
                return bytesToHexString(ndefFormatable.tag.id)
            }
            return null
        }

    init {
        val technologies = tag.techList
        val tagTechs = Arrays.asList(*technologies)
        when {
            tagTechs.contains(NDEF) -> {
                Log.i("WritableTag", "contains ndef")
                ndef = Ndef.get(tag)
                ndefFormatable = null
            }
            tagTechs.contains(NDEF_FORMATABLE) -> {
                Log.i("WritableTag", "contains ndef_formatable")
                ndefFormatable = NdefFormatable.get(tag)
                ndef = null
            }
            else -> {
                throw FormatException("Tag doesn't support ndef")
            }
        }
    }


    companion object {
        fun bytesToHexString(src: ByteArray): String? {
            val sb = StringBuilder()
            for (b in src) {
                sb.append(String.format("%02X", b))
            }
            return sb.toString()
        }
    }
}