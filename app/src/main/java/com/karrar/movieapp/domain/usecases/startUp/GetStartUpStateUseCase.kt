package com.karrar.movieapp.domain.usecases.startUp

import com.karrar.movieapp.data.local.AppConfiguration
import javax.inject.Inject

class GetStartUpStateUseCase
    @Inject
    constructor(
        private val appConfiguration: AppConfiguration,
    ) {
        operator fun invoke() = appConfiguration.getStartUpState()
    }