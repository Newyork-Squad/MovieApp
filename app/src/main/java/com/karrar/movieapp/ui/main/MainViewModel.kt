package com.karrar.movieapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.setting.GetDarkModeUseCase
import com.karrar.movieapp.domain.usecases.setting.GetLanguageUseCase
import com.karrar.movieapp.domain.usecases.setting.SaveDarkModeUseCase
import com.karrar.movieapp.domain.usecases.setting.SaveLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getLanguageUseCase: GetLanguageUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val saveDarkModeUseCase: SaveDarkModeUseCase
) : ViewModel() {

    private val _language = MutableStateFlow("English")
    val language: StateFlow<String> = _language

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode

    init {
        viewModelScope.launch {
            getLanguageUseCase().collect { lang ->
                _language.value = lang
            }
        }
        viewModelScope.launch {
            getDarkModeUseCase().collect { dark ->
                _darkMode.value = dark
            }
        }
    }

    fun changeLanguage(language: String) {
        viewModelScope.launch {
            saveLanguageUseCase(language)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            saveDarkModeUseCase(enabled)
        }
    }
}

