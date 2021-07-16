package com.esei.grvidal.nighttime.network.network_DTOs

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageAsset


data class ChatView(
    val friendshipId: Long,
    val userId: Long,
    val userNickname: String,
    val hasImage: Boolean,
    val messages: List<MessageView>,
    val unreadMessages: Int
)

data class MessageView(
    val messageId: Long,
    val text: String,
    val date: String,
    val time: String,
    val user: Long
)

@Immutable
data class ChatFullView(
    val friendshipId: Long,
    val userId: Long,
    val userNickname: String,
    val hasImage: Boolean,
    val messages: List<MessageView>,
    val unreadMessages: Int,
    var img: ImageAsset?
)