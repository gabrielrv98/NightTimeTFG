package com.esei.grvidal.nighttime.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esei.grvidal.nighttime.scaffold.NavigationScreens
import androidx.navigation.NavHostController
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.*
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.viewmodels.*
import com.esei.grvidal.nighttime.network.MessageListened
import com.esei.grvidal.nighttime.network.network_DTOs.*
import com.esei.grvidal.nighttime.pages.bar_pages.Header
import com.esei.grvidal.nighttime.pages.bar_pages.makeLongShort
import com.esei.grvidal.nighttime.pages.profile_pages.TextChanger
import com.esei.grvidal.nighttime.scaffold.BottomNavigationScreens
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow
import kotlin.coroutines.EmptyCoroutineContext


private const val TAG = "FriendsPage"

@Composable
fun FriendsInit(
    navController: NavHostController,
    userToken: UserToken,
    flow: SharedFlow<MessageListened>,
    showDialog: MutableState<Boolean>,
) {
    val friendsVM: FriendsViewModel =
        viewModel("friendsVM", factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {

                if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {

                    @Suppress("UNCHECKED_CAST")
                    return FriendsViewModel(userToken) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }

        })

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

    FriendsPage(
        navController = navController,
        showDialog = showDialog,
        friendsVM = friendsVM
    )
}

/**
 * Composable that manages the View of the list of chats
 *
 * @param navController  controller of hte navigation, its used to go back or navigate to other views
 */

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun FriendsPage(
    navController: NavHostController,
    showDialog: MutableState<Boolean>,
    friendsVM: FriendsViewModel,
) {

    var requestFriendshipDialog by remember { mutableStateOf(false) }

    if (showDialog.value) {
        CustomDialog(
            onClose = { showDialog.value = false }
        ) {
            FriendsSearch(
                // TODO: 06/09/2021 FAKE DATA fakes search user
                //onSearch = friendsVM::searchUsers,
                onSearch = friendsVM::fakeSearchUsers,
                onClick = { userId ->

                    friendsVM.clearSearchedList()

                    navController.navigateWithId(
                        BottomNavigationScreens.ProfileNav.route,
                        userId
                    )

                },
                userList = friendsVM.searchedUserList
            )
        }
    }

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
    friendsList: List<FriendshipSnapImage>,
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
        FriendSnapList(
            friendList = friendsList,
            numberOfFriends = numberOfFriends,
            loadMoreFriends = fetchFriendList,
            closeDialog = { setNewConversationShowDialog(false) },
            onClick = onChatClick
        )



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
                                    .padding(bottom = 23.dp, start = 26.dp)
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
                    onEntryClick = {
                        Log.d(TAG, "FriendsScreen: chatSend - clicking on ${chatData.friendshipId}")
                        onChatClick(chatData.friendshipId)
                    }
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
    unreadMessages: Int,
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
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .preferredSize(45.dp)
                    .align(Alignment.CenterVertically),
                shape = RoundedCornerShape(50),
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
                } ?: Icon(
                    asset = Icons.Default.Person
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = makeLongShort(lastMessage, 30),
                    style = MaterialTheme.typography.body2,
                    maxLines = 1
                )
            }

            if (unreadMessages > 0)
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(bottom = 23.dp, start = 26.dp)
                            .clip(shape = CircleShape)
                            .align(Alignment.End),
                        color = Color.Red,
                        shape = CircleShape
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clip(CircleShape),
                            text = unreadMessages.toString(),
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


@Composable
fun FriendSnapList(
    friendList: List<FriendshipSnapImage>,
    numberOfFriends: Int,
    loadMoreFriends: () -> Unit,
    closeDialog: () -> Unit,
    onClick: ((Long) -> Unit)? = null
) {

    val state = rememberLazyListState()

    Log.d(
        TAG,
        "UserListOnDate: size: ${friendList.size}  state : ${state.firstVisibleItemIndex} "
    )


    /**
     * [LazyListState.firstVisibleItemIndex] points at the number of items already scrolled
     *
     * So if userList is not empty then we check if the remaining users in userList are 15 or less
     * (Full screen of the app),
     * if so, more users from API are fetched
     */
    if (numberOfFriends > 0 &&
        friendList.size < numberOfFriends &&
        // total - cursor ( la posicion actual) >= 12  ->( los objetos restantes son 12 ( 9 mostrados en pantalla, 3 restantes por abajo ))
        (friendList.size - state.firstVisibleItemIndex <= 12 || friendList.isEmpty())
    ) {

        loadMoreFriends()
    }



    CustomDialog(onClose = closeDialog) {
        FriendSnapListDialog(
            userList = friendList,
            modifier = Modifier.preferredHeight(600.dp),
            listState = state,
            onItemClick = onClick
        )
    }
}


/**
 * Dialog that shows the friends who are coming out the selected date
 *
 * @param modifier custom modifier
 * @param userList list with the users to show
 */
@Composable
fun FriendSnapListDialog(
    userList: List<FriendshipSnapImage>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: ((Long) -> Unit)? = null
) {

    //List with the users
    LazyColumnFor(
        items = userList,
        modifier = modifier,
        state = listState
    ) { user ->

        val modifierUser = if (onItemClick != null)
            Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = { onItemClick(user.friendshipId) })
        else Modifier

        //Each user
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 14.dp)
                .then(modifierUser)
        ) {
            //Image
            Surface(
                modifier = Modifier.preferredSize(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            ) {
                user.image?.let {
                    Image(asset = it)
                } ?: Icon(asset = Icons.Default.Person)

            }

            //Name
            Text(
                text = "${user.username} - ${user.name}",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }

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
