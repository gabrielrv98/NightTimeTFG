package com.esei.grvidal.nighttime.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.network.MarsProperty
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "UserViewModel"

class UserViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _loggedUser = MutableLiveData<UserToken>()

    // The external immutable LiveData for the response String
    val loggedUser: LiveData<UserToken>
        get() = _loggedUser

    val token: String
        get()  = _loggedUser.value?.token ?: ""
    /**
     * Call login() on init so we can display get the token for future calls.
     */
    init {
        login()
    }

    /**
     * Sets the value of the loggedUser LiveData token to the given by NightTime Api.
     */
    private fun login() {
        viewModelScope.launch {
            try {

                Log.e(TAG, "login: creating client")


                val webResponse = NightTimeApi.retrofitService.loginAsync("grvidal", "1234")//.await()

                if (webResponse.isSuccessful) {
                    val id = webResponse.headers()["id"]!!.toLong()
                    val token = webResponse.headers()["token"]!!

                    _loggedUser.value = UserToken(id, token)

                    Log.d(TAG, "login: login successfully id-> $id  token -> $token")
                }


            } catch (e: IOException) {
                Log.e(TAG, "login: network exception ${e.message}  --//-- $e")

            } catch (e: Exception) {
                Log.e(TAG, "login: general exception ${e.message}  --//-- $e")
            
            }finally {
                if ( _loggedUser.value == null ) {
                    _loggedUser.value = UserToken(-1L, "Empty")
                    Log.e(TAG, "login: value was empty")
                }else
                    Log.d(TAG, "login: Everything correct")
            }
            

        }
    }
}
