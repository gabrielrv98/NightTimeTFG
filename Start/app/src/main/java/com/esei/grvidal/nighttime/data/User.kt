package com.esei.grvidal.nighttime.data

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esei.grvidal.nighttime.R


//Todo try to use them in the future

class ProfileViewModel : ViewModel() {

    private var userId: Int = 99

    fun setUserId(newUserId: Int?) {
        if (newUserId != userId) {
            userId = newUserId ?: meUser.id
        }
        _userData.value =
            if (userId == meUser.id) meUser.toProfileScreenState() else userPreview.toProfileScreenState()
    }

    private val _userData = MutableLiveData<ProfileScreenState>()
    val userData: LiveData<ProfileScreenState> = _userData
}

@Immutable
data class ProfileScreenState(
    val id: Int,
    @DrawableRes val photo: Int?,
    val name: String,
    val nickname: String,
    val status: String,
    var nextDate: MyDate? = null
) {
    fun isMe() = id == meUser.id
}

fun User.toProfileScreenState(): ProfileScreenState {
    return ProfileScreenState(
        id = this.id,
        name = this.name,
        nickname = this.nickname,
        status = this.status,
        nextDate = this.nextDate,
        photo = this.photo
    )
}

//here ends the copied code

class User(var name: String) : ViewModel() {
    var id = 0//user ID
    var nextDate: MyDate? = null
    var nickname: String = ""
    lateinit var status: String
    var photo: Int? = null


    var city: City = City(0, "Ourense")

    fun getChats(): List<ChatData> {
        return listOf(
            ChatData(0, userName = "Nuria Sotelo Domarco", "Dicen que hoy abre el Lokal"),
            ChatData(1, userName = "Maria Jose", "Ya he hecho lentejas"),
            ChatData(2, userName = "Pablo Pablito", "Hoy he clavado un clavo"),
            ChatData(
                3, userName = "Elma RockStar", "Concierto en aquel sitio!!, cuento contigo" +
                        " espero que no te pongas enfermo ocmo la ultima vez en aquel lugar, te acuerdas?\n" +
                        "Fue increible pero ojala no repetirlo nunca entiendes?"
            ),
            ChatData(0, userName = "Nuria Sotelo Domarco", "Dicen que hoy abre el Lokal"),
            ChatData(1, userName = "Maria Jose", "Ya he hecho lentejas"),
            ChatData(2, userName = "Pablo Pablito", "Hoy he clavado un clavo"),
            ChatData(
                3, userName = "Elma RockStar", "Concierto en aquel sitio!!, cuento contigo" +
                        " espero que no te pongas enfermo ocmo la ultima vez en aquel lugar, te acuerdas?\n" +
                        "Fue increible pero ojala no repetirlo nunca entiendes?"
            ),
            ChatData(0, userName = "Nuria Sotelo Domarco", "Dicen que hoy abre el Lokal"),
            ChatData(1, userName = "Maria Jose", "Ya he hecho lentejas"),
            ChatData(2, userName = "Pablo Pablito", "Hoy he clavado un clavo"),
            ChatData(
                3, userName = "Elma RockStar", "Concierto en aquel sitio!!, cuento contigo" +
                        " espero que no te pongas enfermo ocmo la ultima vez en aquel lugar, te acuerdas?\n" +
                        "Fue increible pero ojala no repetirlo nunca entiendes?"
            ),
            ChatData(0, userName = "Nuria Sotelo Domarco", "Dicen que hoy abre el Lokal"),
            ChatData(1, userName = "Maria Jose", "Ya he hecho lentejas"),
            ChatData(2, userName = "Pablo Pablito", "Hoy he clavado un clavo"),
            ChatData(
                3, userName = "Elma RockStar", "Concierto en aquel sitio!!, cuento contigo" +
                        " espero que no te pongas enfermo ocmo la ultima vez en aquel lugar, te acuerdas?\n" +
                        "Fue increible pero ojala no repetirlo nunca entiendes?"
            ),
        )
    }

    fun getChatConversation(idChat: Int): FullChat {
        return allChats[idChat]
    }

