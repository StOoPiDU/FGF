package com.cedric.fgf.misc

import retrofit2.Call
import retrofit2.http.GET


interface RetroFitAPI {
    @GET("FreeGameFindings/new/.json")
    fun getData(): Call<FGFData>
}