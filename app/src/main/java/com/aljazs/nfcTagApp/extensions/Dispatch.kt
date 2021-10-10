package com.aljazs.nfcTagApp.extensions

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.util.Log
import android.widget.Toast


 fun extEnableNfcForegroundDispatch(context: Context,adapter: NfcAdapter?) {
    try {
        val intent = Intent(context, context.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val nfcPendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        adapter?.enableForegroundDispatch(context as Activity?, nfcPendingIntent, null, null)
    } catch (ex: IllegalStateException) {
        Log.e(context.javaClass.simpleName, "Error enabling NFC foreground dispatch", ex)
    }
}

 fun extDisableNfcForegroundDispatch(context: Context,adapter: NfcAdapter?) {
    try {
        adapter?.disableForegroundDispatch(context as Activity?)
    } catch (ex: IllegalStateException) {
        Log.e(context.javaClass.simpleName, "Error disabling NFC foreground dispatch", ex)
    }
}