package com.karrar.movieapp.data.local


import javax.inject.Inject

interface AppConfiguration {

    fun getSessionId(): String?

    suspend fun saveSessionId(value: String)

    suspend fun saveRequestDate(key: String,value: Long)

    suspend fun getRequestDate(key: String): Long?

    suspend fun setIsGuest(isGuest: Boolean)

    fun isGuestUser(): Boolean

}

class AppConfigurator @Inject constructor(private val dataStorePreferences: DataStorePreferences) :
    AppConfiguration {

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

    override suspend fun setIsGuest(isGuest: Boolean) {
        dataStorePreferences.writeBoolean(IS_GUEST_USER_KEY, isGuest)
    }

    override fun isGuestUser(): Boolean {
        return dataStorePreferences.readBoolean(IS_GUEST_USER_KEY) ?: false
    }

    companion object DataStorePreferencesKeys {
        const val SESSION_ID_KEY = "session_id"
        const val IS_GUEST_USER_KEY = "is_guest_user"

    }
}