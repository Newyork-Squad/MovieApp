package com.karrar.movieapp.domain.usecases.login


import com.karrar.movieapp.data.repository.AccountRepository
import javax.inject.Inject

class LoginAsGuestUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): Boolean {
        return accountRepository.loginAsGuest()
    }
}