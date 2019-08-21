package com.example.tourney.model

import java.io.Serializable

class UserModel(
    var key: String,
    var id: String,
    var name: String,
    var gender: String,
    var password: String,
    var phone: String,
    var email: String,
    var profile: String
) : Serializable {
    constructor() : this("", "", "", "", "", "", "", "")
}