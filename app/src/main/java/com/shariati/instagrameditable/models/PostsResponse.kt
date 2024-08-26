package com.shariati.instagrameditable.models

data class PostsResponse(
    val `data`: List<Data>,
    val paging: Paging
) {
    data class Data(
        val id: String,
        val media_type: String,
        val media_url: String,
        val username: String
    )

    data class Paging(
        val cursors: Cursors,
        val next: String
    ) {
        data class Cursors(
            val after: String,
            val before: String
        )
    }
}