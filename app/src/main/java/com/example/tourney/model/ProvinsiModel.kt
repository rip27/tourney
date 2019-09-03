package com.example.tourney.model

import com.google.gson.annotations.SerializedName

data class ProvinsiModel(
    @SerializedName("id") var id : String?,
    @SerializedName("nama") var nama : String?
)