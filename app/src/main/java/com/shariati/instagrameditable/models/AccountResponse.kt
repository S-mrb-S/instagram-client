package com.shariati.instagrameditable.models

data class AccountResponse(
    val biography: String,
    val profile_picture_url:String,
    val followers_count: Int,
    val follows_count: Int,
    val id: String,
    val media_count: Int,
    val name: String,
    val username: String
)