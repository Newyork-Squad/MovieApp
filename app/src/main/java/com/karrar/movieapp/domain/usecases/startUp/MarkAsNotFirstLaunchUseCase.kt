package com.karrar.movieapp.domain.usecases.startUp

import com.karrar.movieapp.data.local.AppConfiguration
import javax.inject.Inject

class MarkAsNotFirstLaunchUseCase
    @Inject
    constructor(
        private val appConfiguration: AppConfiguration,
    ) {
        suspend operator fun invoke() = appConfiguration.saveStartUpState(false)
    }
