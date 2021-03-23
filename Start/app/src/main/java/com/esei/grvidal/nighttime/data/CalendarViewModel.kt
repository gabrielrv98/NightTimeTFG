package com.esei.grvidal.nighttime.data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.network.DateCityDTO
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi.retrofitService
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate

private const val TAG = "CalendarViewModel"


data class CalendarData(
    val total: Int,
    val friends: Int,
    val events: List<EventData>
)

data class EventData(val id: Long, val date: String, val description: String, val barName: String)

class CalendarViewModel : ViewModel() {

    private var userToken = UserToken(-1, "")

    fun setUserToken(loggedUser: UserToken) {

        Log.d(TAG, "setUserToken: old token = $userToken, new $loggedUser")
        userToken = loggedUser
    }

    var cityId: Long = -1
        set(value) {
            Log.d(TAG, "setCityId: old id = $cityId, new $value")

            if (cityId != value) { // If the city has been updated, value is set and data is fetched
                field = value

                Log.d(TAG, "setCityId: fetching data")
                loadSelectedDate()//Loading the actual day
                getUserDateList() // Fetch from api the list of dates in the selected city selected by the user
            }
        }


    //remember date, it's used to show the selected date and move the calendar to the specified month
    var selectedDate by mutableStateOf(LocalDate.now()!!.toMyDate())
        private set

    var dateInformation by mutableStateOf(
        CalendarData(
            -1,
            -1,
            listOf()
        )
    )

    //Remembered state of the days that must be shown on the calendar
    var calendar by mutableStateOf(ChipDayFactory.datesCreator())
        private set

    //List with all the selected days by the users
    var userDays by mutableStateOf(listOf<MyDate>())
        private set

    var userFriends by mutableStateOf(listOf<UserSnap>())
        private set

    private fun loadSelectedDate() {
        //call api
        Log.d(TAG, "load: $selectedDate userToken $userToken")

        viewModelScope.launch {
            try {

                val webResponse =  retrofitService.getPeopleAndEventsOnDateAsync(
                    auth = userToken.token,
                    id = userToken.id,
                    day = selectedDate.day,
                    month = selectedDate.month,
                    year = selectedDate.year,
                    idCity = cityId
                )
                Log.d(
                    TAG,
                    "call to retrofit done"
                )

                if (webResponse.isSuccessful) {
                    var eventList: List<EventData> = listOf()

                    webResponse.body()?.let { data ->
                        eventList = data

                    }
                    val total = webResponse.headers()["total"]?.toInt() ?: -1
                    val friends = webResponse.headers()["friends"]?.toInt() ?: -1

                    dateInformation = CalendarData(total, friends, eventList)

                    Log.d(
                        TAG,
                        "data fetched $dateInformation"
                    )
                } else {
                    Log.e(
                        TAG,
                        "error fetching data from CalendarViewModel load"
                    )
                }

            } catch (e: IOException) {
                Log.e(
                    TAG,
                    "loadSelectedDate: network exception (no network) ${e.message}  --//-- $e"
                )
                dateInformation = CalendarData(-1, -1, listOf())

            } catch (e: Exception) {
                Log.e(TAG, "loadSelectedDate: general exception ${e.message}  --//-- $e")

            }

        }
    }


    private fun getUserDateList() {

        Log.d(TAG, "getUserDateList: $userToken, cityId = $cityId")

        viewModelScope.launch {

            try {

                val webResponse = retrofitService.getFutureUsersDateList(
                    auth = userToken.token,
                    id = userToken.id,
                    idCity = cityId
                )
                Log.d(
                    TAG,
                    "call to retrofit done"
                )

                if (webResponse.isSuccessful) {

                    webResponse.body()?.let { data ->

                        userDays = data.map { futureDates ->
                            // val fields = futureDates.nextDate.split("-")

                            val fields = futureDates.nextDate.split("-")
                            try {
                                MyDate(fields[2].toInt(), fields[1].toInt(), fields[0].toInt())
                            } catch (e: NumberFormatException) {
                                Log.e(
                                    TAG,
                                    "getUserDateList: userList parse from String to MyDate",
                                    e
                                )
                                MyDate(-1, -1, -1)
                            }

                        }

                    }
                    Log.d(
                        TAG,
                        "data fetched $userDays"
                    )
                } else {
                    Log.e(
                        TAG,
                        "error fetching data from CalendarViewModel getFriends ${webResponse.errorBody()}"
                    )
                }

            } catch (e: IOException) {
                Log.e(
                    TAG,
                    "getUserDateList: network exception (no network) ${e.message}  --//-- $e"
                )

            } catch (e: Exception) {
                Log.e(TAG, "getUserDateList: general exception ${e.message}  --//-- $e")

            }

        }
    }

