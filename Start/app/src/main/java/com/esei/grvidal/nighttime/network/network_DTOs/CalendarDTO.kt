package com.esei.grvidal.nighttime.network.network_DTOs

data class CalendarData(
    val total: Int,
    val friends: Int,
    val events: List<EventData>
)

