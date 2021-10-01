package com.aljazs.nfcTagApp

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import kotlin.coroutines.coroutineContext

class  NfcUtils {
    companion object {

        fun createNFCMessage(payload: String, intent: Intent?): Boolean {
            val pathPrefix = "A"
            val nfcRecord = NdefRecord(
                NdefRecord.TNF_EXTERNAL_TYPE, pathPrefix.toByteArray(),
                ByteArray(0), payload.toByteArray()
            )
            val nfcMessage = NdefMessage(arrayOf(nfcRecord))
            intent?.let {
                val tag = it.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
                return writeMessageToTag(nfcMessage, tag)
            }
            return false
        }

        private fun writeMessageToTag(nfcMessage: NdefMessage, tag: Tag?): Boolean {
            try {
                Ndef.get(tag)?.let {
                    it.connect()
                    if (it.maxSize < nfcMessage.toByteArray().size) {
                        return false
                    }

                    return if (it.isWritable) {
                        it.writeNdefMessage(nfcMessage)
                        it.close()
                        true
                    } else false
                }

                NdefFormatable.get(tag)?.let {
                    it.connect()
                    it.format(nfcMessage)
                    it.close()
                    true

                }
                return false

            } catch (e: Exception) {
                return false
            }
        }

    }
}