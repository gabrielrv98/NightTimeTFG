package com.esei.grvidal.nighttime.network.network_DTOs

import androidx.compose.ui.graphics.ImageAsset

data class UserFriendViewAPI(
    var friendshipId: Long,
    var userId: Long,
    var userNickname: String,
    var state: String,
    var image: Boolean
)

data class UserFriendView(
    var friendshipId: Long,
    var userId: Long,
    var userNickname: String,
    var state: String,
    val hasImage: Boolean,
    var image: ImageAsset? = null
)

data class FriendshipUpdateDTO(
    val id: Long,
    val answer: AnswerOptions
)

data class FriendshipSnap(
    val userId: Long,
    val friendshipId :Long,
    var username: String,
    val name: String,
    val image: Boolean
){
    fun toFriendshipSnapImage(
        image: ImageAsset? = null
    ): FriendshipSnapImage {
        return FriendshipSnapImage(
            userId = this.userId,
            friendshipId = this.friendshipId,
            username = this.username,
            name = this.name,
            hasImage = this.image,
            image = image
        )
    }
}

data class FriendshipSnapImage(
    val userId: Long,
    val friendshipId :Long,
    var username: String,
    val name: String,
    val hasImage: Boolean,
    var image: ImageAsset?
)

/**
 * Enum with the option to answer a friendship request
 */
enum class AnswerOptions {
    NOT_ANSWERED,
    YES,
    NO
}