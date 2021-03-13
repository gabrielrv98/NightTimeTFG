package com.esei.grvidal.nighttime.data

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.network.BASE_URL
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import com.esei.grvidal.nighttime.network.USER_URL
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.StringBuilder

private const val TAG = "UserViewModel"


data class UserDTO(
    var id: Long,//user ID
    var name: String,
    var nickname: String,
    var state: String,
    var nextDate: NextDate? = null,
    var picture: String? = null
)

data class NextDate(
    val id: Long,
    val nextDate: String,
    val nextCity: CityDTO
){
    override fun toString(): String {
        val date = nextDate.split("-")

        return StringBuilder()
            .append(date[2])
            .append("-")
            .append(date[1])
            .append("-")
            .append(date[0])
            .append(" : ")
            .append(nextCity.name)
            .toString()
    }
}

data class CityDTO(
    val id: Long,
    val name: String,
    val country: String
)

class UserViewModel : ViewModel() {

    private var userToken = UserToken(-1, "")

    fun setUserToken(loggedUser: UserToken) {

        Log.d(TAG, "setUserToken: old token = $userToken, new $loggedUser")
        userToken = loggedUser
    }

    fun getMyId(): Long {
        return userToken.id
    }

    // Internal Uri to upload a new profile picture
    var uriPhoto by mutableStateOf<Uri?>(null)

    // User profile picture
    var userPicture by mutableStateOf<ImageAsset?>(null)

    // Drawable for placeholder and error
    var userDrawable by mutableStateOf<Drawable?>(null)

    // User data
    var user by mutableStateOf(UserEmpty.getEmptyUser())
        private set

    var lock: Boolean = false



    /**
     * Call login() on init so we can display get the token for future calls.
     */
    init {
        Log.d(TAG, "{tags: AssistLogging} init: starting User")

    }

    fun eraseData() {

        if(!lock) {
            Log.d(TAG, "eraseData: erasing all data")
            user = UserEmpty.getEmptyUser()
            uriPhoto = null
            userPicture = null
            userDrawable = null
        }else
            Log.d(TAG, "eraseData: data is locked")
    }

    fun fetchData(userId: Long) {
        eraseData()

        viewModelScope.launch {
            fetchUser(userId) // Get data from new user

            if (!user.picture.isNullOrEmpty()) {
                fetchPhoto(userId)
            }
        }
    }

    private suspend fun fetchUser(userId: Long) {
        try {

            val webResponse = NightTimeApi.retrofitService.getUserDetails(userId)
            Log.d(
                TAG,
                "fetchUser: call to retrofit done userId $userId"
            )

            if (webResponse.isSuccessful) {

                webResponse.body()?.let { userDTO ->
                    user = userDTO

                }

                Log.d(
                    TAG,
                    "fetchUser: user received $user"
                )

            } else {
                Log.d(
                    TAG,
                    "fetchUser: user error  $webResponse"
                )
            }

        } catch (e: IOException) {
            Log.e(TAG, "login: network exception (no network) ${e.message}  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "login: general exception ${e.message}  --//-- $e")

        }

    }


    private fun fetchPhoto(userId: Long) {


        val picasso = Picasso.get()

        picasso
            .load("$BASE_URL$USER_URL$userId/photo")
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_broken_image)
            .resize(500, 500)
            .centerCrop()
            .into(
                object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        Log.d(
                            TAG,
                            "fetchPhotos: onPrepareLoad: loading image user id $userId"
                        )
                        userDrawable = placeHolderDrawable
                        userPicture = null
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        //Handle the exception here
                        Log.d(TAG, "fetchPhotos: onBitmapFailed: error $e")
                        userDrawable = errorDrawable
                        userPicture = null
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                        bitmap?.let { img ->
                            //Here we get the loaded image
                            Log.d(
                                TAG,
                                "fetchPhotos: onBitmapLoaded: Image fetched size ${img.byteCount} height ${img.height}, width ${img.width}"
                            )
                            userPicture = img.asImageAsset()
                        }

                    }
                }
            )

    }
}

object UserEmpty {
    fun getEmptyUser(): UserDTO {
        return UserDTO(
            id = -1,
            name = "",
            nickname = "",
            state = "",
            nextDate = null,
            picture = null
        )
    }
}





