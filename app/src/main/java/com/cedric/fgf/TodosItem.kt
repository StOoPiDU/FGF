package com.cedric.fgf

import com.google.gson.annotations.SerializedName

data class TodosItem(
// data > children > 0 > data

//    @SerializedName("id") // This can also be used to build the link -> https://redd.it/{id}
//    val id: String,
//    @SerializedName("title")
//    val title: String,
//    @SerializedName("author")
//    val author: String,
//    val link_flair_css_class: String,
//    @SerializedName("thumbnail")
//    val thumbnail: String,
//    @SerializedName("url")
//    val url: String,

    @SerializedName("completed")
    val completed: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("userId")
    val userId: Int
)