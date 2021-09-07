package com.esei.grvidal.nighttime.viewmodels

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
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.fakeData.*
import com.esei.grvidal.nighttime.network.BASE_URL
import com.esei.grvidal.nighttime.network.ERROR_HEADER_TAG
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi
import com.esei.grvidal.nighttime.network.USER_URL
import com.esei.grvidal.nighttime.network.network_DTOs.*
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

enum class PhotoState {
    LOADING,
    ERROR,
    DONE
}

data class ErrorHolder(
    val resourceInt: Int? = null,
    val errorString: String? = null
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

    // User profile picture
    var userPicture by mutableStateOf<ImageAsset?>(null)

    // Internal Uri to load new profile picture with Picasso
    var uriPhotoPicasso by mutableStateOf<Uri?>(null)

    var photoState by mutableStateOf(PhotoState.DONE)

    // User data
    var user by mutableStateOf(
        UserFull(
            id = -1,
            name = "",
            nickname = "",
            state = "",
            nextDate = null,
            picture = null,
            password = "",
            email = ""
        )
    )
        private set

    var friendshipState by mutableStateOf(AnswerOptions.NO)

    var username by mutableStateOf(TextFieldValue())

    var name by mutableStateOf(TextFieldValue())

    var state by mutableStateOf(TextFieldValue())

    var password by mutableStateOf(TextFieldValue())

    var email by mutableStateOf(TextFieldValue())

    var errorText by mutableStateOf(ErrorHolder())

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

    fun fetchData(userId: Long) = viewModelScope.launch {
        eraseData()


        // TODO: 06/09/2021 FAKE DATA invalidate picasso
        //fetchUser(userId) // Get data from new user
        fakeFetchUser(userId)

        if (!user.picture.isNullOrEmpty()) {
            // TODO: 06/09/2021 FAKE DATA invalidate picasso
            //fetchPhoto(userId)
        }
        delay(500)
        // TODO: 06/09/2021 FAKE DATA invalidate picasso
        //fetchUser(userId) // Get data again from the user in case the edit delayed a bit
        fakeFetchUser(userId)
    }

    private fun fakeFetchUser(userId: Long) {
        allUsersList.find { search -> userId == search.id }?.let { user1 ->
            user = UserFull(
                user1.id,
                user1.nickname,
                user1.name,
                user1.password,
                user1.state ?: "",
                user1.email,
                user1.nextDates.firstOrNull()?.let { dateCity ->
                    NextDateDTO(
                        dateCity.id,
                        dateCity.nextDate.toString(),
                        CityDTO(dateCity.nextCity.id, dateCity.nextCity.name, "España")
                    )
                },
                user1.picture.toString(),
            )

            val iterator = friendList.iterator()
            var isFound = false
            while (iterator.hasNext() && !isFound) {
                val actual = iterator.next()
                if (actual.userAsk == user1) {
                    isFound = true
                    friendshipState = actual.answer
                }
            }

            if (!isFound) {
                friendshipState = AnswerOptions.NO
            }


        }
    }

    private suspend fun fetchUser(userId: Long) {
        try {

            val webResponse = NightTimeApi.retrofitService.getUserDetails(
                id = userId,
                headers = mapOf(
                    "clientUser" to userToken.id.toString(),
                    "auth" to userToken.token
                )
            )
            Log.d(
                TAG,
                "fetchUser: call to retrofit done userId $userId"
            )

            if (webResponse.isSuccessful) {

                webResponse.body()?.let { userDTO ->
                    user = UserFull(
                        id = userDTO.id,
                        nickname = userDTO.nickname,
                        name = userDTO.name,
                        password = "",
                        state = userDTO.state,
                        email = "",
                        nextDate = userDTO.nextDate,
                        picture = userDTO.picture
                    )
                    friendshipState = userDTO.friendshipState
                }

                Log.d(
                    TAG,
                    "fetchUser: user received $user"
                )

            } else {
                Log.d(
                    TAG,
                    "fetchUser: user error  $webResponse - ${webResponse.message()}"
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

    fun fakeFetchEditData() {

        val user = allUsersList.find { userToken.id == it.id }

        if (user != null) {
            name = TextFieldValue(user.name)
            email = TextFieldValue(user.email)
            password = TextFieldValue(user.password)
            state = TextFieldValue(user.state ?: "")
        }

    }

    fun fetchEditData() {
        viewModelScope.launch {
            try {

                val webResponse =
                    NightTimeApi.retrofitService.getUserPrivate(userToken.token, userToken.id)

                if (webResponse.isSuccessful) {

                    webResponse.body()?.let { userViewPrivate ->

                        name = TextFieldValue(userViewPrivate.name ?: "")
                        email = TextFieldValue(userViewPrivate.email ?: "")
                        password = TextFieldValue(userViewPrivate.password ?: "")
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

                    errorText = ErrorHolder(R.string.unexpected_error)

                } else {
                    userToken = UserToken(id, token)
                    errorText = ErrorHolder(-1)

                    photoUri?.let {
                        updatePicture(File(it))
                    }
                    eraseData()
                    loginFunction(username.text, password.text)
                }

            } else if (webResponse.code() == 208) { // User nickname repeated

                Log.d(TAG, "newUser: User refused, nickname already in use")
                errorText = ErrorHolder(R.string.already_used_name)

            } else {
                Log.d(TAG, "newUser: response code ${webResponse.code()}")

                errorText = webResponse.headers()["error"]?.let {
                    ErrorHolder(null, it)
                } ?: ErrorHolder(R.string.unexpected_error)

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
        //file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("img", file.name, requestFile)
    }

    fun fakeRequestFriendship(idFriend: Long) {
        allUsersList.find { it.id == idFriend }?.let { userFriend ->

            if (findUser(userFriend.id) == null) {
                friendList.add(Friendship(userFriend))
            }

        }
    }

    fun requestFriendship(idFriend: Long) = viewModelScope.launch {
        try {

            val webResponse = NightTimeApi.retrofitService.addFriendshipRequest(
                id = userToken.id,
                auth = userToken.token,
                idFriend = idFriend
            )

            Log.d(TAG, "requestFriendship: code Respond ${webResponse.code()}")

            if (!webResponse.isSuccessful)
                Log.d(TAG, "requestFriendship: ${webResponse.headers()[ERROR_HEADER_TAG]}")

        } catch (e: IOException) {
            Log.e(TAG, "requestFriendship: network exception (no network)   --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "requestFriendship: general exception  --//-- $e")
        }
    }


    fun fakeRemoveFriendship(friendshipId: Long) {
        friendList.remove(
            friendList.find { it.id == friendshipId }
        )
    }

    fun removeFriendShip(userId: Long) = viewModelScope.launch {
        try {

            val webResponse = NightTimeApi.retrofitService.removeFriendship(
                id = userToken.id,
                auth = userToken.token,
                idFriend = userId
            )

            Log.d(TAG, "removeFriendShip: code Respond ${webResponse.code()}")

        } catch (e: IOException) {
            Log.e(TAG, "removeFriendShip: network exception (no network)   --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "removeFriendShip: general exception  --//-- $e")
        }
    }


}
