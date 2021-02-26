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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "UserViewModel"


enum class LoginState {
    LOADING, //Operation started
    NO_DATA_STORED, // No data stored to call login
    REFUSED, // Credentials don't match
    NO_NETWORK, // Network error
    ACCEPTED, // Credentials accepted
    EXCEPTION // Unexpected exception
}

class UserViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    var loggedUser by mutableStateOf(UserToken(-1, ""))

    val auth
        get() = loggedUser.token

    var loggingState by mutableStateOf(LoginState.LOADING)
        private set

    var credentialsChecked by mutableStateOf(false) // todo creo que puede ser una variable normal
        private set

    private suspend fun setLoggingData(username: String, password: String) {
        dataStoreManager.updateLoginCredentials(username, password)
    }

    fun logOff() {
        viewModelScope.launch {
            setLoggingData("", "")
        }
    }

    fun doLoginRefreshed(username: String, password: String) {
        viewModelScope.launch {
            setLoggingData(username, password)
            login()
        }
    }



    /**
     * Call login() on init so we can display get the token for future calls.
     */
    init {
        Log.d(TAG, "{tags: AssistLogging} init: starting User")
        doLogin()

    }

    fun doLogin() {
        viewModelScope.launch {
            login()
        }
    }

    /**
     * Sets the value of the loggedUser with the respond of NightTime Api.
     */
    private suspend fun login() {


        Log.e(TAG, "{tags: AssistLogging} login: creating client")
        loggingState = LoginState.LOADING

        var loginData = LoginData("", "",false)

        try {
            loginData = dataStoreManager.userPreferences.first()
            credentialsChecked = loginData.accepted

            Log.e(
                TAG,
                "{tags: AssistLogging} login: getting LoginData = ${loginData.username} : ${loginData.password} [ ${loginData.accepted} ] loggingState : ${loggingState.name}"
            )

            if (loginData.username == "" || loginData.password == "") {
                loggingState = LoginState.NO_DATA_STORED
                Log.e(
                    TAG,
                    "{tags: AssistLogging} login data was empty"
                )
            }


        } catch (e: NoSuchElementException) {
            loggingState = LoginState.NO_DATA_STORED

        }

        //delay(2000)

        if (loggingState == LoginState.LOADING) {


            try {

                val webResponse = NightTimeApi.retrofitService.loginAsync(
                    loginData.username,
                    loginData.password
                )
                Log.e(
                    TAG,
                    "{tags: AssistLogging} call to retrofit done"
                )

                if (webResponse.isSuccessful) {

                    //If the credentials weren't accepted until now
                    if (!loginData.accepted) {
                        dataStoreManager.credentialsChecked()
                        credentialsChecked = true
                    }

                    val id = webResponse.headers()["id"]!!.toLong()
                    val token = webResponse.headers()["token"]!!

                    loggedUser = UserToken(id, token)
                    loggingState = LoginState.ACCEPTED

                    Log.d(
                        TAG,
                        "{tags: AssistLogging} login: login successfully id-> $id  token -> $token"
                    )

                }else{
                    Log.d(
                        TAG,
                        "{tags: AssistLogging} login: login unsuccessfully  ${loginData.username} : ${loginData.password}"
                    )
                    dataStoreManager.credentialsFailed()
                    loggingState = LoginState.REFUSED
                }

            } catch (e: IOException) {
                loggingState = LoginState.NO_NETWORK
                Log.e(TAG, "login: network exception (no network) ${e.message}  --//-- $e")

            } catch (e: Exception) {
                Log.e(TAG, "login: general exception ${e.message}  --//-- $e")

            } finally {
                if (loggingState == LoginState.LOADING) {
                    Log.e(TAG, "Something unexpected happended")
                    loggingState = LoginState.EXCEPTION

                }
                Log.d(TAG, "login: LoggingState = ${loggingState.name}")

            }


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


