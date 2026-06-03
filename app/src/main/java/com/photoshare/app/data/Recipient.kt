package com.photoshare.app.data

import java.io.Serializable

data class Recipient(
    val name: String,
    val phone: String,
    val email: String
) : Serializable

enum class Channel : Serializable {
    SMS,
    EMAIL,
    WHATSAPP,
    MESSENGER
}

data class RecipientChannel(
    val recipient: Recipient,
    val channel: Channel
) : Serializable
