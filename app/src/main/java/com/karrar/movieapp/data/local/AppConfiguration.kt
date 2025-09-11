package com.karrar.movieapp.data.local


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AppConfiguration {

    fun getSessionId(): String?

    suspend fun saveSessionId(value: String)

    suspend fun saveRequestDate(key: String, value: Long)

    suspend fun getRequestDate(key: String): Long?
    suspend fun saveDarkMode(enabled: Boolean)
    fun isDarkMode(): Flow<Boolean>

    suspend fun saveLanguage(language: String)
    fun getLanguage(): Flow<String>

    fun getStartUpState(): Boolean

    suspend fun saveStartUpState(value: Boolean)

    suspend fun setIsGuest(isGuest: Boolean)

    fun isGuestUser(): Boolean

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

    override fun isDarkMode(): Flow<Boolean> =
        dataStorePreferences.readBooleanFlow(DARK_MODE_KEY)

    override fun getLanguage(): Flow<String> =
        dataStorePreferences.readStringFlow(LANGUAGE_KEY)
            .map { it ?: "English" }

    override suspend fun setIsGuest(isGuest: Boolean) {
        dataStorePreferences.writeBoolean(IS_GUEST_USER_KEY, isGuest)
    }

    override suspend fun saveDarkMode(enabled: Boolean) {
        dataStorePreferences.writeBoolean(DARK_MODE_KEY, enabled)
    }

    override fun isGuestUser(): Boolean {
        return dataStorePreferences.readBoolean(IS_GUEST_USER_KEY) ?: false
    }

    override suspend fun saveLanguage(language: String) {
        dataStorePreferences.writeString(LANGUAGE_KEY, language)
    }

    override fun getStartUpState(): Boolean {
        return sharedPreferences.getBoolean(START_UP_KEY, false)
    }

    override suspend fun saveStartUpState(value: Boolean) {
        sharedPreferences.saveBoolean(START_UP_KEY, value)
    }


    companion object Keys {
        const val SESSION_ID_KEY = "session_id"
        const val IS_GUEST_USER_KEY = "is_guest_user"

        const val START_UP_KEY = "start_up"
        const val DARK_MODE_KEY = "dark_mode"
        const val LANGUAGE_KEY = "language"
    }
}