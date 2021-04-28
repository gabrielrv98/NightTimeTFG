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

data class UserFriendViewAPI(
    var friendshipId: Long,
    var userId: Long,
    var userNickname: String,
    var state: String,
    var image: Boolean
)

data class UserFriendView(
    var friendshipId: Long,
    var userId: Long,
    var userNickname: String,
    var state: String,
    val hasImage: Boolean,
    var image: ImageAsset? = null
)


data class FriendshipUpdateDTO(
    val id: Long,
    val answer: AnswerOptions
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

    /**
     * List of open chats the user has
     */
    var chatList by mutableStateOf(listOf<ChatFullView>())
        private set

    private lateinit var searchString: String
    var searchPage = 0
    private var totalPeopleSearch = 0

    /**
     * List of searched users
     */
    var searchedUserList by mutableStateOf(listOf<UserSnapImage>())
        private set

    /**
     * List of users who has requested friendship to the user
     */
    var requestingFriendshipUserList by mutableStateOf(listOf<UserFriendView>())
        private set

    /**
     * Number of requesting friendships, used for notification
     */
    var totalRequestingFriendship by mutableStateOf(0)
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

                    Log.d(TAG, "fetchPhotosChat starting to fetch photos")
                    for (chat in chatList) {

                        if (chat.hasImage) {
                            loadUserImage(
                                picasso = Picasso.get(),
                                userId = chat.userId,
                                action = { img ->
                                    chatList.filter { chaFilter -> chaFilter.userId == chat.userId }
                                        .getOrNull(
                                            0
                                        )?.let { chatSelected ->

                                            // Recomposition is made when the setter of the list is called,
                                            // so we need to delete, edit (adding the picture) and re-add the element
                                            chatList = chatList.toMutableList().also {
                                                it.remove(chatSelected)
                                                chatSelected.img = img.asImageAsset()

                                            } + listOf(chatSelected)



                                            Log.d(
                                                TAG,
                                                "onBitmapLoaded: getting Image user ${chatSelected.userId} size ${img.byteCount} height ${img.height}, width ${img.width}"
                                            )
                                        }
                                }
                            )
                        }
                    }

                }


            } else Log.d(TAG, "getChats: ${webResponse.headers()[ERROR_HEADER_TAG]}")

        } catch (e: IOException) {
            Log.d(TAG, "getChats: network exception ${e.message}  --//-- $e")
        } catch (e: Exception) {

            Log.d(TAG, "getChats: network exception ${e.message}  --//-- $e")
        }
    }


    fun clearSearchedList() {
        searchedUserList = listOf()
    }

    /**
     * Calls to api to request a new page (starting in 0) of searched users
     * and uses Picasso to get their picture if they have one
     */
    fun searchUsers(username: String) = viewModelScope.launch {
        if (username.isBlank()) {
            searchString = ""
            searchedUserList = listOf()
        } else {
            try {

                Log.d(
                    TAG,
                    "searchUsers: Ready to search users that start with $username ( actual size) ${searchedUserList.size}"
                )

                if (searchString == username) {
                    searchPage++
                    Log.d(TAG, "searchUsers: Searching next page $searchPage")
                } else {
                    searchString = username
                    searchPage = 0
                    searchedUserList = listOf()
                    Log.d(TAG, "searchUsers: Doing a new search with nickname $username")
                    totalPeopleSearch = -1
                }

                // If it is a new search or there are users remaining api is called
                if (totalPeopleSearch == -1 || searchedUserList.size < totalPeopleSearch) {
                    val webResponse = retrofitService.searchUsers(
                        username,
                        searchPage
                    )

                    if (webResponse.isSuccessful) {
                        totalPeopleSearch = webResponse.headers()["total"]?.toInt() ?: 0

                        webResponse.body()?.let { userSnap ->
                            Log.d(
                                TAG,
                                "searchUsers: Users fetched = ${userSnap.size}, total users $totalPeopleSearch"
                            )

                            searchedUserList = searchedUserList + userSnap.map {
                                it.toUserSnapImage()
                            }

                            Log.d(TAG, "SearchUser: Starting to fetch photos")
                            for (user in searchedUserList) {

                                if (user.hasImage && user.img == null) {
                                    loadUserImage(
                                        picasso = Picasso.get(),
                                        userId = user.userId,
                                        action = { img ->
                                            searchedUserList
                                                .findLast { chat -> chat.userId == user.userId }
                                                ?.let { user ->

                                                    // Recomposition is made when the setter of the list is called,
                                                    // so the list need to be edited (add or remove items on the list).
                                                    // This is done by re-adding the edited element of the list
                                                    searchedUserList =
                                                        searchedUserList.toMutableList().also {
                                                            it.remove(user)
                                                            user.img = img.asImageAsset()

                                                        } + listOf(user)



                                                    Log.d(
                                                        TAG,
                                                        "onBitmapLoaded: getting Image user $user.userId size ${img.byteCount} height ${img.height}, width ${img.width}"
                                                    )
                                                }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }


            } catch (e: IOException) {
                Log.e(TAG, "searchUsers: network exception (no network)  --//-- $e")

            } catch (e: Exception) {
                Log.e(TAG, "searchUsers: general exception  --//-- $e")
            }
        }

    }

    fun getRequestingFriendships() = viewModelScope.launch {

        try {

            val webResponse = retrofitService.getRequestingFriendships(
                id = userToken.id,
                auth = userToken.token
            )

            if (webResponse.isSuccessful) {

                webResponse.body()?.let {

                    requestingFriendshipUserList = it.map { user ->
                        UserFriendView(
                            user.friendshipId,
                            user.userId,
                            user.userNickname,
                            user.state,
                            user.image
                        )
                    }

                    totalRequestingFriendship = webResponse.headers()["total"]?.toInt() ?: requestingFriendshipUserList.size

                    for (user in requestingFriendshipUserList) {
                        if (user.hasImage) {

                            loadUserImage(
                                picasso = Picasso.get(),
                                userId = user.userId,
                                action = { bitmap ->
                                    requestingFriendshipUserList
                                        .findLast { requesters -> requesters.userId == user.userId }
                                        ?.let { userFriendView ->

                                            // Recomposition is made when the setter of the list is called,
                                            // so the list need to be edited (add or remove items on the list).
                                            // This is done by re-adding the edited element of the list
                                            requestingFriendshipUserList =
                                                requestingFriendshipUserList.toMutableList()
                                                    .also { mutableList ->
                                                        mutableList.remove(userFriendView)
                                                        userFriendView.image = bitmap.asImageAsset()

                                                    } + listOf(userFriendView)

                                        }
                                }
                            )
                        }
                    }

                    Log.d(
                        TAG,
                        "getRequestingFriendships: fetched users: $requestingFriendshipUserList"
                    )

                }
            }

        } catch (e: IOException) {
            Log.e(TAG, "getRequestingFriendships: network exception (no network)  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "getRequestingFriendships: general exception  --//-- $e")
        }
    }

    @Suppress("RedundantSuspendModifier")
    private suspend fun loadUserImage(
        targetWidth: Int = 250,
        targetHeight: Int = 250,
        userId: Long,
        action: (Bitmap) -> Unit,
        picasso: Picasso
    ) {

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d(
                    TAG,
                    "loadImage: onPrepareLoad: loading image user id $userId"
                )
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                //Handle the exception here
                Log.d(TAG, "loadImage: onBitmapFailed: error $e")
                targetList.remove(this)
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {


                //Here we get the loaded image
                Log.d(
                    TAG,
                    "loadImage: onBitmapLoaded: Image fetched size ${bitmap?.byteCount} height ${bitmap?.height}, width ${bitmap?.width}"
                )

                bitmap?.let { img ->
                    action(img)
                }
                targetList.remove(this)


            }
        }
        targetList.add(target)

        picasso
            .load("$BASE_URL$USER_URL$userId/photo")
            .resize(targetWidth, targetHeight)
            .centerCrop()
            .into(target)

    }

    fun answerFriendshipRequest(idFriendship: Long, isAccepted: Boolean) = viewModelScope.launch {
        try {

            Log.d(
                TAG,
                "answerFriendshipRequest: ready to send retrofit idFriendship $idFriendship isAccepted $isAccepted "
            )
            val webResponse = retrofitService.answerFriendshipRequest(
                id = userToken.id,
                auth = userToken.token,
                friendshipUpdateDTO = FriendshipUpdateDTO(
                    id = idFriendship,
                    answer = if (isAccepted) AnswerOptions.YES
                    else AnswerOptions.NO
                )
            )

            Log.d(TAG, "answerFriendshipRequest: Answer code = ${webResponse.code()} ")
            if (webResponse.isSuccessful) {

                requestingFriendshipUserList.findLast { friend -> friend.friendshipId == idFriendship }
                    ?.let { userAnswered ->
                        Log.d(
                            TAG,
                            "answerFriendshipRequest: deleting from requestingFriendshipUserList user ${userAnswered.userId} - ${userAnswered.userNickname}"
                        )
                        requestingFriendshipUserList =
                            requestingFriendshipUserList.toMutableList()
                                .also { mutableList ->
                                    mutableList.remove(userAnswered)
                                    totalRequestingFriendship--

                                }
                    }
            }


        } catch (e: IOException) {
            Log.e(TAG, "answerFriendshipRequest: network exception (no network)  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "answerFriendshipRequest: general exception  --//-- $e")
        }
    }


}

