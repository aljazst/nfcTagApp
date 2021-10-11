package com.aljazs.nfcTagApp


import android.util.Log
import com.aljazs.nfcTagApp.extensions.TAG
import java.security.*
import javax.crypto.*
import javax.crypto.spec.*

internal class Decryptor {


    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val TAG_LENGTH = 128
    }

    private var keyStore: KeyStore? = null

    init {
        initKeyStore()
    }

    private fun initKeyStore() {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore!!.load(null)
        Log.i(TAG,"Number of keys ${keyStore!!.size()}")
        Log.i(TAG,"Does key exist ${keyStore!!.containsAlias("geslo1")}")
    }

    fun decryptData(alias: String, encryptedData: ByteArray, encryptionIv: ByteArray): String {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(TAG_LENGTH, encryptionIv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec)

            return String(cipher.doFinal(encryptedData))
        }catch (ex: Exception) {
            Log.e("DataEncryptNFC", "decrypt(): $ex")
            return "Exception"
        }
    }

    private fun getSecretKey(alias: String): SecretKey {
        return (keyStore!!.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
    }

}