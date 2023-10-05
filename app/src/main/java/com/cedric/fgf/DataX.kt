package com.cedric.fgf

import com.google.gson.annotations.SerializedName

// Guided by Michael, not working nor doing anything.

data class DataX(
    @SerializedName("id") // This can also be used to build the link -> https://redd.it/{id}
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("thumbnail")
    val link_flair_css_class: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("url")
    val url: String,
)