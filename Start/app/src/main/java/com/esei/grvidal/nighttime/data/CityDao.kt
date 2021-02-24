package com.esei.grvidal.nighttime.data

data class City(val id:Long, val name:String)

class CityDao {

    private val defaultCities = listOf(
        City(0L,"Ourense"),
        City(1L,"Pontevedra"),
        City(2L,"Vigo"),
        City(3L,"Coruña"),
        City(4L,"Allariz"),
        City(5L,"Lugo"),
        City(6L,"Rivadavia"),
    )

    fun getAllCities() : List<City> {
        //conexion a la base y devolver las existentes
        return defaultCities
    }
}