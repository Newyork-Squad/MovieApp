package com.karrar.movieapp.data.remote.response.login

import com.google.gson.annotations.SerializedName


data class GuestSessionResponse(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("guest_session_id") val guestSessionId: String? = null,
    @SerializedName("expires_at") val expiresAt: String? = null,
    )