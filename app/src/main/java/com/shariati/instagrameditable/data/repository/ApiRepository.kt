package com.shariati.instagrameditable.data.repository

import com.shariati.instagrameditable.BuildConfig
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiService: ApiServices) {

    suspend fun getAccount() = apiService.getAccount(
        "17841453995351017",
        "name,profile_picture_url,biography,followers_count,follows_count,id,media_count,username",
        "EAATv4xD7QLMBO9HdnCUrh6X2dSpB1walMZAtt5XvhQcSQmQPnUzp5LkHW0upZC3RULeaxN12nIrkw0IsSrZAYZAvhmvAotJH7ZC4mE7GwzIUdmPkhkCBwDvjdlmWwc4yBhKQK2UVUXK929gwDFphyZAFQpywTDgVn3feJ0hrmoXGoX8G4OJvWyRouydQbzUU50"
    )

    suspend fun getStories() = apiService.getStories(
        "17841453995351017",

        "like_count,username,comments_count,media_type,timestamp,thumbnail_url,media_url,caption",
        "EAATv4xD7QLMBO9HdnCUrh6X2dSpB1walMZAtt5XvhQcSQmQPnUzp5LkHW0upZC3RULeaxN12nIrkw0IsSrZAYZAvhmvAotJH7ZC4mE7GwzIUdmPkhkCBwDvjdlmWwc4yBhKQK2UVUXK929gwDFphyZAFQpywTDgVn3feJ0hrmoXGoX8G4OJvWyRouydQbzUU50"

    )

    suspend fun getPost() = apiService.getPost(
        "17841453995351017",
        "id,username,media_url,media_type",
        "12",
        "EAATv4xD7QLMBO9HdnCUrh6X2dSpB1walMZAtt5XvhQcSQmQPnUzp5LkHW0upZC3RULeaxN12nIrkw0IsSrZAYZAvhmvAotJH7ZC4mE7GwzIUdmPkhkCBwDvjdlmWwc4yBhKQK2UVUXK929gwDFphyZAFQpywTDgVn3feJ0hrmoXGoX8G4OJvWyRouydQbzUU50"

    )
}