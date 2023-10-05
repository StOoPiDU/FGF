package com.cedric.fgf

import retrofit2.Call
import retrofit2.http.GET


interface RetroFitAPI {
//    @GET(".json")
//    fun getData(): Call <List<Children>>

    @GET("todos")
    fun getData(): Call <List<TodosItem>>
}