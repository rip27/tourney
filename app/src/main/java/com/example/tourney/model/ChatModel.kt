package com.example.tourney.model

class ChatModel (
    var text: String? = null,
    var toid: String? = null,
    var fromid: String? = null,
    var timestamp: Long = System.currentTimeMillis()
)