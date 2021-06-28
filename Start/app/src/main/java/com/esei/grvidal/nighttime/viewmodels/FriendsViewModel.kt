package com.esei.grvidal.nighttime.viewmodels

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.lifecycle.*
import com.esei.grvidal.nighttime.network.*
import com.esei.grvidal.nighttime.network.NightTimeService.NightTimeApi.retrofitService
import com.esei.grvidal.nighttime.network.network_DTOs.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException

fun ChatView.toFullView(
    img: ImageAsset? = null
): ChatFullView {
    return ChatFullView(
        friendshipId,
        userId,
        userNickname,
        hasImage,
        messages,
        unreadMessages,
        img
    )
}


private const val TAG = "FriendsViewModel"

class FriendsViewModel(
    // User token to call api
    private val userToken: UserToken
) : ViewModel() {


    fun getId(): Long {
        return userToken.id
    }


    //List of open chats the user has got
    private var chatListHolder = mutableListOf<ChatFullView>()

    // Backing property to avoid state updates from other classes
    private val _chatList = MutableStateFlow(listOf<ChatFullView>())

    // The UI collects from this StateFlow to get its state updates
    val chatList: StateFlow<List<ChatFullView>> = _chatList


    // List of friendships to start a new chat
    var friendshipIdList by mutableStateOf(setOf<FriendshipSnapImage>())

    private var friendListPage = 0
    var totalFriends = -1

    /**
     * List of searched users
     */
    var searchedUserList by mutableStateOf(listOf<UserSnapImage>())
        private set

    /**
     * [searchString] String used to search users
     * [searchPage] reference to page for pagination
     * [totalPeopleSearch] Number of max users with [searchString]
     */
    private lateinit var searchString: String
    private var searchPage = 0
    private var totalPeopleSearch = 0


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

    /**
     *  Strong reference point to avoid loosing the targets
     */
    private var targetList = mutableListOf<Target>()


    /**
     * Fetches from the API all friendships with at least one message
     */
    fun getChats() = viewModelScope.launch {

        try {
            val webResponse =
                retrofitService.getChatsWithMessages(auth = userToken.token, id = userToken.id)

            if (webResponse.isSuccessful) {

                webResponse.body()?.let { fetchedList ->
                    Log.d(TAG, "getChats: fetched data, number of chats = ${fetchedList.size}")
                    chatListHolder = fetchedList.map {
                        it.toFullView()
                    }.toMutableList()


                    val usersToFetchPhoto = chatListHolder.filter { it.hasImage }

                    for (user in usersToFetchPhoto) {
                        Log.d(TAG, "getChats: ${user.userId} has image ${user.hasImage}")

                        loadUserImage(
                            picasso = Picasso.get(),
                            userId = user.userId,
                            action = { img ->

                                chatListHolder.find { userFiltered ->
                                    userFiltered.userId == user.userId
                                }?.let {
                                    chatListHolder.remove(it)
                                    chatListHolder.add(it.copy(img = img.asImageAsset()))
                                    Log.d(TAG, "getChats: Updating img of user ${it.userId}")

                                    updateFlow()

                                }

                            }
                        )

                    }
                    updateFlow()


                }


            } else Log.d(TAG, "getChats: ${webResponse.headers()[ERROR_HEADER_TAG]}")

        } catch (e: IOException) {
            Log.d(TAG, "getChats: network exception ${e.message}  --//-- $e")
        } catch (e: Exception) {

            Log.d(TAG, "getChats: network exception ${e.message}  --//-- $e")
        }
    }

    private fun updateFlow() {
        _chatList.value = chatListHolder
            .sortedByDescending { list -> list.messages[0].time }
            .sortedByDescending { list -> list.messages[0].date }
        Log.d(TAG, "updateFlow: Flow has been updated")
    }

    fun getFriendshipsIds() = viewModelScope.launch {
        if (totalFriends == -1 || totalFriends > friendshipIdList.size)
            try {

                val webResponse = retrofitService.getFriendshipsIdsChat(
                    id = userToken.id,
                    auth = userToken.token,
                    page = friendListPage
                )

                if (webResponse.isSuccessful) {
                    webResponse.body()?.let { fetchedList ->
                        Log.d(
                            TAG,
                            "getFriendshipsIds: users fetched ${fetchedList.size}, page $friendListPage total ${webResponse.headers()["total"]}"
                        )
                        friendListPage++

                        friendshipIdList =
                            friendshipIdList + fetchedList.map { it.toFriendshipSnapImage() }

                        val userWithImages = fetchedList.filter { it.image }
                        for (user in userWithImages) {
                            loadUserImage(
                                picasso = Picasso.get(),
                                userId = user.userId,
                                action = { img ->
                                    friendshipIdList
                                        .findLast { chat -> chat.userId == user.userId }
                                        ?.let { user ->

                                            // Recomposition is made when the setter of the list is called,
                                            // so the list need to be edited (add or remove items on the list).
                                            // This is done by re-adding the edited element of the list
                                            friendshipIdList =
                                                friendshipIdList.toMutableSet().also {
                                                    it.remove(user)
                                                    user.image = img.asImageAsset()

                                                } + setOf(user)


                                            Log.d(
                                                TAG,
                                                "onBitmapLoaded: getting Image user ${user.userId} size ${img.byteCount} height ${img.height}, width ${img.width}"
                                            )
                                        }
                                }
                            )
                        }
                    }

                    totalFriends = webResponse.headers()["total"]?.toInt() ?: friendshipIdList.size

                } else Log.d(
                    TAG,
                    "getFriendshipsIds: error ${webResponse.headers()[ERROR_HEADER_TAG]}"
                )

            } catch (e: IOException) {
                Log.e(TAG, "getFriendshipsIds: network exception (no network)  --//-- $e")

            } catch (e: Exception) {
                Log.e(TAG, "getFriendshipsIds: general exception  --//-- $e")
            }
    }


    /**
     * Resets [searchedUserList] to an empty list
     */
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
                                                        "onBitmapLoaded: getting Image user ${user.userId} size ${img.byteCount} height ${img.height}, width ${img.width}"
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

                    totalRequestingFriendship =
                        webResponse.headers()["total"]?.toInt() ?: requestingFriendshipUserList.size

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

    private fun loadUserImage(
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


    @OptIn(InternalCoroutinesApi::class)
    fun setFlow(
        coroutineScope: CoroutineScope, flow: SharedFlow<MessageListened>
    ) {

        coroutineScope.launch {
            Log.d(TAG, "setFlow: Started viewModel coroutine")
            flow.collect { msg ->

                Log.d(TAG, "setFlow: message arrived ${msg.messageView.text}")

                val selectedChat = _chatList.value.find { it.friendshipId == msg.channel }

                // If the chat is found on the existing list the notification pops the chat up
                selectedChat?.let { chat ->
                    Log.d(TAG, "setFlow: calling updateList")
                    updateList(chat, msg)

                    // If the chat couldn't be found a new entry is created
                } ?: addChat(msg)

            }
        }
    }

    private fun addChat(msg: MessageListened) = viewModelScope.launch {


        try {

            val webResponse = retrofitService.getUserDetails(
                id = msg.messageView.user,
                headers = mapOf(
                    "clientUser" to userToken.id.toString(),
                    "auth" to userToken.token
                )
            )
            if (webResponse.isSuccessful) {
                webResponse.body()?.let {

                    chatListHolder.add(
                        ChatFullView(
                            msg.channel,
                            msg.messageView.user,
                            it.nickname,
                            it.picture != null,
                            listOf(msg.messageView),
                            unreadMessages = 1,
                            img = null
                        )
                    )

                    updateFlow()
                }
            }

        } catch (e: IOException) {
            Log.e(TAG, "addChat: network exception (no network)  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "addChat: general exception  --//-- $e")
        }

    }

    private fun updateList(
        chat: ChatFullView,
        msg: MessageListened
    ) = viewModelScope.launch {

        chatListHolder.remove(chat)
        chatListHolder.add(
            chat.copy(
                messages = listOf(msg.messageView),
                unreadMessages = chat.unreadMessages + 1
            )
        )
        // Flow will update if .equals returns false,
        // for some reason if [chat] is not .copy it will return false

        updateFlow()

        for ((i, chatsAux) in chatListHolder.withIndex())
            Log.d(
                TAG,
                "setFlow: FINISHED!!! $i - ${chatsAux.userNickname}  - last -> ${chatsAux.messages[0].text} - nuevos msg -> ${chatsAux.unreadMessages}"
            )

    }


}

