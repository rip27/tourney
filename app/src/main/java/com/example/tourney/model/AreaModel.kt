package com.example.tourney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AreaModel (
    @SerializedName("error") var error : Boolean?,
    @SerializedName("message") var message: String?,
    @SerializedName("semuaprovinsi") var semuaprovinsi: ArrayList<ProvinsiModel>?

) : Serializable