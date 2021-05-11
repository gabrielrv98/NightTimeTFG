package com.esei.grvidal.nighttime.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.esei.grvidal.nighttime.scaffold.NavigationScreens
import androidx.navigation.NavHostController
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.*
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.*
import com.esei.grvidal.nighttime.network.MessageListened
import com.esei.grvidal.nighttime.network.network_DTOs.ChatFullView
import com.esei.grvidal.nighttime.network.network_DTOs.UserSnapImage
import com.esei.grvidal.nighttime.scaffold.BottomNavigationScreens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharedFlow
import kotlin.coroutines.EmptyCoroutineContext


private const val TAG = "FriendsPage"

/**
 * Composable that manages the View of the list of chats
 *
 * @param navController  controller of hte navigation, its used to go back or navigate to other views
 */

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun FriendsPage(
    navController: NavHostController,
    friendsVM: FriendsViewModel,
    flow: SharedFlow<MessageListened>
) {

    onCommit(friendsVM.getId()) {
        val coroutineScope = CoroutineScope(context = EmptyCoroutineContext)

        Log.d(TAG, "FriendsPage: onCommit")
        friendsVM.getChats()
        friendsVM.getRequestingFriendships()
        friendsVM.setFlow(coroutineScope, flow)

        onDispose {
            coroutineScope.cancel()
        }
    }

    var requestFriendshipDialog by remember { mutableStateOf(false) }

    // Requesting friendship dialog
    if (requestFriendshipDialog) {

        FriendshipRequestDialog(
            navController = navController,
            requestList = friendsVM.requestingFriendshipUserList,
            answerFriendship = friendsVM::answerFriendshipRequest,
            closeRequestFriendshipDialog = { requestFriendshipDialog = false }
        )
    }


    FriendsScreen(
        chatList = friendsVM.chatList.collectAsState().value,
        onChatClick = { chatId ->

            navController.navigateWithId(
                NavigationScreens.ChatConversation.route,
                chatId
            )
        },
        numberRequestFriendship = friendsVM.totalRequestingFriendship,
        showDialog = { requestFriendshipDialog = true },
        fetchFriendList = friendsVM::getFriendshipsIds,
        friendsList = friendsVM.friendshipIdList.toList(),
        numberOfFriends = friendsVM.totalFriends
    )

}

