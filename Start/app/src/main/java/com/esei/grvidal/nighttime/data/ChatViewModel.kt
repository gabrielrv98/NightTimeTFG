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
import com.esei.grvidal.nighttime.network.network_DTOs.*
import com.esei.grvidal.nighttime.repository.interface_repository.IRepositoryChat
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime


private const val TAG = "ChatViewModel"

class ChatViewModel(

    val friendshipId: Long,
    private val chatRepository: IRepositoryChat

) : ViewModel() {

    fun getId(): Long {
        return chatRepository.getId()
    }

    // Backing property to avoid state updates from other classes
    private val _actualChat = MutableStateFlow(
        ChatView(
            friendshipId = friendshipId,
            userId = -1,
            userNickname = "",
            hasImage = false,
            messages = emptyList(),
            unreadMessages = 0
        )
    )

    // The UI collects from this StateFlow to get its state updates
    val actualChat: StateFlow<ChatView> = _actualChat
    var image by mutableStateOf<ImageAsset?>(null)

    fun getSelectedChat() = viewModelScope.launch {

        chatRepository
            .getChatDataRepository(friendshipId)
            .collect { fetchedChatNullable ->
                fetchedChatNullable?.let { fetchedChat ->

                    _actualChat.value = fetchedChat

                    Log.d(TAG, "getSelectedChat: fetchedData -> $fetchedChat")

                    if (fetchedChat.hasImage) {

                        Picasso
                            .get()
                            .load("$BASE_URL$USER_URL${fetchedChat.userId}/photo")
                            .resize(50, 50)
                            .centerCrop()

                            .into(object : Target {
                                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                    Log.d(
                                        TAG,
                                        "loadImage: onPrepareLoad: loading image user id ${fetchedChat.userId}"
                                    )
                                }

                                override fun onBitmapFailed(
                                    e: Exception?,
                                    errorDrawable: Drawable?
                                ) {
                                    //Handle the exception here
                                    Log.d(TAG, "loadImage: onBitmapFailed: error $e")
                                }

                                override fun onBitmapLoaded(
                                    bitmap: Bitmap?,
                                    from: Picasso.LoadedFrom?
                                ) {

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
                } ?: Log.d(TAG, "getSelectedChat: getChat Flow was null")

            }
    }


    fun addMessage(msg: String){

        updateChat(
            MessageView(
                messageId = -1,
                text = msg,
                date = LocalDate.now().toString(),
                time = LocalTime.now().toString(),
                user = getId()
            )
        )

        viewModelScope.launch {
            chatRepository.addMessageRepository(friendshipId, msg)
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

                if (msg.messageView.user != getId() &&
                    msg.channel == friendshipId
                ) {
                    updateChat(msg.messageView)
                }

            }
        }
    }

    private fun updateChat(msg: MessageView) {

        _actualChat.value = _actualChat.value.copy(
            messages = _actualChat.value.messages
                .toMutableList()
                .also { msgListMutable ->
                    msgListMutable.add(msg)
                }
        )
    }

}