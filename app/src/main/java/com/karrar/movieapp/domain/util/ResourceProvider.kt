package com.karrar.movieapp.domain.util

interface ResourceProvider {
    fun getString(resId: Int) : String
}