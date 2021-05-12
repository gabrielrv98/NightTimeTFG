package com.esei.grvidal.nighttime.network

import android.util.Log
import com.esei.grvidal.nighttime.network.network_DTOs.UserToken
import com.esei.grvidal.nighttime.network.network_DTOs.MessageView
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.EmptyCoroutineContext


data class MessageListened(
    val channel: Long,
    val messageView: MessageView
)


private const val TAG = "ChatListener"

class ChatListener(
    private val externalScope: CoroutineScope = CoroutineScope(context = EmptyCoroutineContext),
    private val userToken: UserToken
) {

    private val options = PusherOptions().apply {
        setCluster("eu")
    }

    private val pusher = Pusher("55882193049f9860a4af", options)

    //list of friendships and chats
    private var friendshipIdList = listOf<Long>()

    // Hot stream flow used to send notifications
    private val _events = MutableSharedFlow<MessageListened>() // private mutable shared flow
    val events = _events.asSharedFlow() // publicly exposed as read-only shared flow


    init {

        externalScope.launch {

            getFriendshipsIds()
            for (id in friendshipIdList) {
                listenToChannel(id)
            }
        }

    }


    private suspend fun getFriendshipsIds() {

        try {

            val webResponse = NightTimeService.NightTimeApi.retrofitService.getFriendshipsIds(
                idUser = userToken.id,
                auth = userToken.token
            )

            if (webResponse.isSuccessful) {
                webResponse.body()?.let { idList ->
                    friendshipIdList = idList

                    Log.d(
                        TAG,
                        "getFriendshipsIds: total friendships ${friendshipIdList.size}"
                    )

                }

            } else {
                Log.d(
                    TAG,
                    "getFriendshipsIds: webResponse error ${webResponse.headers()[ERROR_HEADER_TAG] ?: "Unknown error"}"
                )
            }

        } catch (e: IOException) {
            Log.e(TAG, "getFriendshipsIds: network exception (no network)  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "getFriendshipsIds: general exception  --//-- $e")
        }

    }

    private fun listenToChannel(channelNameId: Long) {

        val channel = pusher.subscribe(channelNameId.toString())

        channel.bind("new_message") { channelName: String, _, data ->
            val jsonObject = JSONObject(data)

            val message = MessageView(
                jsonObject["messageId"].toString().toLong(),
                jsonObject["text"].toString(),
                jsonObject["date"].toString(),
                jsonObject["time"].toString(),
                jsonObject["user"].toString().toLong()
            )

            Log.d(TAG, "newMessage received: $message")

            emitMessage(channelName, message)
        }
        pusher.connect()
    }

    private fun emitMessage(channelName: String, message: MessageView) = externalScope.launch {
        _events.emit(
            MessageListened(
                channel = channelName.toLong(),
                messageView = message
            )
        )
    }

}