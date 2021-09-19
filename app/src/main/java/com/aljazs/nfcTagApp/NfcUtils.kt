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

class  NfcUtils {
    companion object {
        private val mimeType: String = ""


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
                    if (it.maxSize < nfcMessage.toByteArray().size) return false

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


        fun getData(rawMsgs: Array<Parcelable>): String {
            val msgs = arrayOfNulls<NdefMessage>(rawMsgs.size)
            for (i in rawMsgs.indices) {
                msgs[i] = rawMsgs[i] as NdefMessage
            }

            val records = msgs[0]!!.records

            var recordData = ""

            for (record in records) {
                recordData += record.toString() + "\n"
            }

            return recordData
        }

        fun getIntentFilters(): Array<IntentFilter> {
            val ndefFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
            try {
                ndefFilter.addDataType("application/vnd.com.tickets")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                Log.e("NfcUtils", "Problem in parsing mime type for nfc reading", e)
            }

            return arrayOf(ndefFilter)
        }

        fun prepareMessageToWrite(tagData: String, context: Context): NdefMessage {
            val message: NdefMessage
            val typeBytes = mimeType.toByteArray()
            val payload = tagData.toByteArray()
            val record1 = NdefRecord(NdefRecord.TNF_MIME_MEDIA, typeBytes, null, payload)
            val record2 = NdefRecord.createApplicationRecord(context.packageName)
            message = NdefMessage(arrayOf(record1, record2))
            return message
        }
    }
}