package com.esei.grvidal.nighttime.network.network_DTOs

import androidx.compose.ui.graphics.ImageAsset


data class UserFull(
    var id: Long,//user ID
    var nickname: String,
    var name: String,
    var password: String,
    var state: String = "",
    var email: String,
    var nextDate: NextDateDTO? = null,
    var picture: String? = null
)

data class UserViewPrivate(
    val id: Long,
    val name: String,
    val password: String,
    val state: String? = null,
    val email: String
)


data class UserDTO(
    var id: Long,//user ID
    var name: String,
    var nickname: String,
    var state: String,
    var nextDate: NextDateDTO? = null,
    var picture: String? = null,
    var friendshipState: AnswerOptions
)

fun UserDTO.toUser(): UserFull {
    return UserFull(
        id = id,
        nickname = nickname,
        name = name,
        password = "",
        state = state,
        email = "",
        nextDate = nextDate,
        picture = picture
    )
}

data class UserDTOInsert(
    val name: String,
    val nickname: String,
    var password: String,
    val state: String? = null,
    val email: String
)


data class UserDTOEdit(
    var id: Long,
    val name: String?,
    val password: String?,
    val state: String? = null,
    val email: String?
)

data class UserToken(
    val id: Long,
    val token: String
)

data class UserSnap(
    val userId: Long,
    val username: String,
    val name: String,
    val image: Boolean
)

fun UserSnap.toUserSnapImage(
    img: ImageAsset? = null
): UserSnapImage {
    return UserSnapImage(
        this.userId,
        this.username,
        this.name,
        this.image,
        img
    )
}

data class UserSnapImage(
    val userId: Long,
    val username: String,
    val name: String,
    val hasImage: Boolean,
    var img: ImageAsset?
)