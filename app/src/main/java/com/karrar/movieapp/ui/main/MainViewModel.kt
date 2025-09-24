package com.karrar.movieapp.ui.main

import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.ClearAppCacheUseCase
import com.karrar.movieapp.domain.usecases.setting.GetDarkModeUseCase
import com.karrar.movieapp.domain.usecases.setting.GetLanguageUseCase
import com.karrar.movieapp.domain.usecases.startUp.GetStartUpStateUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getStartUpStateUseCase: GetStartUpStateUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
) : BaseViewModel(){
    private val _language = MutableStateFlow(
        if (Locale.getDefault().language == "ar") "Arabic" else "English"
    )
    val language: StateFlow<String> = _language

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode

    private val _dataRefreshEvent = MutableStateFlow(false)
    val dataRefreshEvent: StateFlow<Boolean> = _dataRefreshEvent

    override fun getData() {
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

    fun refreshData() {
        viewModelScope.launch {
            getData()
            _dataRefreshEvent.value = !_dataRefreshEvent.value
        }
    }

    fun isFirstLaunch(): Boolean = getStartUpStateUseCase()

    suspend fun clearCache(language: String) {
        try {
            clearAppCacheUseCase(language)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
