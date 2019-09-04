package com.example.tourney.utils

import com.example.tourney.model.AreaModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    @GET("provinsi")
    fun getArea() : Call<AreaModel>

    @GET("nama/{nama}")
    fun getArea(@Path("nama")name: String) : Call<AreaModel>
}