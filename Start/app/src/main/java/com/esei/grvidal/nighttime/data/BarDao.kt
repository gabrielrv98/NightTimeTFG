package com.esei.grvidal.nighttime.data



data class EventData(val barName: String, val eventDescription: String)

object BarDao {

    fun getEvents(cityId: Long, date: MyDate): List<EventData?> {
        //Conexion, consulta y ordenado
        return if ((date.day %5) == 0){
            listOf(
                EventData("Lazaros", "Copas a 3 euros"),
                EventData("Lokal", "Musica de los 90"),
                EventData("Patio andaluz", "Fiesta de la espuma"),
                EventData("Luxus", "Hoy cerrado por fiesta infantil, nos vemos gente"),
                EventData("Urbe", "Cocaina gratis"),
                EventData(
                    "Dulce flor", "Ahora un 30% en nuevos productos y perfumes con un coste " +
                            "inferior a 2$"
                )
            )
        } else if ((date.day %2) == 0){
            listOf(
                EventData("Palmas", "Copas gratis"),
                EventData("ZumosExpress", "Musica de los 2000"),
                EventData("BaresOscuros", "Fiesta de la salchica"),
                EventData("Parajos", "Hoy cerrado por defuncion, esperemos que todos se pongan mejor, gracias por su atencion"),
                EventData("Urbe", "Cocaina gratis"),
                EventData(
                    "Dulce flor", "Hoy igual no nos gusta salir a la calle"
                )
            )
        } else listOf()


    }
}