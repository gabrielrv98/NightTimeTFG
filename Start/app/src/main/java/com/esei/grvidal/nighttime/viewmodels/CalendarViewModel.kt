package com.esei.grvidal.nighttime.viewmodels

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageAsset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.chipdayfactory.ChipDayFactory
import com.esei.grvidal.nighttime.chipdayfactory.MyDate
import com.esei.grvidal.nighttime.chipdayfactory.toMyDate
import com.esei.grvidal.nighttime.fakeData.*
import com.esei.grvidal.nighttime.network.BASE_URL
import com.esei.grvidal.nighttime.network.DateCityDTO
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi.retrofitService
import com.esei.grvidal.nighttime.network.USER_URL
import com.esei.grvidal.nighttime.network.network_DTOs.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate

private const val TAG = "CalendarViewModel"


class CalendarViewModel(
    val userToken: UserToken,
    var cityId: Long
) : ViewModel() {

    @JvmName("setCityId1")
    fun setCityId(id: Long) {
        Log.d(TAG, "setCityId: old id = $cityId, new $id")

        if (cityId != id) { // If the city has been updated, value is set and data is fetched
            cityId = id

            Log.d(TAG, "setCityId: fetching data")
            // TODO: 06/09/2021 fake data setCity
            //loadSelectedDate()//Loading the actual day
            //getUserDateList() // Fetch from api the list of dates in the selected city selected by the user
            fakeLoadSelectedDate()
            fakeUserDateList()
        }
    }

    // Strong reference point to avoid loosing them
    private var targetList = mutableListOf<Target>()


    // Remembered date, it's used to show the selected date and move the calendar to the specified month
    var selectedDate by mutableStateOf(LocalDate.now().toMyDate())
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

    var userFriends by mutableStateOf(listOf<UserSnapImage>())
        private set

    private var pageUserFriends by mutableStateOf(0)

    init {
        // TODO: 06/09/2021 fake data init
        //loadSelectedDate()//Loading the actual day
        //getUserDateList() // Fetch from api the list of dates in the selected city selected by the user
        fakeLoadSelectedDate()
        fakeUserDateList()
    }


    private fun fakeLoadSelectedDate() {
        //call api
        Log.d(TAG, "load: $selectedDate userToken $userToken")

        var eventList = listOf<EventData>()

        barListFakeData.forEach { bar ->
            eventList = eventList.toMutableList()
                .apply {

                    bar.events
                        .filter { it.date == selectedDate.toLocalDate().toString() }
                        .forEach { event ->
                            add(event)
                        }
                }
        }
        var total = 0
        allUsersList.forEach { userChecking ->
            userChecking.nextDates.forEach { dc ->
                if (dc.nextDate.toMyDate() == selectedDate) {
                    total++

                }
            }
        }
        var friends = 0
        friendList.filter { it.answer == AnswerOptions.YES }// Filter all friends with true
            .onEach { friendship ->                       // Checks all dates looking for the selected one
                friendship.userAsk.nextDates
                    .forEach { dateCity ->
                        if (dateCity.nextDate == selectedDate.toLocalDate()) {
                            friends++
                        }
                    }

            }

        dateInformation = CalendarData(total, friends, eventList)
        fakeGetFriendsOnSelectedDate()
        Log.d(
            TAG,
            "data fetched $dateInformation"
        )


    }

    private fun fakeUserDateList() {
        userDays = if (cityId == cityOu.id)
            grvidal.nextDates.map { dateCity ->
                MyDate(
                    dateCity.nextDate.dayOfMonth,
                    dateCity.nextDate.monthValue,
                    dateCity.nextDate.year
                )
            }
        else
            listOf()
    }

    private fun loadSelectedDate() = viewModelScope.launch {
        //call api
        Log.d(TAG, "load: $selectedDate userToken $userToken")

        try {

            val webResponse = retrofitService.getPeopleAndEventsOnDateAsync(
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

    fun fakeGetFriendsOnSelectedDate() {
        val finalList = mutableSetOf<User>()
        friendList
            .filter { it.answer == AnswerOptions.YES } // filter only friends
            .toList()
            .onEach { friendship ->
                friendship.userAsk.nextDates.onEach { dateCity -> // check their selected dates
                    if (dateCity.nextDate == selectedDate.toLocalDate())// if there is a match, add it to the list
                        finalList.add(friendship.userAsk)
                }
            }

        userFriends = finalList.map {
            UserSnapImage(
                it.id,
                it.nickname,
                it.name,
                it.picture != null,
                null
            )
        }.toList()
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

    fun getFriendsOnSelectedDate() = viewModelScope.launch {


        Log.d(TAG, "getFriendsOnSelectedDate: $selectedDate userToken $userToken")

        try {

            val webResponse = retrofitService.getUsersOnDateAsync(
                auth = userToken.token,
                id = userToken.id,
                day = selectedDate.day,
                month = selectedDate.month,
                year = selectedDate.year,
                idCity = cityId,
                page = pageUserFriends
            )

            Log.d(
                TAG,
                "getFriendsOnSelectedDate: call to retrofit done"
            )
            Log.d(
                TAG,
                "getFriendsOnSelectedDate: userList size before page $pageUserFriends-> ${userFriends.size}"
            )
            pageUserFriends += 1

            if (webResponse.isSuccessful) {

                webResponse.body()?.let { data ->
                    userFriends = userFriends + data.map { userSnap ->
                        userSnap.toUserSnapImage(null)
                    }

                    Log.d(
                        TAG,
                        "getFriendsOnSelectedDate: data fetched ${data.size}"
                    )
                    fetchPhotosUserSnap()


                }

                Log.d(TAG, "getFriendsOnSelectedDate: userList size after ${userFriends.size}")
            } else {
                Log.e(
                    TAG,
                    "getFriendsOnSelectedDate: error fetching data from CalendarViewModel getFriends ${webResponse.errorBody()}"
                )
            }

        } catch (e: IOException) {
            Log.e(
                TAG,
                "getFriendsOnSelectedDate: network exception (no network) ${e.message}  --//-- $e"
            )

        } catch (e: Exception) {
            Log.e(TAG, "getFriendsOnSelectedDate: general exception ${e.message}  --//-- $e")

        }

    }

    private fun fetchPhotosUserSnap() {
        Log.d(TAG, "fetchPhotosUserSnap starting to fetch photos")
        for (user in userFriends) {

            if (user.hasImage && user.img == null) {
                loadImage(
                    url = "$BASE_URL$USER_URL${user.userId}/photo",
                    userId = user.userId
                )
            }
        }
    }

    private fun loadImage(
        url: String,
        userId: Long,
        picasso: Picasso = Picasso.get()
    ) {

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d(
                    TAG,
                    "fetchPhotos: onPrepareLoad: loading image user id $userId"
                )
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                //Handle the exception here
                Log.d(TAG, "fetchPhotos: onBitmapFailed: error $e")
                targetList.remove(this)
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {


                //Here we get the loaded image
                Log.d(
                    TAG,
                    "fetchPhotos: onBitmapLoaded: Image fetched size ${bitmap?.byteCount} height ${bitmap?.height}, width ${bitmap?.width}"
                )

                bitmap?.let { img ->
                    userFriends.filter { chat -> chat.userId == userId }.getOrNull(
                        0
                    )?.let { user ->

                        // Recomposition is made when the setter of the list is called,
                        // so we need to delete, edit (adding the picture) and re-add the element
                        userFriends = userFriends.toMutableList().also {
                            it.remove(user)
                            user.img = img.asImageAsset()

                        } + listOf(user)



                        Log.d(
                            TAG,
                            "onBitmapLoaded: getting Image user $userId size ${img.byteCount} height ${img.height}, width ${img.width}"
                        )
                    }
                }
                targetList.remove(this)


            }
        }
        targetList.add(target)

        picasso
            .load(url)
            .resize(250, 250)
            .centerCrop()
            .into(target)

    }


    /**
     * Set the selected day to show relative information like people and events,
     *  besides checks if its needed to different a new month
     */
    fun setDate(myDate: MyDate) {

        Log.d(TAG, "setDate: setting new Date $myDate")

        if (selectedDate.month != myDate.month)
            calendar =
                ChipDayFactory.datesCreator(myDate) //Creates the calendar layout ( days of the week) from a day of a month

        selectedDate = myDate
        userFriends = listOf()
        pageUserFriends = 0

        // TODO: 06/09/2021 Fake data loadSelectedDate
        //loadSelectedDate()
        fakeLoadSelectedDate()
    }


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
                Log.e(
                    TAG,
                    "removeDateFromUserList: general exception  --//-- $e --//--${e.stackTrace}"
                )


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