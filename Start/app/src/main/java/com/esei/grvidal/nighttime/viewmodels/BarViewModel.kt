package com.esei.grvidal.nighttime.viewmodels

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.platform.ContextAmbient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.fakeData.Bar
import com.esei.grvidal.nighttime.fakeData.barListFakeData
import com.esei.grvidal.nighttime.fakeData.cityOu
import com.esei.grvidal.nighttime.network.BAR_URL
import com.esei.grvidal.nighttime.network.BASE_URL
import com.esei.grvidal.nighttime.network.network_DTOs.BarDTO
import com.esei.grvidal.nighttime.network.network_DTOs.EventFromBar
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "BarViewModel"

class BarViewModel : ViewModel() {

    // List with all the loaded bars
    var barList by mutableStateOf(listOf<BarDTO>())

    var selectedBar by mutableStateOf(
        BarDTO(
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
    )
        private set

    // Total of photos of the bar
    //var totalNPhotos by mutableStateOf(0)
    var totalNPhotos by mutableStateOf(0)
        private set

    // Number of downloaded photos
    //var nPhotos by mutableStateOf(0)
    var nPhotos by mutableStateOf(0)
        private set

    // Strong reference point to avoid loosing them
    private var targetList = mutableListOf<Target>()

    // Photos from the selected bar
    var barSelectedPhotos by mutableStateOf(listOf<ImageAsset>())

    // TODO fake info photos
    var barSelectedResources by mutableStateOf(listOf<Int>())


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
                // TODO: 06/09/2021 FAKE DATA bar list
                //loadBarsOnCity()//Loading the first page
                fakeLoadBarsOnCity()
            }
        }


    fun fakeLoadBarsOnCity() {
        if (city == cityOu)
            barList = barListFakeData.map {
                BarDTO(
                    it.id,
                    it.name,
                    it.owner,
                    it.address,
                    it.description ?: "",
                    it.mondaySchedule,
                    it.tuesdaySchedule,
                    it.wednesdaySchedule,
                    it.thursdaySchedule,
                    it.fridaySchedule,
                    it.saturdaySchedule,
                    it.sundaySchedule
                )
            }
    }

    fun fakeGetBarDetails(id: Long) {
        val bar = barListFakeData.find { it.id == id } ?: Bar(
            "ERROR",
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
            cityOu
        )

        selectedBar = BarDTO(
            bar.id,
            bar.name,
            bar.owner,
            bar.address,
            bar.description ?: "",
            bar.mondaySchedule,
            bar.tuesdaySchedule,
            bar.wednesdaySchedule,
            bar.thursdaySchedule,
            bar.fridaySchedule,
            bar.saturdaySchedule,
            bar.sundaySchedule
        )
        totalNPhotos= bar.photos.size
        nPhotos = bar.photos.size
        barSelectedEvents = bar.events.map{
            EventFromBar(it.id,it.description,it.date)
        }

        barSelectedResources = bar.photos


    }

    /**
     * Starts a new coroutine to fetch data from internet and add 1 to the index page
     */
    fun loadBarsOnCity() {

        if (city.id != -1L) {
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
                    Log.d(TAG, "fetchBars: data fetched $bars")
                }


            }


        } catch (e: IOException) {
            Log.e(TAG, "fetchBars: network exception ${e.message}  --//-- $e")

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


    fun eraseSelectedBar() {
        barSelectedEvents = listOf()
        barSelectedPhotos = listOf()
        targetList = mutableListOf()
        totalNPhotos = 0
        nPhotos = 0
        selectedBar = BarDTO(
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

    fun fetchPhotos() {

        if (nPhotos < totalNPhotos) {
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

                loadImage(url)

            }
        }
    }

    private fun loadImage(url: String) {
        val picasso = Picasso.get()

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

                bitmap?.let { img ->
                    Log.d(
                        TAG,
                        "fetchPhotos: onBitmapLoaded: Image fetched size ${barSelectedPhotos.size}, size ${img.byteCount} height ${img.height}, width ${img.width}"
                    )
                    barSelectedPhotos = barSelectedPhotos + listOf(img.asImageAsset())
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
}