    private var allChats: List<FullChat> = listOf(
        FullChat(
            0, "Nuria Sotelo Domarco", 1, listOf(
                Message(0, "hey que tal?", "8:04 PM"),
                Message(1, "Bien, llegando a casa y tu?", "8:04 PM"),
                Message(0, "Acabando de trabjar", "8:04 PM"),
                Message(0, "Te apetece hacer algo hoy?", "8:04 PM"),
                Message(1, "Dicen que hoy abre el Lokal", "8:04 PM")
            )
        ),
        FullChat(
            1, "Maria Jose", 2, listOf(
                Message(3, "Hola hijo", "8:04 PM"),
                Message(3, "Estas bien?", "8:04 PM"),
                Message(0, "Con algo de hambre", "8:04 PM"),
                Message(3, "Yo me encargo", "8:04 PM"),
                Message(3, "Ya he hecho lentejas", "8:04 PM")
            )
        ),
        FullChat(
            2, "Pablo Pablito", 3, listOf(
                Message(3, "Hola hijo", "8:04 PM"),
                Message(3, "Estas bien?", "8:04 PM"),
                Message(0, "Con algo de hambre", "8:04 PM"),
                Message(3, "Yo me encargo", "8:04 PM"),
                Message(3, "Hoy he clavado un clavo", "8:04 PM")
            )
        ),
        FullChat(
            3, "Elma RockStar", 4, listOf(
                Message(3, "Hola hijo", "8:04 PM"),
                Message(3, "Estas bien?", "8:04 PM"),
                Message(0, "Con algo de hambre", "8:04 PM"),
                Message(3, "Yo me encargo", "8:04 PM"),
                Message(
                    3, "Concierto en aquel sitio!!, cuento contigo" +
                            " espero que no te pongas enfermo ocmo la ultima vez en aquel lugar, te acuerdas?\n" +
                            "Fue increible pero ojala no repetirlo nunca entiendes?", "8:04 PM"
                ),
                Message(3, "Hola hijo", "8:04 PM"),
                Message(3, "Estas bien?", "8:04 PM"),
                Message(0, "Con algo de hambre", "8:04 PM"),
                Message(3, "Yo me encargo", "8:04 PM"),
                Message(
                    3, "Concierto en aquel sitio!!, cuento contigo" +
                            " espero que no te pongas enfermo ocmo la ultima vez en aquel lugar, te acuerdas?\n" +
                            "Fue increible pero ojala no repetirlo nunca entiendes?", "8:04 PM"
                ),
                Message(3, "Hola hijo", "8:04 PM"),
                Message(3, "Estas bien?", "8:04 PM"),
                Message(0, "Con algo de hambre", "8:04 PM"),
                Message(3, "Yo me encargo", "8:04 PM"),
                Message(
                    3, "Concierto en aquel sitio!!, cuento contigo" +
                            " espero que no te pongas enfermo ocmo la ultima vez en aquel lugar, te acuerdas?\n" +
                            "Fue increible pero ojala no repetirlo nunca entiendes?", "8:04 PM"
                ),
                Message(3, "Hola hijo", "8:04 PM"),
                Message(3, "Estas bien?", "8:04 PM"),
                Message(0, "Con algo de hambre", "8:04 PM"),
                Message(3, "Yo me encargo", "8:04 PM"),
                Message(
                    3, "Concierto en aquel sitio!!, cuento contigo" +
                            " espero que no te pongas enfermo ocmo la ultima vez en aquel lugar, te acuerdas?\n" +
                            "Fue increible pero ojala no repetirlo nunca entiendes?", "8:04 PM"
                ),
                Message(3, "Hola hijo", "8:04 PM"),
                Message(3, "Estas bien?", "8:04 PM"),
                Message(0, "Con algo de hambre", "8:04 PM"),
                Message(0, "Pero poca", "8:04 PM"),
                Message(0, "Con algo de hambreeeeeeeeeeeeeeeeeeeee", "8:04 PM"),
                Message(0, "Con algo de hambreeeeeeeeeeeeeeeee", "8:04 PM"),
                Message(0, "Con algo de hambreeeeeee", "8:04 PM"),
                Message(
                    0,
                    "Pero no tanta como la del concierto del ourenrock esa vez fue increible\nUf\n cuantos recuerdos parece mentira eh",
                    "8:04 PM"
                ),
                Message(3, "Yo me encargo", "8:04 PM"),
                Message(
                    3, "Concierto en aquel sitio!!, cuento contigo" +
                            " espero que no te pongas enfermo ocmo la ultima vez en aquel lugar, te acuerdas?\n" +
                            "Fue increible pero ojala no repetirlo nunca entiendes?", "8:04 PM"
                ),
                Message(0, "Ok", "8:14 PM"),
            )
        )
    )


}

val userPreview = User("Manuel").apply {
    this.id = 1//user ID
    this.nickname = "Manu_23f"
    this.nextDate = MyDate(20, 11, 2020)
    this.status = "Hey there I'm using NightTime"
    this.photo = R.drawable.someone_else
}

val meUser = User("Gabriel").apply {
    this.id = 0//user ID
    this.nickname = "grvidal"
    this.nextDate = MyDate(25, 11, 2020)
    this.status = "Hey there I'm using NightTime"
    this.photo = R.drawable.arcangel
}

data class DatePeople(val amigos: Int, val total: Int)
object CalendarDao {

    fun getPeopleOnDate(cityId: Int, date: MyDate): DatePeople {

        return when {
            date.day % 5 == 0 -> {
                DatePeople(35, 144)
            }
            date.day % 2 == 0 -> {
                DatePeople(14, 20)
            }
            else -> DatePeople(0, 5)
        }
    }

    fun getFriends(cityId: Int, date: MyDate): List<User?> {
        //todo this is hardcoded
        return if (date.day == 1) {
            listOf()
        } else listOf(
            User(name = "Nuria"),
            User(name = "Miguel"),
            User(name = "Maria"),
            User(name = "Marcos"),
            User(name = "Laura"),
            User(name = "Sara"),
            User(name = "Julio"),
            User(name = "Juan"),
            User(name = "Pedro"),
            User(name = "Salva"),
            User(name = "Gabriel"),
            User(name = "Jose"),
            User(name = "Emma"),
            User(name = "Santi"),
            User(name = "Filo"),
            User(name = "Nuria"),
            User(name = "Miguel"),
            User(name = "Maria"),
            User(name = "Marcos"),
            User(name = "Laura"),
            User(name = "Sara"),
            User(name = "Julio"),
            User(name = "Juan"),
            User(name = "Pedro"),
            User(name = "Salva"),
            User(name = "Gabriel"),
            User(name = "Jose"),
            User(name = "Emma"),
            User(name = "Santi"),
            User(name = "Filo")
        )
    }
}