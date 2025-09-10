package com.karrar.movieapp.data.local


import javax.inject.Inject

interface AppConfiguration {

    fun getSessionId(): String?

    suspend fun saveSessionId(value: String)

    suspend fun saveRequestDate(key: String,value: Long)

    suspend fun getRequestDate(key: String): Long?

    fun getStartUpState(): Boolean

    suspend fun saveStartUpState(value: Boolean)

}

class AppConfigurator @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val sharedPreferences: SharedPreferences,
    ) : AppConfiguration {

    override fun getSessionId(): String? {
        return dataStorePreferences.readString(SESSION_ID_KEY)
    }

    override suspend fun saveSessionId(value: String) {
        dataStorePreferences.writeString(SESSION_ID_KEY, value)
    }

    override suspend fun saveRequestDate(key: String, value: Long) {
        dataStorePreferences.writeLong(key, value)
    }

    override suspend fun getRequestDate(key: String): Long? {
        return dataStorePreferences.readLong(key)
    }

    override fun getStartUpState(): Boolean {
        return sharedPreferences.getBoolean(START_UP_KEY, false)
    }

    override suspend fun saveStartUpState(value: Boolean) {
        sharedPreferences.saveBoolean(START_UP_KEY, value)
    }


    companion object DataStorePreferencesKeys {
        const val SESSION_ID_KEY = "session_id"
        const val START_UP_KEY = "start_up"
    }
}