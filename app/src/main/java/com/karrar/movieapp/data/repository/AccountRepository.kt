package com.karrar.movieapp.data.repository

import com.karrar.movieapp.data.remote.response.account.AccountDto
import kotlinx.coroutines.flow.Flow


interface AccountRepository {

    fun getSessionId(): String?


    suspend fun loginWithUserNameANdPassword(userName: String, password: String): Boolean

    suspend fun logout()

    suspend fun getAccountDetails(): AccountDto?

    suspend fun isDarkMode(): Flow<Boolean>
    suspend fun saveDarkMode(enabled: Boolean)

    suspend fun getLanguage():  Flow<String>
    suspend fun saveLanguage(language: String)

    fun isGuestUser(): Boolean

    suspend fun loginAsGuest(): Boolean

}

