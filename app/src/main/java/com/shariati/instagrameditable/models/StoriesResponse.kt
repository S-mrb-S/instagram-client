package com.shariati.instagrameditable.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class StoriesResponse(
    val `data`: List<Data>
) {
    @Parcelize
    data class Data(
        val comments_count: Int,
        val id: String,
        val like_count: Int,
        var thumbnail_url: String,
        val media_type: String,
        var media_url: String,
        val username: String,
        val caption: String? = null,
        var timestamp: String,
        var story: Story
    ) : Parcelable

    @Parcelize
    data class Story(
        var id: String,
        val date: String,
        val time: String,
        val reached: String,
        val engaged: String,
        val profileActivity: String,
        val reachedFollowers: String,
        val reachedNonFollowers: String,
        val impressions: String,
        val engagedFollowers: String,
        val engagedNonFollowers: String,
        val storyInteraction: String,
        val likes: String,
        val replies: String,
        val shares: String,
        val navigation: String,
        val forwards: String,
        val excited: String,
        val nextStory: String,
        val back: String,
        val profileVisits: String,
        val follows: String,
        val hasTaps: Boolean = false,
        val tapName: String = "",
        val tapNumber: String = "0",
        val hasLabel: Boolean = false,
        val path: String,
        var showDate: Boolean,
    ) : Parcelable
}