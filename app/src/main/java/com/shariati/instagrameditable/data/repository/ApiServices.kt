package com.shariati.instagrameditable.data.repository

import com.shariati.instagrameditable.models.AccountResponse
import com.shariati.instagrameditable.models.PostsResponse
import com.shariati.instagrameditable.models.StoriesResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {

    @GET("{app_id}")
    suspend fun getAccount(
        @Path("app_id") appId: String,
        @Query("fields") query: String,
        @Query("access_token") token: String
    ): Response<AccountResponse>

    @GET("v20.0/{app_id}/stories")
    suspend fun getStories(
        @Path("app_id") appId: String,
        @Query("fields") query: String,
        @Query("access_token") token: String
    ): Response<StoriesResponse>

    @GET("v20.0/{app_id}/media")
    suspend fun getPost(
        @Path("app_id") appId: String,
        @Query("fields") query: String,
        @Query("limit") limit: String,
        @Query("access_token") token: String
    ): Response<PostsResponse>
}