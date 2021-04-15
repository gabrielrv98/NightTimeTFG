package com.esei.grvidal.nighttime.data

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageAsset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esei.grvidal.nighttime.R


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
            0, "Nuria Sotelo Domarco", 67, listOf(
                Message(0, "hey que tal?", "8:04 PM"),
                Message(1, "Bien, llegando a casa y tu?", "8:04 PM"),
                Message(0, "Acabando de trabjar", "8:04 PM"),
                Message(0, "Te apetece hacer algo hoy?", "8:04 PM"),
                Message(1, "Dicen que hoy abre el Lokal", "8:04 PM")
            )
        ),
        FullChat(
            1, "Santi", 71, listOf(
                Message(3, "Heyy", "8:04 PM"),
                Message(3, "Estas en casa", "8:04 PM"),
                Message(0, "Claro", "8:04 PM"),
                Message(3, "Llego en 15 min", "8:04 PM"),
                Message(3, "No te quedes dormido", "8:04 PM")
            )
        ),
        FullChat(
            2, "Maria Vidal", 75, listOf(
                Message(3, "Hola hijo", "8:04 PM"),
                Message(3, "Estas bien?", "8:04 PM"),
                Message(0, "Con algo de hambre", "8:04 PM"),
                Message(3, "Yo me encargo", "8:04 PM"),
                Message(3, "Hoy he clavado un clavo", "8:04 PM")
            )
        ),
        FullChat(
            3, "Elma RockStar", 79, listOf(
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
