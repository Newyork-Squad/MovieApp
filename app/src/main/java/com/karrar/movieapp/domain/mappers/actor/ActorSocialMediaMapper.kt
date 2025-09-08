package com.karrar.movieapp.domain.mappers.actor

import com.karrar.movieapp.data.remote.response.actor.ActorSocialMediaResponse
import com.karrar.movieapp.domain.mappers.Mapper
import javax.inject.Inject

class ActorSocialMediaMapper @Inject constructor() :
    Mapper<ActorSocialMediaResponse, List<String>> {

    override fun map(input: ActorSocialMediaResponse): List<String> {
        val socialMediaLinks = mutableListOf<String>()
        input.imdbId?.let { socialMediaLinks.add(SocialMedia.IMDB.baseUrl + it) }
        input.facebookId?.let { socialMediaLinks.add(SocialMedia.FACEBOOK.baseUrl + it) }
        input.instagramId?.let { socialMediaLinks.add(SocialMedia.INSTAGRAM.baseUrl + it) }
        input.twitterId?.let { socialMediaLinks.add(SocialMedia.TWITTER.baseUrl + it) }
        input.youtubeId?.let { socialMediaLinks.add(SocialMedia.YOUTUBE.baseUrl + it) }
        input.tiktokId?.let { socialMediaLinks.add(SocialMedia.TIKTOK.baseUrl + it) }
        return socialMediaLinks.filter { it.isNotBlank() }
    }

    private enum class SocialMedia(val key: String, val baseUrl: String) {
        YOUTUBE(
            "youtube_id",
            "https://www.youtube.com/channel/"
        ), // Or https://www.youtube.com/user/ if it's a username
        FACEBOOK("facebook_id", "https://www.facebook.com/"),
        INSTAGRAM("instagram_id", "https://www.instagram.com/"),
        TWITTER("twitter_id", "https://twitter.com/"),
        IMDB("imdb_id", "https://www.imdb.com/name/"),
        TIKTOK("tiktok_id", "https://www.tiktok.com/@")
    }
}