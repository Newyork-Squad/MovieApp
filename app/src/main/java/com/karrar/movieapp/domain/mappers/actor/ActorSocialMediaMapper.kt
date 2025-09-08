package com.karrar.movieapp.domain.mappers.actor

import com.karrar.movieapp.data.remote.response.actor.ActorSocialMediaResponse
import com.karrar.movieapp.domain.mappers.Mapper
import javax.inject.Inject

class ActorSocialMediaMapper @Inject constructor() :
    Mapper<ActorSocialMediaResponse, HashMap<String, String>> {

    override fun map(input: ActorSocialMediaResponse): HashMap<String, String> {
        val socialMediaMap = hashMapOf<String, String>()
        input.imdbId?.takeIf { it.isNotEmpty() }?.let {
            socialMediaMap[SocialMedia.IMDB.toString()] = SocialMedia.IMDB.baseUrl + it
        }
        input.facebookId?.takeIf { it.isNotEmpty() }?.let {
            socialMediaMap[SocialMedia.FACEBOOK.toString()] = SocialMedia.FACEBOOK.baseUrl + it
        }
        input.instagramId?.takeIf { it.isNotEmpty() }?.let {
            socialMediaMap[SocialMedia.INSTAGRAM.toString()] = SocialMedia.INSTAGRAM.baseUrl + it
        }
        input.twitterId?.takeIf { it.isNotEmpty() }?.let {
            socialMediaMap[SocialMedia.TWITTER.toString()] = SocialMedia.TWITTER.baseUrl + it
        }
        input.youtubeId?.takeIf { it.isNotEmpty() }?.let {
            socialMediaMap[SocialMedia.YOUTUBE.toString()] = SocialMedia.YOUTUBE.baseUrl + it
        }
        input.tiktokId?.takeIf { it.isNotEmpty() }?.let {
            socialMediaMap[SocialMedia.TIKTOK.toString()] = SocialMedia.TIKTOK.baseUrl + it
        }
        return socialMediaMap
    }

    private enum class SocialMedia(val key: String, val baseUrl: String) {
        YOUTUBE(
            "youtube_id",
            "https://www.youtube.com/channel/"
        ),
        FACEBOOK("facebook_id", "https://www.facebook.com/"),
        INSTAGRAM("instagram_id", "https://www.instagram.com/"),
        TWITTER("twitter_id", "https://twitter.com/"),
        IMDB("imdb_id", "https://www.imdb.com/name/"),
        TIKTOK("tiktok_id", "https://www.tiktok.com/@")
    }
}