package com.cedric.fgf

import retrofit2.Call
import retrofit2.http.GET


interface RetroFitAPI {
//    @GET("children")
//    fun getData(): Call <List<todosItem>>

    @GET("todos")
    fun getData(): Call <List<TodosItem>>
}