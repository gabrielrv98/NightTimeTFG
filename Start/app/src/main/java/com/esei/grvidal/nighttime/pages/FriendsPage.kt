package com.esei.grvidal.nighttime.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.esei.grvidal.nighttime.scaffold.NavigationScreens
import androidx.navigation.NavHostController
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.CustomDialog
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.*
import com.esei.grvidal.nighttime.navigateWithId
import com.esei.grvidal.nighttime.scaffold.BottomNavigationScreens


private const val TAG = "FriendsPage"

/**
 * Composable that manages the View of the list of chats
 *
 * @param navController  controller of hte navigation, its used to go back or navigate to other views
 */
@Composable
fun FriendsPage(navController: NavHostController, chatVM: ChatViewModel) {

    onCommit(chatVM.getId()) {

        chatVM.getChats()

    }

    if (chatVM.showDialog) {
        CustomDialog(
            onClose = { chatVM.setDialog(false) }
        ) {
            FriendsSearch(
                onSearch = chatVM::searchUsers,
                onClick = { userId ->
                    navController.navigateWithId(
                        BottomNavigationScreens.ProfileNav.route,
                        userId
                    )

                },
                users = chatVM.userList
            )
        }
    }

    FriendsScreen(
        chatList = chatVM.chatList
    ) { chatId ->
        navController.navigateWithId(
            NavigationScreens.ChatConversation.route,
            chatId
        )
    }

}

@Composable
fun FriendsScreen(
    chatList: List<ChatFullView>,
    onChatClick: (Long) -> Unit
) {
    Log.d(TAG, "FriendsScreen: photoList $chatList")
    Column {
        Header(
            text = "Chat",
            modifier = Modifier.padding(top = 24.dp)
        )

        LazyColumnFor(items = chatList) { chatData ->
            ChatEntry(
                userName = chatData.userNickname,
                lastMessage = chatData.messages[0].text,
                img = chatData.img,
                onEntryClick = { onChatClick(chatData.friendshipId) }
            )
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
                    "ChatEntry, timing: lasRecomposition user $userName, img ${img != null} "
                )

                img?.let {
                    Image(
                        asset = it,
                        contentScale = ContentScale.Crop
                    )
                }
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
        }
        Divider(startIndent = 25.dp)
    }

}


@Composable
fun FriendsSearch(
    onSearch: (String) -> Unit,
    onClick: (Long) -> Unit,
    users: List<UserSnapImage>
) {

    val (nicknameSearch, setNicknameSearch) = remember { mutableStateOf(TextFieldValue()) }
    val state = rememberLazyListState()

    Log.d(TAG, "FriendsSearch: userList = $users")

    Column {
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

        FriendlyUsersDialog(
            itemsUser = users,
            state = state,
            onClick = onClick
        )


    }
}

@Preview
@Composable
fun previewSearch() {
    MaterialTheme {
        FriendsSearch(onClick = {}, onSearch = {}, users = listOf(
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
