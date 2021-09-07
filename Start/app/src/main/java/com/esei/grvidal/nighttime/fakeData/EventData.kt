package com.esei.grvidal.nighttime.fakeData

import com.esei.grvidal.nighttime.network.network_DTOs.EventData
import java.time.LocalDate


val eventDataList = listOf(
    EventData(0, date = LocalDate.now().toString(), "Oferta 2 x 1", "Night"),
    EventData(1, date = LocalDate.now().plusDays(1).toString(), "Oferta invita a dos amigos y te regalamos una copa", "Night"),
    EventData(2, date = LocalDate.now().plusDays(1).toString(), "Oferta 2 x 1", "Lokal"),
    EventData(
        3,
        date = LocalDate.now().toString(),
        "Fiesta de la espuma.",
        "Lokal"
    ),
    EventData(
        4,
        date = LocalDate.now().plusDays(3).toString(),
        "Hoy cerrado por fiesta privada.",
        "Lokal"
    ),
    EventData(5, date = LocalDate.now().toString(), "Musica en vivo", "Studio 34"),
    EventData(
        6,
        date = LocalDate.now().plusDays(3).toString(),
        "Hoy cerrado por defuncion, esperemos que todos se pongan mejor, gracias por su atencion.",
        "Studio 34"
    ),
    EventData(7, date = LocalDate.now().plusDays(4).toString(), "Copas a 3 euros.", "Studio 34")
)
