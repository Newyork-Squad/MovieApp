package com.karrar.movieapp.ui.main

import com.karrar.movieapp.domain.usecases.startUp.GetStartUpStateUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val getStartUpStateUseCase: GetStartUpStateUseCase,
    ) : BaseViewModel() {
        private val _mainUiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())
        val mainUiState = _mainUiState.asStateFlow()

        override fun getData() {
            _mainUiState.update {
                it.copy(
                    isFirstLaunch = getStartUpStateUseCase(),
                )
            }
        }
    }
