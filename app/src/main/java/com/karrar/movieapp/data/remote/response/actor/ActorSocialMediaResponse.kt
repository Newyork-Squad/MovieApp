package com.karrar.movieapp.data.remote.response.actor

import com.google.gson.annotations.SerializedName

data class ActorSocialMediaResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("freebase_mid")
    val freebaseMid: String? = null,
    @SerializedName("freebase_id")
    val freebaseId: String? = null,
    @SerializedName("imdb_id")
    val imdbId: String? = null,
    @SerializedName("tvrage_id")
    val tvrageId: Int? = null,
    @SerializedName("wikidata_id")
    val wikidataId: String? = null,
    @SerializedName("facebook_id")
    val facebookId: String? = null,
    @SerializedName("instagram_id")
    val instagramId: String? = null,
    @SerializedName("tiktok_id")
    val tiktokId: String? = null,
    @SerializedName("twitter_id")
    val twitterId: String? = null,
    @SerializedName("youtube_id")
    val youtubeId: String? = null
)