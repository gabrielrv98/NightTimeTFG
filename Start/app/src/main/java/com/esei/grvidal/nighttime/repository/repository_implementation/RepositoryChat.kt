package com.esei.grvidal.nighttime.repository.repository_implementation

import android.util.Log
import com.esei.grvidal.nighttime.network.ERROR_HEADER_TAG
import com.esei.grvidal.nighttime.network.NightTimeService
import com.esei.grvidal.nighttime.network.network_DTOs.ChatView
import com.esei.grvidal.nighttime.network.network_DTOs.MessageForm
import com.esei.grvidal.nighttime.network.network_DTOs.UserToken
import com.esei.grvidal.nighttime.repository.interface_repository.IRepositoryChat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

private const val TAG = "RepositoryChat"

class RepositoryChat(
    private val userToken: UserToken,
    private val apiService: NightTimeService
): IRepositoryChat {

    override fun getId(): Long {
        return userToken.id
    }

    override suspend fun getChatDataRepository(friendshipId: Long): Flow<ChatView?> {
        var chatToret: ChatView? = null
        try {

            val webResponse = apiService.getSelectedChat(
                idUser = userToken.id,
                auth = userToken.token,
                idFriendship = friendshipId
            )

            if (webResponse.isSuccessful)
                chatToret = webResponse.body()


        } catch (e: IOException) {
            Log.e(TAG, "getChatDataRepository: network exception (no network)  --//-- $e")

        } catch (e: Exception) {
            Log.e(TAG, "getChatDataRepository: general exception  --//-- $e")

        } finally {

            return flow { emit(chatToret) }
        }

    }

    override suspend fun addMessageRepository(friendshipId: Long, msg: String) {
        try {

            val webResponse = apiService.sendMessage(
                auth = userToken.token,
                idUser = userToken.id,
                messageForm = MessageForm(
                    friendshipId = friendshipId,
                    text = msg
                )
            )

            Log.d(
                TAG,
                "addMessageRepository: response ${webResponse.body()} - code ${webResponse.code()} - header error ${webResponse.headers()[ERROR_HEADER_TAG]}"
            )

        } catch (e: IOException) {
            Log.d(TAG, "addMessageRepository: network exception ${e.message}  --//-- $e")
        } catch (e: Exception) {

            Log.d(TAG, "addMessageRepository: network exception ${e.message}  --//-- $e")
        }
    }


}