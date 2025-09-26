package com.karrar.movieapp.data.repository

import com.karrar.movieapp.data.remote.response.account.AccountDto
import kotlinx.coroutines.flow.Flow


interface AccountRepository {

    fun getSessionId(): String?


    suspend fun loginWithUserNameANdPassword(userName: String, password: String): Boolean

    suspend fun logout()

    suspend fun getAccountDetails(): AccountDto?

    fun isGuestUser(): Boolean

    suspend fun loginAsGuest(): Boolean

}