@Composable
private fun FriendshipRequestDialog(
    navController: NavHostController,
    requestList: List<UserFriendView>,
    answerFriendship: (Long, Boolean) -> Unit,
    closeRequestFriendshipDialog: () -> Unit
) {
    val context = ContextAmbient.current

    CustomDialog(onClose = { closeRequestFriendshipDialog() }) {

        Text(
            text = stringResource(R.string.friendship_requested_to_you),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FriendshipRequestListDialog(
            friendshipList = requestList,
            onAccept = { idFriendship, userNickname ->

                answerFriendship(idFriendship, true)
                Toast.makeText(
                    context,
                    "${context.getString(R.string.acceptingUser)} $userNickname",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDecline = { idFriendship, userNickname ->

                answerFriendship(idFriendship, false)
                Toast.makeText(
                    context,
                    "${context.getString(R.string.refusingUser)} $userNickname",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onItemClick = { id ->
                navController.navigateWithId(
                    BottomNavigationScreens.ProfileNav.route,
                    id
                )
            }
        )
    }
}

@Composable
fun FriendsScreen(
    chatList: List<ChatFullView>,
    onChatClick: (Long) -> Unit,
    numberRequestFriendship: Int,
    showDialog: () -> Unit,
    friendsList: List<UserSnapImage>,
    fetchFriendList: () -> Unit,
    numberOfFriends: Int
) {
    for ((i, chatsAux) in chatList.withIndex())
        Log.d(
            TAG,
            "setFlow: $i After all ${chatsAux.userNickname}  - last ${chatsAux.messages[0].text} - unread ${chatsAux.unreadMessages}"
        )

    Log.d(TAG, "FriendsScreen: chatList $chatList")

    val (showNewConversationDialog, setNewConversationShowDialog) = remember { mutableStateOf(false) }

    if (showNewConversationDialog)
        UserSnapList(
            userList = friendsList,
            numberOfFriends = numberOfFriends,
            loadMoreFriends = fetchFriendList,
            closeDialog = { setNewConversationShowDialog(false) },
            onClick = onChatClick
        )
    // TODO: 10/05/2021 show all users to start a new Chat



    Box(
        modifier = Modifier
            .fillMaxHeight()
    ) {

        Column {
            Header(
                text = stringResource(id = R.string.chat_title),
                modifier = Modifier.padding(top = 24.dp),
                icon = { iconModifier ->
                    Box(
                        modifier = iconModifier
                            .padding(end = 17.dp, bottom = 15.dp),
                        alignment = Alignment.Center

                    ) {
                        Surface(
                            modifier = Modifier
                                .wrapContentSize()
                                .clip(shape = CircleShape)
                                .clickable(onClick = { showDialog() }),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, SolidColor(MaterialTheme.colors.primary))
                        ) {
                            Icon(
                                asset = Icons.Default.Add,
                                modifier = Modifier.padding(3.dp)
                            )
                        }

                        if (numberRequestFriendship > 0)
                            Surface(
                                modifier = Modifier
                                    //.wrapContentHeight()
                                    .padding(bottom = 23.dp, start = 26.dp)
                                    //.align(Alignment.TopEnd)
                                    .clip(shape = CircleShape),
                                color = Color.Red,
                                shape = CircleShape
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .clip(CircleShape),
                                    text = numberRequestFriendship.toString(),
                                    color = Color.White,

                                    )
                            }

                    }

                }
            )

            LazyColumnFor(items = chatList) { chatData ->
                Log.d(TAG, "FriendsScreen: setFlow Recomposing ${chatData.userNickname}")

                ChatEntry(
                    userName = chatData.userNickname,
                    lastMessage = chatData.messages[0].text,
                    unreadMessages = chatData.unreadMessages,
                    img = chatData.img,
                    onEntryClick = { onChatClick(chatData.friendshipId) }
                )
            }

        }

        FloatingActionButton(
            onClick = {
                setNewConversationShowDialog(true)
                if (friendsList.isEmpty())
                    fetchFriendList()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .preferredHeight(48.dp)
                .widthIn(min = 48.dp),
        ) {
            Icon(asset = Icons.Default.Add)
        }


    }


}


/**
 * Composable that shows the information about the chat
 */
@Composable
fun ChatEntry(
    userName: String,
    lastMessage: String,
    unreadMessages: Boolean,
    img: ImageAsset? = null,
    onEntryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp)
            .clickable(onClick = onEntryClick)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                modifier = Modifier.preferredSize(45.dp).align(Alignment.CenterVertically),
                color = Color.Gray
            ) {
                Log.d(
                    TAG,
                    "ChatEntry: lastRecomposition user $userName, hasImg ${img != null} "
                )

                img?.let {
                    Image(
                        asset = it,
                        contentScale = ContentScale.Crop
                    )
                } ?: Icon(asset = Icons.Default.Person)
            }

            Column(
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold
                )
                Text(text = lastMessage, style = MaterialTheme.typography.body2, maxLines = 1)
            }

            if (unreadMessages)
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(bottom = 23.dp, start = 26.dp)
                            .clip(shape = MaterialTheme.shapes.medium)
                            .align(Alignment.End),
                        color = Color.Red,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clip(MaterialTheme.shapes.medium),
                            text = stringResource(R.string.new_messages),
                            color = Color.White
                        )
                    }
                }


        }
        Divider(startIndent = 25.dp)
    }

}


@Composable
fun FriendsSearch(
    userList: List<UserSnapImage>,
    onSearch: (String) -> Unit,
    onClick: (Long) -> Unit
) {

    val (nicknameSearch, setNicknameSearch) = remember { mutableStateOf(TextFieldValue()) }
    val state = rememberLazyListState()

    if (userList.size - state.firstVisibleItemIndex <= 12) {
        Log.d(TAG, "FriendsSearch: userList size = ${userList.size}, calling more pages")
        onSearch(nicknameSearch.text)

    }

    TextChanger(
        modifier = Modifier.wrapContentSize(),
        title = stringResource(R.string.search_friend),
        canContainSpace = true,
        value = nicknameSearch,
        setValue = { textFV ->
            setNicknameSearch(textFV)
            onSearch(textFV.text)
        },
        canBeEmpty = true
    )
    Divider()

    UsersSnapListDialog(
        userList = userList,
        listState = state,
        onItemClick = onClick
    )


}

@Preview
@Composable
fun previewSearch() {
    MaterialTheme {
        FriendsSearch(onClick = {}, onSearch = {}, userList = listOf(
            UserSnapImage(1, "Nuria", "pinknut", false, null),
            UserSnapImage(1, "Miguel", "emikepick", false, null),
            UserSnapImage(1, "Maria", "dulceFlor", false, null),
            UserSnapImage(1, "Marcos", "Tigre", false, null),
            UserSnapImage(1, "Laura", "laux21", false, null),
            UserSnapImage(1, "Sara", "saraaldo", false, null),
            UserSnapImage(1, "Julio", "itsme", false, null),
            UserSnapImage(1, "Juan", "john32", false, null),
            UserSnapImage(1, "Pedro", "pcsantiago", false, null),
            UserSnapImage(1, "Salva", "Salvador", false, null)
        )
        )
    }
}