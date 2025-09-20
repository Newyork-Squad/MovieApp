package com.karrar.movieapp.data.remote.response
import com.google.gson.annotations.SerializedName

typealias CollectionMediaTypeString = String?


data class CollectionDto(
    @SerializedName("id")
    val id: Int? = 0,
    val name: String,
    val description: String,
    @SerializedName("item_count")
    val itemsCount: Int = 0,
    @SerializedName("list_type")
    val type: CollectionMediaTypeString = "movie"

)