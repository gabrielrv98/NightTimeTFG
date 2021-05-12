package com.esei.grvidal.nighttime.network.network_DTOs

data class NextDateDTO(
    val id: Long,
    val nextDate: String,
    val nextCity: CityDTO
) {
    override fun toString(): String {
        val date = nextDate.split("-")

        return StringBuilder()
            .append(date[2])
            .append("-")
            .append(date[1])
            .append("-")
            .append(date[0])
            .append(" : ")
            .append(nextCity.name)
            .toString()
    }
}