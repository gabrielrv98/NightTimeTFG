package com.esei.grvidal.nighttime.data


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.datastore.DataStoreManager
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "CityViewModel"


data class City(val id: Long, val name: String)

class CityViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    var city by mutableStateOf(City(-1, "Ourense"))
        private set

    var allCities by mutableStateOf(listOf<City>())
        private set

    var showDialog by mutableStateOf(false)
        private set

    fun setDialog(state: Boolean) {
        showDialog = state
    }

    /**
     * Call getCityFromPreferences() on init so we can display the last city used
     */
    init {
        Log.d(TAG, "init: starting City")
        getCityFromPreferences()
        getCitiesRepository() // Only get all the cities once per application start
    }

    private fun getCityFromPreferences() {
        viewModelScope.launch {
            city = try {
                dataStoreManager.cityPreferences.first()
            } catch (e: NoSuchElementException) {
                City(-1, "Eligue Ciudad")

            }
        }
    }

    fun setCity(id: Long, name: String) {
        viewModelScope.launch {
            dataStoreManager.updateCityData(id, name)
            city = City(id, name)
        }
    }

    private fun getCitiesRepository() {
        viewModelScope.launch {
            fetchCitiesFromApi()
        }
    }

    private suspend fun fetchCitiesFromApi() {
        try {
            val webResponse = NightTimeApi.retrofitService.getAllCitiesAsync()
            Log.e(
                TAG,
                "getCitiesFromApi: Call to retrofit done"
            )

            if (webResponse.isSuccessful) {
                webResponse.body()?.let {
                    allCities = it
                }

                Log.d(TAG, "getCitiesFromApi: List of cities = ${webResponse.body()}")

            } else {
                Log.d(TAG, "getCitiesFromApi: webResponse was unSucesful")
            }

        } catch (e: IOException) {
            Log.e(TAG, "getCitiesFromApi: network exception (no network) ${e.message}  --//-- $e")
            allCities = CitiesNoInternet.cities
        } catch (e: Exception) {
            Log.e(TAG, "getCitiesFromApi: general exception ${e.message}  --//-- $e")

        }

    }
}

object CitiesNoInternet {
    val cities = listOf(
        City(1, "Ourense"),
        City(2, "Vigo"),
        City(3, "Pontevedra"),
        City(4, "Coruña"),
        City(5, "Allariz"),
        City(6, "Lugo"),
        City(7, "Rivadavia"),
    )
}


class CityViewModelFactory(
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(CityViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return CityViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}


