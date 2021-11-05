package com.aljazs.nfcTagApp.data.local.sharedpreferences

import android.content.SharedPreferences

class SharedPreferencesStorage(private val sharedPreferences: SharedPreferences) :
    SharedPreferencesContract {


    override fun saveString(key: String, message: String) {
        sharedPreferences.edit().putString(key, message).apply()
    }

    override fun getString(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    override fun saveLong(key: String, number: Long) {
        sharedPreferences.edit().putLong(key, number).apply()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    override fun saveInt(key: String, number: Int) {
        sharedPreferences.edit().putInt(key, number).apply()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    override fun saveFloat(key: String, number: Float) {
        sharedPreferences.edit().putFloat(key, number).apply()
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    override fun saveBoolean(key: String, bool: Boolean) {
        sharedPreferences.edit().putBoolean(key, bool).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    override fun saveDouble(key: String, double: Double) {
        sharedPreferences.edit().putLong(key, java.lang.Double.doubleToRawLongBits(double)).apply()
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(defaultValue)))
    }
}
