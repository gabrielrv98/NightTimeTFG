package com.esei.grvidal.nighttime.data

import android.net.Uri
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
import kotlin.system.exitProcess

private const val TAG = "UserViewModel"


class UserViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private var userToken = UserToken(-1, "")

    fun setUserToken(loggedUser: UserToken) {

        Log.d(TAG, "setUserToken: old token = $userToken, new $loggedUser")
        userToken = loggedUser
    }

    var uriPhoto by mutableStateOf<Uri?>(null)

    var user by mutableStateOf<User?>(null)


    /**
     * Call login() on init so we can display get the token for future calls.
     */
    init {
        Log.d(TAG, "{tags: AssistLogging} init: starting User")

    }


    private suspend fun login(loginData: LoginData) {
        try {

            val webResponse = NightTimeApi.retrofitService.loginAsync(
                loginData.username,
                loginData.password
            )
            Log.d(
                TAG,
                "{tags: AssistLogging} call to retrofit done"
            )

            if (webResponse.isSuccessful) {

                //If the credentials weren't accepted until now
                if (!loginData.accepted) {
                    dataStoreManager.credentialsChecked()
                }

                val id = webResponse.headers()["id"]!!.toLong()
                val token = webResponse.headers()["token"]!!


                Log.d(
                    TAG,
                    "{tags: AssistLogging} login: login successfully id-> $id  token -> $token"
                )

            } else {
                Log.d(
                    TAG,
                    "{tags: AssistLogging} login: login unsuccessfully  ${loginData.username} : ${loginData.password}"
                )
                dataStoreManager.credentialsFailed()
            }

        } catch (e: IOException) {
            Log.e(TAG, "login: network exception (no network) ${e.message}  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "login: general exception ${e.message}  --//-- $e")

        } finally {
            Log.e(TAG, "Something unexpected happended")

        }

    }

    fun getMyId(): Long { return userToken.id }
}





