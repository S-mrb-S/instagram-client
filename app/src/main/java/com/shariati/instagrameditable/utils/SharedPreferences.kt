package com.shariati.instagrameditable.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE


fun setDataAccount(
    context: Context,
    username: String,
    name: String,
    biography: String,
    media_count: String,
    profile_picture_url: String,
    followers_count: String,
    follows_count: String
) {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    sharedPrefs.edit().putString("username", username).apply()
    sharedPrefs.edit().putString("name", name).apply()
    sharedPrefs.edit().putString("biography", biography).apply()
    sharedPrefs.edit().putString("media_count", media_count).apply()
    sharedPrefs.edit().putString("profile_picture_url", profile_picture_url).apply()
    sharedPrefs.edit().putString("followers_count", followers_count).apply()
    sharedPrefs.edit().putString("follows_count", follows_count).apply()
}

fun saveReachRange(context: Context, start: Int, end: Int) {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    sharedPrefs.edit().putInt("startReach", start).apply()
    sharedPrefs.edit().putInt("endReach", end).apply()
}

fun getStartReach(context: Context): Int {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    return sharedPrefs.getInt("startReach", 6000)
}

fun getEndReach(context: Context): Int {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    return sharedPrefs.getInt("endReach", 6400)
}

fun isGetData(context: Context, setData: Boolean) {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    sharedPrefs.edit().putBoolean("data", setData).apply()
}

fun getGetData(context: Context): Boolean {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    return sharedPrefs.getBoolean("data", false)
}

fun getUsername(context: Context): String {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    return sharedPrefs.getString("username", "").toString()
}

fun getName(context: Context): String {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    return sharedPrefs.getString("name", "").toString()
}

fun getBiography(context: Context): String {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    return sharedPrefs.getString("biography", "").toString()
}

fun getMediaCount(context: Context): String {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    return sharedPrefs.getString("media_count", "").toString()
}

fun getProfilePictureUrl(context: Context): String {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    return sharedPrefs.getString("profile_picture_url", "").toString()
}

fun getFollowersCount(context: Context): String {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    return sharedPrefs.getString("followers_count", "").toString()
}


fun getFollowsCount(context: Context): String {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    return sharedPrefs.getString("follows_count", "").toString()
}

fun userLogOut(context: Context) {
    val sharedPrefs = context.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
    sharedPrefs.edit().putString("id", null).apply()
    sharedPrefs.edit().putString("authToken", null).apply()
    sharedPrefs.edit().putString("token", null).apply()
    sharedPrefs.edit().putString("username", null).apply()
    sharedPrefs.edit().putString("expireAt", null).apply()
    sharedPrefs.edit().putString("email", null).apply()
    sharedPrefs.edit().putBoolean("active", true).apply()
    sharedPrefs.edit().putBoolean("login", false).apply()
}