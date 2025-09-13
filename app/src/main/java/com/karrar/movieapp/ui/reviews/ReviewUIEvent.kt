package com.karrar.movieapp.ui.reviews

sealed interface ReviewUIEvent {
    object ClickBackEvent : ReviewUIEvent
}