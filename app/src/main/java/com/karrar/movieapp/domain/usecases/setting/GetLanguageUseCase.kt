package com.karrar.movieapp.domain.usecases.setting

import com.karrar.movieapp.data.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLanguageUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(): Flow<String> {
        return repository.getLanguage()
    }
}