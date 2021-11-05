package com.aljazs.nfcTagApp.data.local.sharedpreferences

interface SharedPreferencesContract {

    fun saveString(key: String, message: String)
    fun getString(key: String, defaultValue: String): String?

    fun saveLong(key: String, number: Long)
    fun getLong(key: String, defaultValue: Long): Long

    fun saveInt(key: String, number: Int)
    fun getInt(key: String, defaultValue: Int): Int

    fun saveFloat(key: String, number: Float)
    fun getFloat(key: String, defaultValue: Float): Float

    fun saveBoolean(key: String, bool: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun saveDouble(key: String, double: Double)
    fun getDouble(key: String, defaultValue: Double): Double

    fun clear()
}