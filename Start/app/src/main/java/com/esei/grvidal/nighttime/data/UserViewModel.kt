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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.network.BASE_URL
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import com.esei.grvidal.nighttime.network.USER_URL
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException


private const val TAG = "UserViewModel"


data class UserFull(
    var id: Long,//user ID
    var nickname: String,
    var name: String,
    var password: String,
    var state: String = "",
    var email: String,
    var nextDate: NextDate? = null,
    var picture: String? = null
)

data class UserViewPrivate(
    val name: String,
    val password: String,
    val state: String? = null,
    val email: String
)

data class UserDTOEdit(
    var id: Long,
    val name: String?,
    val password: String?,
    val state: String? = null,
    val email: String?
)

data class UserDTO(
    var id: Long,//user ID
    var name: String,
    var nickname: String,
    var state: String,
    var nextDate: NextDate? = null,
    var picture: String? = null
) {
    fun toUser(): UserFull {
        return UserFull(
            id = id,
            nickname = nickname,
            name = name,
            password = "",
            state = state,
            email = "",
            nextDate = nextDate,
            picture = picture
        )
    }
}

data class NextDate(
    val id: Long,
    val nextDate: String,
    val nextCity: CityDTO
) {
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

data class UserDTOInsert(
    val name: String,
    val nickname: String,
    var password: String,
    val state: String? = null,
    val email: String
)

enum class PhotoState {
    LOADING,
    ERROR,
    DONE
}

class UserViewModel : ViewModel() {

    private var userToken = UserToken(-1, "")

    fun setUserToken(loggedUser: UserToken) {

        Log.d(TAG, "setUserToken: old token = $userToken, new $loggedUser")
        userToken = loggedUser
    }

    fun getMyId(): Long {
        return userToken.id
    }

    // User profile picture
    var userPicture by mutableStateOf<ImageAsset?>(null)

    // Internal Uri to load new profile picture with Picasso
    var uriPhotoPicasso by mutableStateOf<Uri?>(null)

    var photoState by mutableStateOf(PhotoState.DONE)

    // User data
    var user by mutableStateOf(UserEmpty.getEmptyUser())
        private set

    var username by mutableStateOf(TextFieldValue())

    var name by mutableStateOf(TextFieldValue())

    var state by mutableStateOf(TextFieldValue())

    var password by mutableStateOf(TextFieldValue())

    var email by mutableStateOf(TextFieldValue())

    var errorText by mutableStateOf("")

    var lock by mutableStateOf(false)


    fun eraseData() {

        if (!lock) {
            Log.d(TAG, "eraseData: erasing all data")
            uriPhotoPicasso = null
            userPicture = null // Is the most important to release
        }
    }

    private fun invalidatePicasso(userId: Long) {
        Picasso.get().invalidate("$BASE_URL$USER_URL${userId}/photo")
    }

    fun fetchData(userId: Long) {
        eraseData()

        viewModelScope.launch {
            fetchUser(userId) // Get data from new user

            if (!user.picture.isNullOrEmpty()) {
                fetchPhoto(userId)
            }
            delay(500)
            fetchUser(userId) // Get data again from the user in case the edit delayed a bit // todo check if this is ok or is stupid
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
                    user = userDTO.toUser()
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
            Log.e(TAG, "fetchUser: network exception (no network) ${e.message}  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "fetchUser: general exception ${e.message}  --//-- $e")

        }

    }


    private fun fetchPhoto(userId: Long) {

        val picasso = Picasso.get()
        Log.d(TAG, "fetchPhoto starting to fetch photo")


        picasso
            .load("$BASE_URL$USER_URL$userId/photo")
            .resize(500, 500)
            .centerCrop()
            .into(
                object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        Log.d(
                            TAG,
                            "fetchPhotos: onPrepareLoad: loading image user id $userId"
                        )
                        photoState = PhotoState.LOADING
                        userPicture = null
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        //Handle the exception here
                        Log.d(TAG, "fetchPhotos: onBitmapFailed: error $e")
                        photoState = PhotoState.ERROR
                        userPicture = null
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                        bitmap?.let { img ->
                            //Here we get the loaded image
                            Log.d(
                                TAG,
                                "fetchPhotos: onBitmapLoaded: Image fetched user $userId size ${img.byteCount} height ${img.height}, width ${img.width}"
                            )
                            photoState = PhotoState.DONE
                            userPicture = img.asImageAsset()
                        }

                    }
                }
            )

    }

    fun fetchEditData() {
        viewModelScope.launch {
            try {

                val webResponse =
                    NightTimeApi.retrofitService.getUserPrivate(userToken.token, userToken.id)

                if (webResponse.isSuccessful) {

                    webResponse.body()?.let { userViewPrivate ->

                        name = TextFieldValue(userViewPrivate.name)
                        email = TextFieldValue(userViewPrivate.email)
                        password = TextFieldValue(userViewPrivate.password)
                        state = TextFieldValue(userViewPrivate.state ?: "")

                        Log.d(TAG, "fetchEditData: user retrieved successfully $userViewPrivate")
                    }


                } else {
                    Log.e(TAG, "fetchEditData: user retrieved error")
                }

            } catch (e: IOException) {
                Log.e(TAG, "fetchEditData: network exception (no network) ${e.message}  --//-- $e")

            } catch (e: Exception) {
                Log.e(TAG, "fetchEditData: general exception ${e.message}  --//-- $e")

            }
        }
    }

    fun saveData(setLoginCredentials: (String) -> Unit, realPath: String?) {

        Log.d(TAG, "saveData: Updating data")

        viewModelScope.launch {

            realPath?.let {
                updatePicture(File(it))
            }
            userUpdate(
                UserDTOEdit(
                    id = userToken.id,
                    name = name.text.trim(),
                    password = password.text.trim(),
                    state = state.text.trim(),
                    email = email.text.trim()
                )
            ) { setLoginCredentials(password.text.trim()) }
        }
    }

    private suspend fun userUpdate(
        user: UserDTOEdit,
        setLoginCredentials: () -> Unit
    ) {
        try {

            val webResponse =
                NightTimeApi.retrofitService.updateUser(userToken.token, userToken.id, user)

            if (webResponse.isSuccessful) {

                setLoginCredentials()
                Log.d(TAG, "userUpdate: user updated successfully")
            } else {
                Log.e(TAG, "userUpdate: user update error")
            }

        } catch (e: IOException) {
            Log.e(TAG, "userUpdate: network exception (no network) ${e.message}  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "userUpdate: general exception ${e.message}  --//-- $e")

        }
    }

    private suspend fun updatePicture(file: File) {
        try {


            val webResponse =
                NightTimeApi.retrofitService.setPicture(
                    userToken.token,
                    userToken.id,
                    img = createMultiPart(file)
                )

            if (webResponse.isSuccessful) {
                // Remove cache of picasso so it will fetch the new photo
                invalidatePicasso(userToken.id)
                Log.d(TAG, "updatePicture: user updated successfully")
            } else {
                Log.e(TAG, "updatePicture: user update error code ${webResponse.code()}")
            }

        } catch (e: IOException) {
            Log.e(TAG, "updatePicture: network exception (no network) ${e.message}  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "updatePicture: general exception ${e.message}  --//-- $e")

        }
    }

    fun newUser(
        photoUri: String? = null,
        loginFunction: (String, String) -> Unit
    ) = viewModelScope.launch {

        try {
            Log.d(
                TAG,
                "newUser: ready to add user named ${name.text}, has photo ${photoUri != null}"
            )

            val webResponse = NightTimeApi.retrofitService.newUser(
                user = UserDTOInsert(
                    name = name.text,
                    nickname = username.text,
                    password = password.text,
                    state = state.text,
                    email = email.text
                )
            )
            Log.d(TAG, "newUser: webResponse code ${webResponse.code()}")

            if (webResponse.code() == 201) { // User accepted

                Log.d(TAG, "newUser: User accepted")

                val headers = webResponse.headers()
                val id = headers.get("id")?.toLong() ?: -1L
                val token = headers.get("token") ?: ""


                if (id == -1L || token.isBlank()) {

                    errorText = "Unexpected error, contact with admin"

                } else {
                    userToken = UserToken(id, token)
                    errorText = ""

                    photoUri?.let {
                        updatePicture(File(it))
                    }
                    eraseData()
                    loginFunction(username.text, password.text)
                }

            } else if (webResponse.code() == 208) { // User nickname repeated

                Log.d(TAG, "newUser: User refused, nickname already in use")
                errorText = "Nombre de usuario en uso"

            } else {
                Log.d(TAG, "newUser: response code ${webResponse.code()}")
                errorText = webResponse.headers()["error"] ?: "Error desconocido"
            }

        } catch (e: IOException) {
            Log.e(TAG, "newUser: network exception (no network) ${e.message}  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "newUser: general exception ${e.message}  --//-- $e")
        }
    }

    private fun createMultiPart(file: File): MultipartBody.Part {
        // Create requestFile
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("img", file.name, requestFile)
    }

}

object UserEmpty {
    fun getEmptyUser(): UserFull {
        return UserFull(
            id = -1,
            name = "",
            nickname = "",
            state = "",
            nextDate = null,
            picture = null,
            password = "",
            email = "",
        )
    }
}





