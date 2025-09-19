package com.karrar.movieapp.data.repository

import com.karrar.movieapp.data.DataClassParser
import com.karrar.movieapp.data.local.AppConfiguration
import com.karrar.movieapp.data.remote.response.account.AccountDto
import com.karrar.movieapp.data.remote.response.login.ErrorResponse
import com.karrar.movieapp.data.remote.service.MovieService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AccountRepositoryImp @Inject constructor(
    private val service: MovieService,
    private val appConfiguration: AppConfiguration,
    private val dataClassParser: DataClassParser,
) : AccountRepository, BaseRepository() {

    override fun getSessionId(): String? {
        return appConfiguration.getSessionId()
    }

    override fun isGuestUser(): Boolean {
        return appConfiguration.isGuestUser()
    }

    override suspend fun loginWithUserNameANdPassword(
        userName: String,
        password: String
    ): Boolean {
        return try {
            val token = getRequestToken()
            val body = mapOf<String, Any>(
                "username" to userName,
                "password" to password,
                "request_token" to token,
            ).toMap()

            val validateRequestTokenWithLogin = service.validateRequestTokenWithLogin(body)
            if (validateRequestTokenWithLogin.isSuccessful) {
                validateRequestTokenWithLogin.body()?.requestToken?.let {
                    createSession(it)
                    appConfiguration.setIsGuest(false)
                }
                true
            } else {
                val errorResponse = dataClassParser.parseFromJson(
                    validateRequestTokenWithLogin.errorBody()?.string(), ErrorResponse::class.java
                )
                throw Throwable(errorResponse.statusMessage)
            }
        } catch (e: Exception) {
            throw Throwable(e)
        }
    }

    override suspend fun loginAsGuest(): Boolean {
        if (!isGuestUser() && getAccountDetails() != null) {
            return false
        }
        appConfiguration.saveSessionId("")
        appConfiguration.setIsGuest(true)
        return true
    }

    override suspend fun logout() {
        appConfiguration.saveSessionId("")
    }

    override suspend fun getAccountDetails(): AccountDto? {
        return if (isGuestUser()) {
            null
        } else service.getAccountDetails().body()
    }

    private suspend fun getRequestToken(): String {
        val tokenResponse = service.getRequestToken()
        return tokenResponse.body()?.requestToken.toString()
    }


    private suspend fun createSession(requestToken: String) {
        val sessionResponse = service.createSession(requestToken).body()
        if (sessionResponse?.success == true) {
            saveSessionId(sessionResponse.sessionId.toString())
        }
    }

    private suspend fun saveSessionId(sessionId: String) {
        appConfiguration.saveSessionId(sessionId)
    }

    override suspend fun isDarkMode(): Flow<Boolean> =
        appConfiguration.isDarkMode()

    override suspend fun saveDarkMode(enabled: Boolean) {
        appConfiguration.saveDarkMode(enabled)
    }

    override suspend fun getLanguage():  Flow<String> =
        appConfiguration.getLanguage()

    override suspend fun saveLanguage(language: String) {
        appConfiguration.saveLanguage(language)
    }


}