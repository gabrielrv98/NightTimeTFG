package com.esei.grvidal.nighttime.fakeData

import com.esei.grvidal.nighttime.network.network_DTOs.AnswerOptions
import java.time.LocalDate
import java.time.LocalTime


data class Friendship(
    var userAsk: User

) {
    var answer: AnswerOptions = AnswerOptions.NOT_ANSWERED
    var messages: Set<Message>? = null
    var id: Long = 0
}


class Message(
    var text: String,
    var date: LocalDate = LocalDate.now(),
    var hour: LocalTime = LocalTime.now(),
    var friendship: Friendship,
    var user: User
) {

    var readState: ReadState = ReadState.NOT_READ
    var id: Long = 0
}

enum class ReadState {
    READ,
    NOT_READ
}


val friendList = mutableListOf(
    Friendship(nuria)
        .apply {
            id = 0
            answer = AnswerOptions.YES
            messages = setOf(
                Message(
                    "Hola Nuria", LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(10),
                    this,
                    grvidal
                ).apply { readState = ReadState.READ },
                Message(
                    "Que tal?", LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(9),
                    this,
                    nuria
                ).apply { readState = ReadState.READ },
                Message(
                    "Todo bien que tal estas tu?", LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(8),
                    this,
                    grvidal
                ).apply { readState = ReadState.READ },
                Message(
                    "Genial, tengo ganas de ir al cine a ver esa nueva peli",
                    LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(7),
                    this,
                    nuria
                ).apply { readState = ReadState.READ },
                Message(
                    "Yo tambien, dicen que esta muy bien", LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(6),
                    this,
                    grvidal
                ).apply { readState = ReadState.READ },
                Message(
                    "Además hace mucho que no vamos al cine", LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(5),
                    this,
                    grvidal
                ).apply { readState = ReadState.READ },
                Message(
                    "Te veo a las 12, no llegues tarde!", LocalDate.now(),
                    LocalTime.now().minusMinutes(1),
                    this,
                    nuria
                )
            )
        },


    Friendship(santi)
        .apply {
            id = 1
            answer = AnswerOptions.YES
            messages = setOf(
                Message(
                    "Santiii al final lograste conectarlo?", LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(10),
                    this,
                    grvidal
                ).apply { readState = ReadState.READ },
                Message(
                    "Si, ahora todo funciona bien, no se muy bien por que fallaba",
                    LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(9),
                    this,
                    this.userAsk
                ).apply { readState = ReadState.READ },
                Message(
                    "Seria un bug", LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(8),
                    this,
                    this.userAsk
                ).apply { readState = ReadState.READ },
                Message(
                    "Tu hiciste tu parte",
                    LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(7),
                    this,
                    this.userAsk
                ).apply { readState = ReadState.READ },
                Message(
                    "Claroo, deberiamos estar listos para pasar a produccion",
                    LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(6),
                    this,
                    grvidal
                ).apply { readState = ReadState.READ },
                Message(
                    "Perfecto, te veo el jueves a las 6", LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(5),
                    this,
                    this.userAsk
                ),
                Message(
                    "A las 16*", LocalDate.now(),
                    LocalTime.now().minusMinutes(1),
                    this,
                    this.userAsk
                )
            )
        },

    Friendship(maria)
        .apply {
            id = 2
            answer = AnswerOptions.YES
            messages = setOf(
                Message(
                    "Hola, la aplicacion dice que el sabado sales, nos vamos encontes?",
                    LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(10),
                    this,
                    this.userAsk
                ).apply { readState = ReadState.READ },
                Message(
                    "Claro, estare donde siempre, fijo que nos vemos", LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(9),
                    this,
                    grvidal
                ).apply { readState = ReadState.READ },
                Message(
                    "Me puedes dar esa copa que me debias si quires",
                    LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(8),
                    this,
                    this.userAsk
                ).apply { readState = ReadState.READ },
                Message(
                    "Nunca perdonas una eh, el primero en ser encontrado invita a un chupito",
                    LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(7),
                    this,
                    grvidal
                ).apply { readState = ReadState.READ },
                Message(
                    "Mira que eres rata!!!", LocalDate.now().minusDays(1),
                    LocalTime.now().minusMinutes(6),
                    this,
                    this.userAsk
                ).apply { readState = ReadState.READ },
                Message(
                    "Buena suerte XD", LocalDate.now(),
                    LocalTime.now().minusMinutes(5),
                    this,
                    grvidal
                ).apply { readState = ReadState.READ }
            )
        },
    Friendship(joseN).apply { id = 3 },
    Friendship(allUsersList[5]).apply { id = 4 },
    Friendship(allUsersList[6]).apply { id = 5 },
    Friendship(allUsersList[9]).apply { id = 6 }

)

// Find a user if he is in friendList
fun findUser(idUser: Long):User?{
    val iterator = friendList.iterator()
    var isFound = false
    var user:User? = null
    while (iterator.hasNext() && !isFound ){
        val actual = iterator.next()
        if (actual.userAsk.id == idUser){
            isFound = true
            user = actual.userAsk
        }
    }

    return user
}