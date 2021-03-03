package com.esei.grvidal.nighttime.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "BarViewModel"

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
    val schedule: List<Boolean>
        get() {
            return listOf(
                (mondaySchedule != null),
                (tuesdaySchedule != null),
                (wednesdaySchedule != null),
                (thursdaySchedule != null),
                (fridaySchedule != null),
                (saturdaySchedule != null),
                (sundaySchedule != null)
            )
        }
}

class BarViewModel : ViewModel() {

    var barList by mutableStateOf(listOf<BarDTO>())
    private var page: Int = 0

    var city: City = City(-1, "Ourense")
        set(value) {
            Log.d(TAG, "setCityId: old id = $city, new $value")

            if (city != value) { // If the city has been updated, value is set and data is fetched
                field = value

                barList = listOf()
                page = 0

                Log.d(TAG, "setCityId: removing old data and fetching new data")
                loadBarsOnCity()//Loading the first page
            }
        }


    init {
        Log.d(TAG, "init: iniciando Bar")
    }

    /**
     * Sets the value of the loggedUser LiveData token to the given by NightTime Api.
     */
    fun loadBarsOnCity() {

        viewModelScope.launch {
            fetchBars(page++)
        }
    }

    private suspend fun fetchBars(page: Int) {

        try {

            Log.e(TAG, "fetchBars: fetching for bars page $page")

            val webResponse = NightTimeApi.retrofitService.listByCity(city.id, page)


            if (webResponse.isSuccessful) {

                webResponse.body()?.let { bars ->
                    barList = barList + bars
                }

                Log.d(TAG, "fetchBars: data fetched $barList")

            }


        } catch (e: IOException) {
            Log.e(TAG, "fetchBars: network exception ${e.message}  --//-- $e")
            barList = BarOfflineList.bars

        } catch (e: Exception) {
            Log.e(TAG, "fetchBars: general exception ${e.message}  --//-- $e")

        }
    }
}

object BarOfflineList{

    val bars = listOf(

    BarDTO(
        id = 0,
        name = "Luxus",
        owner = "Nuria Sotelo",
        address = "Rua Concordia",
        description = "Un lugar libre para gente libre",

        mondaySchedule = "12:00-22:00",
        tuesdaySchedule = null,
        wednesdaySchedule = "11:00-20:30",
        thursdaySchedule = "14:40-21:20",
        fridaySchedule = "11:00-20:30",
        saturdaySchedule = null,
        sundaySchedule = "09:30-21:30"
    ),
        BarDTO(
            id = 1,
            name = "Patio andaluz",
            owner = "Aida Miguez",
            address = "Calle turbia",
            description = "Un buen lugar para charlar, conocer gente y disfrutar de la vida reunido de buenas compañias",
            mondaySchedule = "12:00-20:30",
            tuesdaySchedule = "12:00-20:30",
            wednesdaySchedule = null,
            thursdaySchedule = "17:00-22:00",
            fridaySchedule = null,
            saturdaySchedule = "14:40-21:20",
                    sundaySchedule = "09:30-21:30"
        ),
        BarDTO(
            id = 2,
            name = "Faro de Vigo",
            owner = "Juan Miranda",
            address = "Calle Principe",
            description = "Somos el mejor lugar para verlo todo",
            mondaySchedule = "11:00-20:30",
            tuesdaySchedule = null,
            wednesdaySchedule = "12:00-22:00",
            thursdaySchedule = "12:00-22:00",
            fridaySchedule = null,
            saturdaySchedule = "14:40-21:20",
            sundaySchedule = "09:30-21:30"
        ),
        BarDTO(
            id = 3,
            name = "Night",
            owner = "NightOwner",
            address = "Rua cabeza de manzaneda",
            description = "Ven y pasatelo como si volvieses a tener 15",
            mondaySchedule = null,
            tuesdaySchedule = "11:00-20:30",
            wednesdaySchedule = "12:00-22:00",
            thursdaySchedule = null,
            fridaySchedule = "11:00-20:30",
            saturdaySchedule = "14:40-21:20",
            sundaySchedule = "09:30-21:30"
        ),

        BarDTO(
            id = 4,
            name = "Studio 34",
            owner = "Studio Owner Santiago",
            address = "Rua Concordia",
            description = "Un lugar libre para gente libre",
            mondaySchedule = "12:00-22:00",
            tuesdaySchedule = "11:00-20:30",
            wednesdaySchedule = null,
            thursdaySchedule = "14:40-21:20",
            fridaySchedule = "11:00-20:30",
            saturdaySchedule = null,
            sundaySchedule = "09:30-21:30"
        )
    )
}
