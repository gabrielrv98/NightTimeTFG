package com.esei.grvidal.nighttime.data

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esei.grvidal.nighttime.network.*
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi.retrofitService
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.launch
import java.io.IOException

data class ChatView(
    val friendshipId: Long,
    val userId: Long,
    val userNickname: String,
    val hasImage: Boolean,
    val messages: List<MessageView>
)

fun ChatView.toFullView(
    img: ImageAsset? = null
): ChatFullView {
    return ChatFullView(
        friendshipId,
        userId,
        userNickname,
        hasImage,
        messages,
        img
    )
}

data class MessageView(
    val messageId: Long,
    val text: String,
    val date: String,
    val hour: String,
    val user: Long
)

data class ChatFullView(
    val friendshipId: Long,
    val userId: Long,
    val userNickname: String,
    val hasImage: Boolean,
    val messages: List<MessageView>,
    var img: ImageAsset?
)

private const val TAG = "ChatViewModel"

class ChatViewModel : ViewModel() {

    // User token to call api
    private var userToken = UserToken(-1, "")

    fun setUserToken(loggedUser: UserToken) {

        Log.d(TAG, "setUserToken: old token = $userToken, new $loggedUser")
        userToken = loggedUser
    }

    fun getId(): Long {
        return userToken.id
    }

    // Dialog to add new friends
    var showDialog by mutableStateOf(false)

    fun setDialog(b: Boolean) {
        showDialog = b
    }

    var chatList by mutableStateOf(listOf<ChatFullView>())
        private set

    var userList by mutableStateOf(listOf<UserSnapImage>())
        private set

    // Strong reference point to avoid loosing them
    private var targetList = mutableListOf<Target>()

    fun getChats() = viewModelScope.launch {

        try {
            Log.d(TAG, "getChats: Ready to call retrofit")
            val webResponse = retrofitService.getChats(auth = userToken.token, id = userToken.id)

            if (webResponse.isSuccessful) {

                webResponse.body()?.let { fetchedList ->
                    Log.d(TAG, "getChats: fetched data, number of chats = ${fetchedList.size}")
                    chatList = fetchedList.map {
                        it.toFullView()
                    }

                }
                fetchPhotosChat()

            } else Log.d(TAG, "getChats: ${webResponse.headers()[ERROR_HEADER_TAG]}")

        } catch (e: IOException) {
            Log.d(TAG, "getChats: network exception ${e.message}  --//-- $e")
        } catch (e: Exception) {

            Log.d(TAG, "getChats: network exception ${e.message}  --//-- $e")
        }
    }

    private suspend fun fetchPhotosChat() {

        Log.d(TAG, "fetchPhotosChat starting to fetch photos")
        for (chat in chatList) {

            if(chat.hasImage) {
                loadImageChat(
                    url = "$BASE_URL$USER_URL${chat.userId}/photo",
                    userId = chat.userId
                )
            }
        }

    }


    private suspend fun loadImageChat(
        url: String,
        userId: Long,
        picasso: Picasso = Picasso.get()
    ) {

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d(
                    TAG,
                    "fetchPhotos: onPrepareLoad: loading image user id $userId"
                )
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                //Handle the exception here
                Log.d(TAG, "fetchPhotos: onBitmapFailed: error $e")
                targetList.remove(this)
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {


                //Here we get the loaded image
                Log.d(
                    TAG,
                    "fetchPhotos: onBitmapLoaded: Image fetched size ${bitmap?.byteCount} height ${bitmap?.height}, width ${bitmap?.width}"
                )

                bitmap?.let { img ->
                    chatList.filter { chat -> chat.userId == userId }.getOrNull(
                        0
                    )?.let { chat ->

                        // Recomposition is made when the setter of the list is called,
                        // so we need to delete, edit (adding the picture) and re-add the element
                        chatList = chatList.toMutableList().also {
                            it.remove(chat)
                            chat.img = img.asImageAsset()

                        } + listOf(chat)



                        Log.d(
                            TAG,
                            "onBitmapLoaded: getting Image user $userId size ${img.byteCount} height ${img.height}, width ${img.width}"
                        )
                    }
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

    fun searchUsers(username: String) = viewModelScope.launch {
        if (username.isBlank()) {
            userList = listOf()
        } else {
            try {

                Log.d(TAG, "searchUsers: Ready to search users that start with $username")

                val webResponse = retrofitService.searchUsers(username)

                if (webResponse.isSuccessful) {
                    webResponse.body()?.let { userSnap ->
                        Log.d(TAG, "searchUsers: Users fetched = $userSnap")

                        userList = userSnap.map {
                            it.toUserSnapImage(null)
                        }

                        fetchPhotosUserSnap()
                    }
                }


            } catch (e: IOException) {
                Log.e(TAG, "searchUsers: network exception (no network)  --//-- $e")

            } catch (e: Exception) {
                Log.e(TAG, "searchUsers: general exception  --//-- $e")
            }
        }

    }

    private suspend fun fetchPhotosUserSnap() {
        Log.d(TAG, "fetchPhotosUserSnap starting to fetch photos")
        for (user in userList) {

            if (user.hasImage) {
                loadImageUser(
                    url = "$BASE_URL$USER_URL${user.userId}/photo",
                    userId = user.userId
                )
            }
        }
    }

    private suspend fun loadImageUser(
        url: String,
        userId: Long,
        picasso: Picasso = Picasso.get()
    ) {

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d(
                    TAG,
                    "fetchPhotos: onPrepareLoad: loading image user id $userId"
                )
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                //Handle the exception here
                Log.d(TAG, "fetchPhotos: onBitmapFailed: error $e")
                targetList.remove(this)
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {


                //Here we get the loaded image
                Log.d(
                    TAG,
                    "fetchPhotos: onBitmapLoaded: Image fetched size ${bitmap?.byteCount} height ${bitmap?.height}, width ${bitmap?.width}"
                )

                bitmap?.let { img ->
                    userList.filter { chat -> chat.userId == userId }.getOrNull(
                        0
                    )?.let { user ->

                        // Recomposition is made when the setter of the list is called,
                        // so we need to delete, edit (adding the picture) and re-add the element
                        userList = userList.toMutableList().also {
                            it.remove(user)
                            user.img = img.asImageAsset()

                        } + listOf(user)



                        Log.d(
                            TAG,
                            "onBitmapLoaded: getting Image user $userId size ${img.byteCount} height ${img.height}, width ${img.width}"
                        )
                    }
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

    fun addUser(
        idUserSearch: Long
    ) = viewModelScope.launch {

        try {
            Log.d(TAG, "addUser: Ready to call retrofit")
            val webResponse = retrofitService.getChats(auth = userToken.token, id = userToken.id)

            if (webResponse.isSuccessful) {

                webResponse.body()?.let { fetchedList ->
                    Log.d(TAG, "addUser: fetched data ${chatList.toString()}")
                    chatList = listOf()
                }


            } else Log.d(TAG, "addUser: ${webResponse.headers()[ERROR_HEADER_TAG]}")

        } catch (e: IOException) {
            Log.d(TAG, "addUser: network exception ${e.message}  --//-- $e")
        } catch (e: Exception) {

            Log.d(TAG, "addUser: network exception ${e.message}  --//-- $e")
        }
    }


}

