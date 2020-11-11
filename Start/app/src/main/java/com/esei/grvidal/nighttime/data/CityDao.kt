package com.esei.grvidal.nighttime.data

data class City(val id:Int, val name:String)

class CityDao {

    private val defaultCities = listOf(
        City(0,"Ourense"),
        City(1,"Pontevedra"),
        City(2,"Vigo"),
        City(3,"Coruña"),
        City(4,"Allariz"),
        City(5,"Lugo"),
        City(6,"Rivadavia"),
    )

    fun getAllCities() : List<City> {
        //conexion a la base y devolver las existentes
        return defaultCities
    }
}