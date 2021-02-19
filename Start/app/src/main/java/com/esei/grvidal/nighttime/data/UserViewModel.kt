package com.esei.grvidal.nighttime.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue 
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.datastore.DataStoreManager
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "UserViewModel"

enum class NetworkState {
    WORKING,
    ERROR,
    LOADING
}

class UserViewModel(
   // val dataStoreManager: DataStoreManager
    ) : ViewModel() {

    var loggedUser by mutableStateOf(UserToken(-1, ""))

    val auth
        get() = loggedUser.token

    var networkState by mutableStateOf(NetworkState.LOADING)
        // Returns a State<> allowing compose to recompose when it changes
        private set

    val isNetworkDown: Boolean
        get() = networkState == NetworkState.ERROR

    val isNetworkWorking: Boolean
        get() = networkState == NetworkState.WORKING


    val isNetworkLoading: Boolean
        get() = networkState == NetworkState.LOADING
    /**
     * Call login() on init so we can display get the token for future calls.
     */
    init {
        Log.d(TAG, "init: iniciando User")
        login("grvidal", "1234")
    }

    /**
     * Sets the value of the loggedUser with the respond of NightTime Api.
     */
    private fun login(username : String, password: String) {
        viewModelScope.launch { // Start a new thread
            try {
                Log.e(TAG, "login: creating client")

                val webResponse =
                    NightTimeApi.retrofitService.loginAsync(username, password)

                networkState = NetworkState.WORKING

                if (webResponse.isSuccessful) {
                    val id = webResponse.headers()["id"]!!.toLong()
                    val token = webResponse.headers()["token"]!!

                    loggedUser = UserToken(id, token)

                    Log.d(TAG, "login: login successfully id-> $id  token -> $token")

                }


            } catch (e: IOException) {
                Log.e(TAG, "login: network exception ${e.message}  --//-- $e")


            } catch (e: Exception) {
                Log.e(TAG, "login: general exception ${e.message}  --//-- $e")

            } finally {
                if (loggedUser.id == -1L) {
                    networkState = NetworkState.ERROR
                    Log.e(TAG, "login: id = -1")

                } else {
                    Log.d(TAG, "login: Everything correct")
                }

                Log.d(TAG, "login: networkState: ${networkState.name}")
            }

        }

    }
}
