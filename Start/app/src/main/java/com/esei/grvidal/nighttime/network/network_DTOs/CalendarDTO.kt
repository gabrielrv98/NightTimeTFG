package com.esei.grvidal.nighttime.network.network_DTOs

import com.esei.grvidal.nighttime.network.EventData

data class CalendarData(
    val total: Int,
    val friends: Int,
    val events: List<EventData>
)

