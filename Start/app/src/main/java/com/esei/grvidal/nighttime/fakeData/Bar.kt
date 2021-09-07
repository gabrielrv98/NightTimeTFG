package com.esei.grvidal.nighttime.fakeData

import com.esei.grvidal.nighttime.network.network_DTOs.EventData
import com.esei.grvidal.nighttime.viewmodels.City
import com.esei.grvidal.nighttime.R
import java.time.LocalDate
import kotlin.random.Random

class Bar(
    var name: String,
    var owner: String,
    var address: String,
    var description: String?,

    var mondaySchedule: String? = null,
    var tuesdaySchedule: String? = null,
    var wednesdaySchedule: String? = null,
    var thursdaySchedule: String? = null,
    var fridaySchedule: String? = null,
    var saturdaySchedule: String? = null,
    var sundaySchedule: String? = null,

    var city: City
) {

    var events: List<EventData> = listOf()

    var photos: List<Int> = listOf()

    var id: Long = 0

}

val barNight = Bar(
    name = "Night",
    owner = "NightOwner",
    address = "Rua cabeza de manzaneda",
    description = "Ven y pasatelo como si volvieses a tener 15",
    city = cityOu,
    mondaySchedule = "12:00-22:00",
    tuesdaySchedule = "11:00-20:30",
    wednesdaySchedule = "12:00-22:00",
    thursdaySchedule = null,
    fridaySchedule = "11:00-20:30",
    saturdaySchedule = "14:40-21:20",
    sundaySchedule = "09:30-21:30"
).apply {
    id = 0

    events = events.toMutableList().apply {
        add(eventDataList[0])
        add(eventDataList[1])
    }
    photos = photos.toMutableList().apply {
        add(R.drawable.bar8_0)
        add(R.drawable.bar8_1)
        add(R.drawable.bar8_3)
        add(R.drawable.bar8_4)
        add(R.drawable.bar8_6)
        add(R.drawable.bar8_7)
    }
}

val barLokal = Bar(
    "Lokal",
    "Mario Garcia",
    "Praza Correxidor",
    description = "Un lokal para escuchar rock",
    city = cityOu,
    mondaySchedule = "12:00-22:00",
    tuesdaySchedule = "11:00-20:30",
    wednesdaySchedule = null,
    thursdaySchedule = "14:40-21:20",
    fridaySchedule = "11:00-20:30",
    saturdaySchedule = null,
    sundaySchedule = "09:30-21:30"
).apply {
    id = 1

    events = events.toMutableList().apply {
        add(eventDataList[2])
        add(eventDataList[3])
        add(eventDataList[4])
    }
    photos = photos.toMutableList().apply {
        add(R.drawable.bar18_3)
        add(R.drawable.bar18_4)
        add(R.drawable.bar18_5)
        add(R.drawable.bar18_6)
    }
}


val barStudio = Bar(
    name = "Studio 34",
    owner = "Studio Owner Santiago",
    address = "Rua Concordia",
    description = "Un lugar libre para gente libre",
    city = cityOu,
    mondaySchedule = null,
    tuesdaySchedule = "11:00-20:30",
    wednesdaySchedule = null,
    thursdaySchedule = "14:40-21:20",
    fridaySchedule = "11:00-20:30",
    saturdaySchedule = null,
    sundaySchedule = "09:30-21:30"
).apply {
    id = 2

    events = events.toMutableList().apply {
        add(eventDataList[5])
        add(eventDataList[6])
        add(eventDataList[7])
    }

    photos = photos.toMutableList().apply {
        add(R.drawable.bar22_0)
        add(R.drawable.bar22_1)
        add(R.drawable.bar22_2)
        add(R.drawable.bar22_3)
        add(R.drawable.bar22_4)
        add(R.drawable.bar22_5)
    }
}


val barRequiem = Bar(
    name = "Requiem",
    owner = "Nuria Sotelo",
    address = "Rua Concordia",
    description = "Un lugar libre para gente libre.",
    city = cityOu,
    mondaySchedule = "12:00-22:00",
    tuesdaySchedule = null,
    wednesdaySchedule = "11:00-20:30",
    thursdaySchedule = "14:40-21:20",
    fridaySchedule = "11:00-20:30",
    saturdaySchedule = null,
    sundaySchedule = "09:30-21:30"
).apply {
    id = 3

}

fun generateBars(): List<Bar> {
    val list = mutableListOf<Bar>()
    for (i in 0..15) {
        list.add(
            Bar(
                name = "Bar$i",
                owner = "Nuria Sotelo",
                address = "Rua San Francisco",
                description = "Un lugar libre para gente libre.",
                city = cityOu,
                mondaySchedule = "12:00-22:00",
                tuesdaySchedule = null,
                wednesdaySchedule = "11:00-20:30",
                thursdaySchedule = "14:40-21:20",
                fridaySchedule = "11:00-20:30",
                saturdaySchedule = null,
                sundaySchedule = "09:30-21:30"
            ).apply {
                id = i + 4L
                events = generateEvents(5, "Bar$i")
            }
        )
    }
    return list.toList()
}

fun generateEvents(max: Int, name: String): List<EventData> {

    val list = mutableMapOf<LocalDate, EventData>()
    var id = 0L
    for (i in 0..max / 3 * 2) {
        var date: LocalDate

        do {
            date = LocalDate.now().plusDays(Random.nextLong(0, 30))
        } while (list.keys.contains(date))


        list[date] = EventData(
            id,
            date.toString(),
            offersList[Random.nextInt(
                0,
                offersList.size
            )],
            name
        )
        id++
    }

    for (i in 0..max / 3) {
        var date: LocalDate

        do {
            date = LocalDate.now().plusDays(Random.nextLong(30, 60))
        } while (list.keys.contains(date))

        list[date] = EventData(
            id,
            date.toString(),
            offersList[Random.nextInt(
                0,
                offersList.size
            )],
            name
        )
        id++
    }

    return list.toSortedMap().values.toList()
}


val barListFakeData = listOf(barNight, barLokal, barRequiem, barStudio) + generateBars()