    fun getFriends() {

        Log.d(TAG, "getFriends: $selectedDate userToken $userToken")

        viewModelScope.launch {

            try {

                val webResponse = retrofitService.getUsersOnDateAsync(
                    auth = userToken.token,
                    id = userToken.id,
                    day = selectedDate.day,
                    month = selectedDate.month,
                    year = selectedDate.year,
                    idCity = cityId
                )
                Log.d(
                    TAG,
                    "call to retrofit done"
                )

                if (webResponse.isSuccessful) {

                    webResponse.body()?.let { data ->
                        userFriends = data

                    }
                    Log.d(
                        TAG,
                        "data fetched $userFriends"
                    )
                } else {
                    Log.e(
                        TAG,
                        "error fetching data from CalendarViewModel getFriends ${webResponse.errorBody()}"
                    )
                }

            } catch (e: IOException) {
                Log.e(TAG, "getFriends: network exception (no network) ${e.message}  --//-- $e")

            } catch (e: Exception) {
                Log.e(TAG, "getFriends: general exception ${e.message}  --//-- $e")

            }
        }
    }


    /**
     * Set the selected day to show relative information like people and events,
     *  besides checks if its needed to different a new month
     */
    fun setDate(myDate: MyDate) {

        if (selectedDate.month != myDate.month)
            calendar = ChipDayFactory.datesCreator(myDate) //Creates the calendar layout ( days of the week) from a day of a month

        selectedDate = myDate
        loadSelectedDate()
    }

    // event: addItem
    fun addDateToUserList(date: MyDate) {

        userDays = userDays + listOf(date)

        viewModelScope.launch {

            try {

                val webResponse = retrofitService.addDateAsync(
                    auth = userToken.token,
                    id = userToken.id,
                    dateCity = DateCityDTO(date.toLocalDate().toString(), cityId)
                )

                Log.d(
                    TAG,
                    "call to retrofit done"
                )

                if (webResponse.isSuccessful) {

                    Log.d(
                        TAG,
                        "data sent ${webResponse.body()}, id = ${webResponse.headers()["id"]}"
                    )

                } else {

                    removeDate(date)

                    Log.e(
                        TAG,
                        "error sending data from CalendarViewModel addDate ${webResponse.errorBody()}"
                    )
                }


            } catch (e: IOException) {
                removeDate(date)
                Log.e(TAG, "addDateToUserList: network exception (no network)  --//-- $e")

            } catch (e: Exception) {
                removeDate(date)
                Log.e(TAG, "addDateToUserList: general exception  --//-- $e --//--${e.stackTrace}")


            }
        }

    }

    // event: removeItem
    fun removeDateFromUserList(date: MyDate) {
        removeDate(date)

        viewModelScope.launch {

            try {

                val webResponse = retrofitService.removeDateAsync(
                    auth = userToken.token,
                    id = userToken.id,
                    dateCity = DateCityDTO(date.toLocalDate().toString(), cityId)
                )

                Log.d(
                    TAG,
                    "removeDateFromUserList: call to retrofit done"
                )

                if (webResponse.isSuccessful) {

                    Log.d(
                        TAG,
                        "removeDateFromUserList: data deleted ${webResponse.body()}"
                    )

                } else {

                    userDays = userDays + listOf(date)

                    Log.e(
                        TAG,
                        "removeDateFromUserList: error deleting data from CalendarViewModel removeDate body = $webResponse "
                    )
                }


            } catch (e: IOException) {
                Log.e(TAG, "removeDateFromUserList: network exception (no network)  --//-- $e")

            } catch (e: Exception) {
                Log.e(TAG, "removeDateFromUserList: general exception  --//-- $e --//--${e.stackTrace}")


            }
        }
    }


    private fun removeDate(date: MyDate) {
        // toMutableList makes a mutable copy of the list we can edit, then
        // assign the new list to todoItems (which is still an immutable list)
        userDays = userDays.toMutableList().also {
            it.remove(date)
        }
    }


}

private fun MyDate.toLocalDate(): LocalDate {
    return LocalDate.of(this.year, this.month, this.day)
}



