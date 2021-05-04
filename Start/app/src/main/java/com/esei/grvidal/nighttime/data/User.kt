package com.esei.grvidal.nighttime.data

import androidx.compose.ui.graphics.ImageAsset


data class UserToken(
    val id: Long,
    val token: String
)

data class UserSnap(
    val userId: Long,
    val username: String,
    val name: String,
    val image: Boolean
){
    fun toUserSnapImage(
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
}

data class UserSnapImage(
    val userId: Long,
    val username: String,
    val name: String,
    val hasImage: Boolean,
    var img: ImageAsset?
)


