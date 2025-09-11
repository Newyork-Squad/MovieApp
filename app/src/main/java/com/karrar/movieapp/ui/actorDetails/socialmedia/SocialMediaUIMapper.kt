package com.karrar.movieapp.ui.actorDetails.socialmedia

import com.karrar.movieapp.domain.mappers.Mapper
import javax.inject.Inject

class SocialMediaUIMapper @Inject constructor() :
    Mapper<HashMap<String, String>, SocialMediaLinksUIState> {
    override fun map(input: HashMap<String, String>): SocialMediaLinksUIState {
        return SocialMediaLinksUIState(
            youtube = input["YOUTUBE"],
            facebook = input["FACEBOOK"],
            instagram = input["INSTAGRAM"],
            twitter = input["TWITTER"],
            tiktok = input["TIKTOK"],
        )
    }
}