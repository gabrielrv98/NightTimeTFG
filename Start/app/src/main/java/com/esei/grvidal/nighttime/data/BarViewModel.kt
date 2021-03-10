package com.esei.grvidal.nighttime.data

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.network.BAR_URL
import com.esei.grvidal.nighttime.network.BASE_URL
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
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

class BarViewModel : ViewModel() {

    object EmptyBar {
        val bar = BarDTO(
            -1,
            "ERROR",
            "error",
            ".",
            "description",
            "",
            ",",
            ",",
            "",
            "",
            "",
            ""
        )
    }

    //TODO refactor all this

    // List with all the loaded bars
    var barList by mutableStateOf(listOf<BarDTO>())

    var selectedBar by mutableStateOf(EmptyBar.bar)
        private set

    // Total of photos of the bar
    var totalNPhotos by mutableStateOf(0)
        private set

    // Number of downloaded photos
    var nPhotos by mutableStateOf(0)
        private set

    // Strong reference point to avoid loosing them
    private var targetList = mutableListOf<Target>()

    // Photos from the selected bar
    var barSelectedPhotos by mutableStateOf(listOf<ImageAsset>())

    // Events from the selected bar
    var barSelectedEvents by mutableStateOf(listOf<EventFromBar>())

    // Index of the page for data pagination
    private var page: Int = 0

    // Last city selected, it is initialized to -1, so when its called on the first time it will fetch the new data
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

    /**
     * Starts a new coroutine to fetch data from internet and add 1 to the index page
     */
    fun loadBarsOnCity() {

        if (city.id == -1L) {
            barList = BarOfflineList.bars
        } else {
            viewModelScope.launch {
                fetchBars(page++)
            }
        }

    }

    /**
     * Suspend function that request a call to the Api service and sets the new data into [barList]
     */
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

    /**
     * Returns the bar with the same id as [requestedID]
     * And request the api for the rest of the data
     */
    fun getSelectedBarDetails(requestedID: Long) {

        //Remove old data or we will show it for a few seconds
        eraseSelectedBar()

        Log.d(TAG, "getSelectedBarDetails: data erased, fetching new data, size ${barList.size}")

        // Create a coroutine to fetch photos and events
        viewModelScope.launch {
            fetchBarDetails(id = requestedID)
        }

        selectedBar = barList.filter { bar -> bar.id == requestedID }.getOrNull(0) ?: BarDTO(
            -1,
            "ERROR",
            "error",
            ".",
            "description",
            "",
            ",",
            ",",
            "",
            "",
            "",
            ""
        )

        Log.d(TAG, "getSelectedBarDetails: selected bar = $requestedID, name = ${selectedBar.name}")
    }

    private suspend fun fetchBarDetails(id: Long) {
        try {

            val webResponse = NightTimeApi.retrofitService.getBarDetails(id)
            Log.d(TAG, "fetchBarDetails: Api call done")

            if (webResponse.isSuccessful) {

                webResponse.body()?.let { barDetails ->
                    Log.e(TAG, "fetchBarDetails: events   -> $barDetails")
                    barSelectedEvents = barDetails.events
                    totalNPhotos = barDetails.photos

                }
                Log.d(
                    TAG,
                    "fetchBarDetails: Response Successful, photos size = $totalNPhotos, events size = ${barSelectedEvents.size}"
                )
            } else {
                Log.e(TAG, "fetchBarDetails: Response  unsuccessful")
            }
        } catch (e: IOException) {
            Log.e(TAG, "fetchBarDetails: network exception (no network) --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "fetchBarDetails: general error --//-- $e")

        }
    }

    fun addImage(img: Bitmap?) {
        img?.let {
            Log.d(TAG, "addImage: Adding img number ${barSelectedPhotos.size + 1}")
            barSelectedPhotos = barSelectedPhotos + listOf(img.asImageAsset())
        }
    }


    fun eraseSelectedBar() {
        barSelectedEvents = listOf()
        barSelectedPhotos = listOf()
        targetList = mutableListOf()
        totalNPhotos = 0
        nPhotos = 0
        selectedBar = EmptyBar.bar
    }

    fun fetchPhotos() {

        if (nPhotos < totalNPhotos) {
            val picasso = Picasso.get()
            var url: String
            var nextId: Int

            // If there are at least 5 pictures more, get them, if there are less, fetch the rest of them
            val next =
                if (nPhotos + 5 <= totalNPhotos) 5 else totalNPhotos - nPhotos

            val start = nPhotos

            nPhotos += next
            for (i in 0 until next) {
                nextId = start + i
                Log.d(TAG, "ShowDetails: bar ${selectedBar.id}, idPhoto $nextId")

                url = BASE_URL + BAR_URL + selectedBar.id + "/photo/$nextId"
                Log.d(TAG, "ShowDetails: url $url")

                loadImage(url,picasso = picasso)

            }
        }
    }

    private fun loadImage(url: String, picasso: Picasso){

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d(
                    TAG,
                    "fetchPhotos: onPrepareLoad: loading nImages size ${barSelectedPhotos.size}"
                )
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                //Handle the exception here
                Log.d(TAG, "fetchPhotos: onBitmapFailed: error $e")
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                //Here we get the loaded image
                Log.d(
                    TAG,
                    "fetchPhotos: onBitmapLoaded: Image fetched size ${barSelectedPhotos.size}"
                )
                addImage(bitmap)
                targetList.remove(this)

            }
        }
        targetList.add(target)

        picasso
            .load(url)
            .into(target)

    }





}

object BarOfflineList {

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
