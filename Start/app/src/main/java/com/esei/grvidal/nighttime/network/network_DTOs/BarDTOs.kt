package com.esei.grvidal.nighttime.network

/**
 * File with all DTOs related with Bars from API
 */

data class BarDTO(
    val id: Long,
    val name: String,
    val owner: String,
    val address: String,
    val description: String,

    val mondaySchedule: String?,
    val tuesdaySchedule: String?,
    val wednesdaySchedule: String?,
    val thursdaySchedule: String?,
    val fridaySchedule: String?,
    val saturdaySchedule: String?,
    val sundaySchedule: String?
) {
    val schedule: List<String>
        get() {
            return listOf(
                (mondaySchedule ?: ""),
                (tuesdaySchedule ?: ""),
                (wednesdaySchedule ?: ""),
                (thursdaySchedule ?: ""),
                (fridaySchedule ?: ""),
                (saturdaySchedule ?: ""),
                (sundaySchedule ?: "")
            )
        }
}

data class BarDetailsDTO(val id: Long, val events: List<EventFromBar>, val photos: Int)
data class EventFromBar(val id: Long, val description: String, val date: String?)
data class EventData(val id: Long, val date: String, val description: String, val barName: String)