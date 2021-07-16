package com.esei.grvidal.nighttime.repository.interface_repository

import com.esei.grvidal.nighttime.network.network_DTOs.ChatView
import kotlinx.coroutines.flow.Flow

interface IRepositoryChat {
    suspend fun getChatDataRepository(friendshipId: Long): Flow<ChatView?>

    suspend fun addMessageRepository(friendshipId: Long, msg: String)
    fun getId(): Long
}