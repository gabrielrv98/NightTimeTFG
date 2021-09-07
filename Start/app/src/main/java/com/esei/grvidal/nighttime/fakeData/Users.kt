package com.esei.grvidal.nighttime.fakeData

import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.viewmodels.City
import java.time.LocalDate
import kotlin.collections.HashMap
import kotlin.random.Random


data class User(
    var name: String,
    var nickname: String,
    var password: String,
    var state: String? = null,
    var email: String

) {

    constructor(
        name: String,
        nickname: String,
        password: String,
        state: String? = null,
        email: String,
        daysInFuture: HashMap<Long, City>
    ) : this(name, nickname, password, state, email) {

        for (day in daysInFuture) {
            nextDates = nextDates.toMutableList().also {

                it.add(
                    DateCity(
                        nextDate = LocalDate.now().plusDays(day.key),
                        nextCity = day.value
                    )
                )
            }.toList()

        }
    }

    var nextDates: List<DateCity> = listOf()

    var picture: Int? = null
    var id: Long = -1

}

class DateCity(
    var nextDate: LocalDate,
    var nextCity: City
) {
    var id: Long = 0L
}


val grvidal = User(
    "Gabriel Rguez",
    "grvidal",
    "1234",
    "Hey there i'm using NightTime",
    email = "grvidal@esei.uvigo.es",
    daysInFuture = hashMapOf(
        Pair(0L, cityOu),
        Pair(1L, cityOu),
        Pair(2L, cityVigo),
        Pair(9L, cityOu)
    )
).apply {
    picture = R.drawable.grvidal
    id = 0
}

val santi = User(
    "Santi Gómez",
    "santii810",
    "santiSuperSecret",
    "Programando",
    "santii810@gmail.com",
    daysInFuture = hashMapOf(
        Pair(0, cityOu),
        Pair(2, cityOu),
        Pair(5, cityOu)
    )
).apply {
    picture = R.drawable.santii810
    id = 1
}


val maria = User(
    "Maria Vidal",
    "mvittae",
    "Gabriel<3",
    "Me encanta el bar \"Luxus\"",
    email = "mjvidal@hotmail.com",
    daysInFuture = hashMapOf(
        Pair(0, cityOu),
        Pair(1, cityOu),
        Pair(5, cityOu)
    )
).apply {
    id = 2
}


val joseN = User(
    "Jose Negro",
    "joseju",
    "passwordUser4",
    "Todo lo que se pueda decir es irrelevante",
    "joseNegro@gmail.com",
    daysInFuture = hashMapOf(
        Pair(0, cityOu),
        Pair(1, cityVigo),
        Pair(3, cityVigo)
    )
).apply {
    id = 3
}


val nuria = User(
    "Nuria Sotelo",
    "pinkxnut",
    "passwordUser2",
    ".",
    "nuasotelo@gmail.com",
    daysInFuture = hashMapOf(
        Pair(0, cityOu),
        Pair(1, cityOu),
        Pair(3, cityOu)
    )
).apply {
    picture = R.drawable.pinkxnut
    id = 4
}


val juan = User(
    "Juan Quintás",
    "juquint",
    "passwordUser5",
    "Fiesta ya!",
    "juanjuan@gmail.com",
    daysInFuture = hashMapOf(
        Pair(0, cityOu),
        Pair(1, cityOu),
        Pair(2, cityOu)
    )
).apply {
    picture = R.drawable.someone_else
    id = 5
}


val allUsersList: List<User> = listOf(grvidal, santi, maria, nuria, joseN, juan) + generateUsers(0)

fun generateUsers(max: Int): List<User> {
    val list = mutableListOf<User>()

    var idIndex = 6L
    nicknameList.subList(0, if(max== 0) nicknameList.size else max).forEach {
        val name =
            namesList[Random.nextInt(0, namesList.size - 1)] +
                    " " + surnameList[Random.nextInt(0, surnameList.size - 1)]
        list.add(

            User(
                name = name,
                nickname = it.trim(),
                password = it.trim() + "12345",
                state = stateList[Random.nextInt(0, stateList.size - 1)],
                email = "$name@gmail.com",
                daysInFuture = hashMapOf<Long, City>().apply {
                    for (i in 0..7) {
                        this[Random.nextLong(0, 30)] = cityOu
                    }
                    for (i in 0..3) {
                        this[Random.nextLong(30, 60)] = cityOu
                    }
                }

            ).apply {
                id = idIndex
                idIndex++
            }
        )

    }
    return list
}