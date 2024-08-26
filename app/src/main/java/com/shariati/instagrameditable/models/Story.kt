package com.shariati.instagrameditable.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    var date: String,
    var time: String,
    var reached: String,
    var engaged: String,
    var profileActivity: String,
    var reachedFollowers: String,
    var reachedNonFollowers: String,
    var impressions: String,
    var engagedFollowers: String,
    var engagedNonFollowers: String,
    var storyInteraction: String,
    var likes: String,
    var replies: String,
    var shares: String,
    var navigation:String,
    var forwards:String,
    var excited:String,
    var nextStory:String,
    var back:String,
    var profileVisits:String,
    var follows:String,
    var hasTaps:Boolean = false,
    var tapName:String = "",
    var tapNumber:String ="0",
    var hasLabel:Boolean = false,
    var path:String,
    ) :Parcelable