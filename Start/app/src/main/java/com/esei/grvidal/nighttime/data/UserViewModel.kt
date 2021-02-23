package com.esei.grvidal.nighttime.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.datastore.DataStoreManager
import com.esei.grvidal.nighttime.datastore.LoginData
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "UserViewModel"

enum class NetworkState {
    WORKING,
    ERROR,
    LOADING
}

enum class LoginState {
    LOADING,
    NO_DATA_STORED,
    REFUSED, //TODO  by the api or by the network?? does it matter??
    ACCEPTED
}

class UserViewModel(
    private val dataStoreManager: DataStoreManager
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

    var loggingState by mutableStateOf(LoginState.LOADING)


    private suspend fun setLoggingData(username: String, password: String) {

        dataStoreManager.updateLoginCredentials(username, password)

    }

    fun logOff(){
        viewModelScope.launch {
            setLoggingData("","")
        }
    }

    public fun doLoginRefreshed(username: String, password: String) {
        viewModelScope.launch {
            setLoggingData(username, password)
            login()
        }
    }



    /**
     * Call login() on init so we can display get the token for future calls.
     */
    init {
        Log.d(TAG, "{tags: AssistLoggin} init: iniciando User")
        //login()

    }

    public fun doLogin(){
        viewModelScope.launch {
            login()
        }
    }

    /**
     * Sets the value of the loggedUser with the respond of NightTime Api.
     */
    private suspend fun login() {


            Log.e(TAG, "{tags: AssistLoggin} login: creating client thread name = ${Thread.currentThread().name}")
            loggingState = LoginState.LOADING

            var loginData = LoginData("", "")

            try {
                loginData = dataStoreManager.userPreferences.first()

                Log.e(
                    TAG,
                    "{tags: AssistLoggin} login: getting LoginData = ${loginData.username} : ${loginData.password} loggingState : ${loggingState.name}"
                )

                if (loginData.username == "" || loginData.password == ""){
                    loggingState = LoginState.NO_DATA_STORED
                    Log.e(
                        TAG,
                        "{tags: AssistLoggin} login data was empty"
                    )
                }


            } catch (e: NoSuchElementException) {
                loggingState = LoginState.NO_DATA_STORED

            }

            //delay(2000)

            //loggingState = LoginState.NO_DATA_STORED
            if(loggingState == LoginState.LOADING ) {


                    try {
                        Log.e(
                            TAG,
                            "{tags: AssistLoggin} loggingState was loading"
                        )

                        Log.e(TAG, "{tags: AssistLoggin} login: creating client thread name = ${Thread.currentThread().name}")

                        val webResponse = NightTimeApi.retrofitService.loginAsync(
                            loginData.username,
                                loginData.password
                            )
                        Log.e(
                            TAG,
                            "{tags: AssistLoggin} call to retrifit done"
                        )

                        networkState = NetworkState.WORKING

                        if (webResponse.isSuccessful) {
                            val id = webResponse.headers()["id"]!!.toLong()
                            val token = webResponse.headers()["token"]!!

                            loggedUser = UserToken(id, token)

                            Log.d(
                                TAG,
                                "{tags: AssistLoggin} login: login successfully id-> $id  token -> $token"
                            )

                        }else{
                            Log.e(
                                TAG,
                                "{tags: AssistLoggin} response was failure"
                            )
                        }


                    } catch (e: IOException) {
                        Log.e(TAG, "login: network exception ${e.message}  --//-- $e")


                    } catch (e: Exception) {
                        Log.e(TAG, "login: general exception ${e.message}  --//-- $e")

                    } finally {
                        if (loggedUser.id == -1L) {
                            networkState = NetworkState.ERROR
                            Log.e(TAG, "login: id = -1")
                            loggingState = LoginState.REFUSED

                        } else {
                            Log.d(TAG, "login: Everything correct")
                            loggingState = LoginState.ACCEPTED
                        }

                        Log.d(TAG, "login: networkState: ${networkState.name}")
                    }


            }else{
                Log.e(
                    TAG,
                    "{tags: AssistLoggin} loggingState was different from loading"
                )
            }

        }


}


class UserViewModelFactory(
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return UserViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}


