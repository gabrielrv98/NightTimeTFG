package com.esei.grvidal.nighttime.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate

private const val TAG = "CalendarViewModel"


data class CalendarData(
    val data: MyDate,
    val event: List<EventData>,
    val friends: Int,
    val total: Int
)

class CalendarViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _calendarData = MutableLiveData<CalendarData>()

    // The external immutable LiveData for the response String
    val calendarData: LiveData<CalendarData>
        get() = _calendarData



    private val _date = MutableLiveData<MyDate>()

    val date: LiveData<MyDate>
        get() = _date


    /**
     * Call login() on init so we can display get the token for future calls.
     */
    init {
        getDateInfo(LocalDate.now().toMyDate())
    }

    /**
     * Sets the value of the loggedUser LiveData token to the given by NightTime Api.
     */
    private fun getDateInfo(date: MyDate) {
        viewModelScope.launch {
            /*
            try {


                Log.e(TAG, "login: creating client")


                val webResponse = NightTimeApi.retrofitService.getPeopleOnDateAsync(userToken.id,date.day,date.month, date.year, cityId)

                if (webResponse.isSuccessful) {
                    val id = webResponse.headers()["id"]!!.toLong()
                    val token = webResponse.headers()["token"]!!

                    _calendarData.value =
                        CalendarData(date, listOf(), -1, -1)//todo finish

                    Log.e(TAG, "login: login successfully id-> $id  token -> $token")
                }


            } catch (e: IOException) {
                Log.e(TAG, "getDateInfo: network exception ${e.message}  --//-- $e")

            } catch (e: Exception) {
                Log.e(TAG, "getDateInfo: general exception ${e.message}  --//-- $e")

            } finally {
                if (_calendarData.value == null) {
                    _calendarData.value = CalendarData(LocalDate.now().toMyDate(), listOf(), -1, -1)
                    Log.d(TAG, "getDateInfo: value was empty")
                } else
                    Log.d(TAG, "getDateInfo: Everything correct")
            }

             */
        }
    }

}
