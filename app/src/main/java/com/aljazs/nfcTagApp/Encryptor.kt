package com.aljazs.nfcTagApp


import android.os.Build
import android.security.keystore.*
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import java.security.KeyStore
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec


internal class Encryptor {

    companion object {

        private const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"

        private const val TAG = "DataEncryptorModern"
    }

    private var cipher: Cipher = Cipher.getInstance(CIPHER_ALGORITHM)

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
    lateinit var encryptedBytes: ByteArray
    private lateinit var ivValue: ByteArray

    @RequiresApi(Build.VERSION_CODES.M)
    fun encryptText(alias: String, textToEncrypt: String, ivValueID: String): String {
        return try {
            //ivValue = byteArrayOf(-32, 122, 27, 64, -76, 126, 33, -62, -81, 24, 31, 118)
            initCipher(alias, ivValueID)

            encryptedBytes = cipher.doFinal(textToEncrypt.toByteArray())
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

        } catch (ex: Exception) {
            Log.e("DataEncryptNFC", "encrypt(): $ex")
            ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCipher(alias: String, ivValueID: String) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias), GCMParameterSpec(128, ivValueID.toByteArray()))
            ivValue = ivValueID.toByteArray()

            //ivValue = cipher.iv

        } catch (invalidKeyException: KeyPermanentlyInvalidatedException) {
            Log.d(TAG, "initCipher(): Invalid Key: $invalidKeyException")
            deleteInvalidKey(alias)
        } catch (ex: Exception) {
            Log.e(TAG, "initCipher(): $ex")
        }
    }

    private fun deleteInvalidKey(alias: String) {
        keyStore.load(null)
        keyStore.deleteEntry(alias)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getSecretKey(alias: String): SecretKey {

        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)

        keyGenerator.init(
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .build()
        )

        return keyGenerator.generateKey()
    }

    fun getEncryption(): ByteArray {
        return encryptedBytes
    }


    // Initialisation Vector
    fun getIv(): ByteArray {
        return ivValue
    }

}