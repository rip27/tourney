package com.example.tourney.model

class ChatModel {
    constructor() //empty for firebase

    constructor(messageText: String) {
        text = messageText
    }

    var text: String? = null
    var timestamp: Long = System.currentTimeMillis()
}