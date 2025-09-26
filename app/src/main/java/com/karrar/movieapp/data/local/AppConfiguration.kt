package com.karrar.movieapp.data.local


import android.content.res.Configuration
import android.content.res.Resources
import com.karrar.movieapp.ml.StrengthLevel
import com.karrar.movieapp.utilities.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

interface AppConfiguration {

    fun getSessionId(): String?

    suspend fun saveSessionId(value: String)

    suspend fun saveRequestDate(key: String, value: Long)

    suspend fun getRequestDate(key: String): Long?
    suspend fun saveDarkMode(enabled: Boolean)
    fun isDarkMode(): Flow<Boolean>

    suspend fun saveLanguage(language: String)
    fun getLanguageCodeFlow(): Flow<String?>

    fun getStartUpState(): Boolean

    suspend fun saveStartUpState(value: Boolean)

    suspend fun setIsGuest(isGuest: Boolean)

    fun isGuestUser(): Boolean
    suspend fun clearRequestDates()
    suspend fun saveContentPreference(level: StrengthLevel)
    fun getContentPreference(): Flow<StrengthLevel>

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

    override suspend fun saveDarkMode(enabled: Boolean) {
        dataStorePreferences.writeBoolean(DARK_MODE_KEY, enabled)
    }

    override fun isDarkMode(): Flow<Boolean> =
        dataStorePreferences.readBooleanFlow(DARK_MODE_KEY)
            .map { savedMode ->
                savedMode ?: isSystemInDarkMode()
            }

    override fun getLanguageCodeFlow(): Flow<String?> =
        dataStorePreferences.readStringFlow(LANGUAGE_KEY)
            .map { savedLanguage ->
                savedLanguage
            }

    override suspend fun setIsGuest(isGuest: Boolean) {
        dataStorePreferences.writeBoolean(IS_GUEST_USER_KEY, isGuest)
    }

    override fun isGuestUser(): Boolean {
        return dataStorePreferences.readBoolean(IS_GUEST_USER_KEY) ?: false
    }

    override suspend fun saveLanguage(language: String) {
        dataStorePreferences.writeString(LANGUAGE_KEY, language)
    }

    override fun getStartUpState(): Boolean {
        return sharedPreferences.getBoolean(START_UP_KEY, true)
    }

    override suspend fun saveStartUpState(value: Boolean) {
        sharedPreferences.saveBoolean(START_UP_KEY, value)
    }

    override suspend fun saveContentPreference(level: StrengthLevel) {
        dataStorePreferences.writeString(CONTENT_PREFERENCE_KEY, level.name)
    }

    override fun getContentPreference(): Flow<StrengthLevel> =
        dataStorePreferences.readStringFlow(CONTENT_PREFERENCE_KEY)
            .map { it?.let { name -> StrengthLevel.valueOf(name) } ?: StrengthLevel.HIDE_EXPLICIT }

    override suspend fun clearRequestDates() {
        dataStorePreferences.remove(Constants.POPULAR_MOVIE_REQUEST_DATE_KEY)
        dataStorePreferences.remove(Constants.TRENDING_MOVIE_REQUEST_DATE_KEY)
        dataStorePreferences.remove(Constants.UPCOMING_MOVIE_REQUEST_DATE_KEY)
        dataStorePreferences.remove(Constants.ADVENTURE_MOVIE_REQUEST_DATE_KEY)
        dataStorePreferences.remove(Constants.MYSTERY_MOVIE_REQUEST_DATE_KEY)
        dataStorePreferences.remove(Constants.NOW_STREAMING_MOVIE_REQUEST_DATE_KEY)
        dataStorePreferences.remove(Constants.AIRING_TODAY_SERIES_REQUEST_DATE_KEY)
        dataStorePreferences.remove(Constants.ON_THE_AIR_SERIES_REQUEST_DATE_KEY)
        dataStorePreferences.remove(Constants.TOP_RATED_SERIES_REQUEST_DATE_KEY)
        dataStorePreferences.remove(Constants.ACTOR_REQUEST_DATE_KEY)
    }

    private fun isSystemInDarkMode(): Boolean {
        val currentNightMode =
            Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    companion object Keys {
        const val SESSION_ID_KEY = "session_id"
        const val IS_GUEST_USER_KEY = "is_guest_user"
        const val CONTENT_PREFERENCE_KEY = "content_preference"

        const val START_UP_KEY = "isFirstLaunch"
        const val DARK_MODE_KEY = "dark_mode"
        const val LANGUAGE_KEY = "language"
    }
}