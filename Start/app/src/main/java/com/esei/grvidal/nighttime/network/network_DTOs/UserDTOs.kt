package com.esei.grvidal.nighttime.network.network_DTOs

import com.esei.grvidal.nighttime.network.AnswerOptions



data class UserFull(
    var id: Long,//user ID
    var nickname: String,
    var name: String,
    var password: String,
    var state: String = "",
    var email: String,
    var nextDate: NextDate? = null,
    var picture: String? = null
)

data class UserViewPrivate(
    val id: Long,
    val name: String,
    val password: String,
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

data class UserDTO(
    var id: Long,//user ID
    var name: String,
    var nickname: String,
    var state: String,
    var nextDate: NextDate? = null,
    var picture: String? = null,
    var friendshipState: AnswerOptions
) {
    fun toUser(): UserFull {
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
}

data class NextDate(
    val id: Long,
    val nextDate: String,
    val nextCity: CityDTO
) {
    override fun toString(): String {
        val date = nextDate.split("-")

        return StringBuilder()
            .append(date[2])
            .append("-")
            .append(date[1])
            .append("-")
            .append(date[0])
            .append(" : ")
            .append(nextCity.name)
            .toString()
    }
}

data class CityDTO(
    val id: Long,
    val name: String,
    val country: String
)

data class UserDTOInsert(
    val name: String,
    val nickname: String,
    var password: String,
    val state: String? = null,
    val email: String
)

