package com.esei.grvidal.nighttime.data

import androidx.lifecycle.ViewModel

class User(val name: String) : ViewModel() {

    val nextDate: MyDate
        get() {
            TODO()
        }
    val city: City
        get() {
            TODO()
        }


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