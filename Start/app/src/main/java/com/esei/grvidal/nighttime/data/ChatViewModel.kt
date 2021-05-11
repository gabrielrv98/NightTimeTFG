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
import com.esei.grvidal.nighttime.network.network_DTOs.ChatFullView
import com.esei.grvidal.nighttime.network.network_DTOs.MessageView
import com.esei.grvidal.nighttime.network.network_DTOs.UserToken
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException


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


    var friendshipId = -1L
    var otherUserId = -1L
    var userNickname by mutableStateOf("ERROR")
    var image  by mutableStateOf<ImageAsset?>(null)
    var messages by mutableStateOf(listOf<MessageView>())


    fun getSelectedChat(
        friendshipIdSelected: Long
    ) = viewModelScope.launch {

        try {

            friendshipId = friendshipIdSelected
            val webResponse = retrofitService.getSelectedChat(
                idUser = userToken.id,
                auth = userToken.token,
                idFriendship = friendshipId
            )

            if (webResponse.isSuccessful) {
                webResponse.body()?.let { fetchedChat ->
                    messages = fetchedChat.messages
                    userNickname = fetchedChat.userNickname
                    otherUserId = fetchedChat.userId

                    if(fetchedChat.hasImage){

                        Picasso
                            .get()
                            .load("$BASE_URL$USER_URL$otherUserId/photo")
                            .resize(50, 50)
                            .centerCrop()

                            .into(object : Target {
                                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                    Log.d(
                                        TAG,
                                        "loadImage: onPrepareLoad: loading image user id $otherUserId"
                                    )
                                }

                                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                                    //Handle the exception here
                                    Log.d(TAG, "loadImage: onBitmapFailed: error $e")
                                }

                                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                                    //Here we get the loaded image
                                    Log.d(
                                        TAG,
                                        "loadImage: onBitmapLoaded: Image fetched size ${bitmap?.byteCount} height ${bitmap?.height}, width ${bitmap?.width}"
                                    )
                                    bitmap?.let { img ->
                                        image = img.asImageAsset()
                                    }

                                }
                            })
                    }

                    Log.d(
                        TAG,
                        "getSelectedChat: messages fetched for friendship $friendshipId, fetched ${fetchedChat.messages.size} messages"
                    )

                }
            }

        } catch (e: IOException) {
            Log.e(TAG, "getSelectedChat: network exception (no network)  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "getSelectedChat: general exception  --//-- $e")
        }
    }

    fun addMessage(msg: String) = viewModelScope.launch {
        try {

            val webResponse = retrofitService.sendMessage(
                auth = userToken.token,
                idUser = getId(),
                messageForm = MessageForm(
                    friendshipId = friendshipId,
                    text = msg
                )
            )

            Log.d(
                TAG,
                "addMessage: response ${webResponse.body()} - code ${webResponse.code()} - header error ${webResponse.headers()[ERROR_HEADER_TAG]}"
            )

        } catch (e: IOException) {
            Log.d(TAG, "addMessage: network exception ${e.message}  --//-- $e")
        } catch (e: Exception) {

            Log.d(TAG, "addMessage: network exception ${e.message}  --//-- $e")
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

                if (msg.channel == friendshipId) {
                    updateChat(msg.messageView)
                }

            }
        }
    }

    private fun updateChat(msg: MessageView) = viewModelScope.launch {

        messages = messages.toMutableList().apply {
            add(msg)
        }
    }

}