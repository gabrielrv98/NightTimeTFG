package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.LastBaseline
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.esei.grvidal.nighttime.BottomNavigationScreens
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.FullChat
import com.esei.grvidal.nighttime.data.User
import com.esei.grvidal.nighttime.navigateWithId
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.semantics
import com.esei.grvidal.nighttime.data.Message
import java.time.LocalDate


/**
 * Composable that cheks if [chatId] is null, if its false it will show the conversation
 *
 * @param navController controller of hte navigation, its used to go back or navigate to other views
 * @param chatId Id of the current char
 */
@Composable
fun ChatConversationPageAND(navController: NavHostController, chatId: Int?) {
    val user = User("me")

    //Nullable check
    if (chatId == null) {
        errorComposable(errorText = stringResource(id = R.string.errorChatId))
    } else {

        ConversationContent(
            actualChat = user.getChatConversation(chatId),
            navigateToProfile = { userId ->
                navController.navigateWithId(
                    BottomNavigationScreens.Profile.route,
                    userId
                )
            },
            onNavIconPressed = {
                navController.popBackStack(navController.graph.startDestination, false)
                navController.navigate(BottomNavigationScreens.Friends.route)
            },
            user = user
        )



    }
}

/**
 * Entry point for a conversation screen.
 *
 * @param actualChat [ConversationUiState] that contains messages to display
 * @param navigateToProfile User action when navigation to a profile is requested
 * @param modifier [Modifier] to apply to this layout node
 * @param onNavIconPressed Sends an event up when the user clicks on the menu
 */
@Composable
fun ConversationContent(
    user : User,
    actualChat: FullChat,
    navigateToProfile: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { }
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier,
        color = MaterialTheme.colors.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {

                Messages(
                    messages = actualChat.messages,
                    navigateToProfile = navigateToProfile,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                    userId = user.id
                )

                UserInput(
                    onMessageSent = { content ->
                        actualChat.addMessage(
                            Message(user.id, content, LocalDate.now().toString())
                        )
                    },
                    scrollState
                )


            }
            // Channel name bar floats above the messages
            ChatNameBar(
                channelName = actualChat.otherUserName,
                onBackIconPressed = onNavIconPressed
            )
        }
    }
}



@Composable
fun ChatNameBar(
    channelName: String,
    modifier: Modifier = Modifier,
    onBackIconPressed: () -> Unit = { }
) {

    TopAppBar(
        modifier = modifier,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Channel name
                Text(
                    text = channelName,
                    style = MaterialTheme.typography.subtitle1
                )
            }

        },
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.95f),
        elevation = 0.dp,
        contentColor = MaterialTheme.colors.onSurface,
        actions = {
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                // Search icon
                Icon(
                    asset = Icons.Outlined.Search,
                    modifier = Modifier
                        .clickable(onClick = {}) // TODO: Show not implemented dialog.
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .preferredHeight(24.dp)
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBackIconPressed) {
                Icon(asset = Icons.Default.ArrowBack)
            }
        }
    )
    Divider()
}


@Composable
fun Messages(
    userId : Int,
    messages: List<Message>,
    navigateToProfile: (Int) -> Unit,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {

        ScrollableColumn(
            scrollState = scrollState,
            reverseScrollDirection = true,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.preferredHeight(64.dp))
            messages.forEachIndexed { index, content ->
                val prevAuthor = messages.getOrNull(index - 1)?.idUser
                val nextAuthor = messages.getOrNull(index + 1)?.idUser
                val isFirstMessageByAuthor = prevAuthor != content.idUser
                val isLastMessageByAuthor = nextAuthor != content.idUser

                // Hardcode day dividers for simplicity//todo acabar
                if (index == 0) {
                    DayHeader("20 Aug")
                } else if (index == 4) {
                    DayHeader("Today")
                }

                Message(
                    onAuthorClick = {
                        navigateToProfile(content.idUser)
                    },
                    msg = content,
                    isUserMe = content.idUser == userId,
                    isFirstMessageByAuthor = isFirstMessageByAuthor,
                    isLastMessageByAuthor = isLastMessageByAuthor
                )
            }
        }
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(DensityAmbient.current) {
            (56.dp).toPx()
        }

        // Apply the threshold:
        val jumpToBottomButtonEnabled = scrollState.value > jumpThreshold

        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scrollState.smoothScrollTo(0f)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun Message(
    onAuthorClick: () -> Unit,
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {

    val borderColor = if (isUserMe) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.secondary
    }

    val spaceBetweenAuthors = if (isFirstMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    Row(modifier = spaceBetweenAuthors) {
        if (isFirstMessageByAuthor) {
            // Avatar
            Image(
                modifier = Modifier
                    .clickable(onClick = onAuthorClick)
                    .padding(horizontal = 16.dp)
                    .preferredSize(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
                    .border(3.dp, MaterialTheme.colors.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                asset = Icons.Default.Person,
                contentScale = ContentScale.Crop
            )
        } else {
            // Space under avatar
            Spacer(modifier = Modifier.preferredWidth(74.dp))
        }
        AuthorAndTextMessage(
            msg = msg,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f)
        )
    }
}


@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (isFirstMessageByAuthor) {
            AuthorNameTimestamp(msg)
        }
        ChatItemBubble(msg, isLastMessageByAuthor)
        if (isLastMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.preferredHeight(8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.preferredHeight(4.dp))
        }
    }
}


private val ChatBubbleShape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)
private val LastChatBubbleShape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 8.dp)

@Composable
fun ChatItemBubble(
    message: Message,
    lastMessageByAuthor: Boolean
) {

    val backgroundBubbleColor = Color(0xFFF5F5F5)//todo cambiar

    val bubbleShape = if (lastMessageByAuthor) LastChatBubbleShape else ChatBubbleShape
    Column {
        Surface(color = backgroundBubbleColor, shape = bubbleShape) {
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
                Text(
                    text = message.messageText,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }

        message.image?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(color = backgroundBubbleColor, shape = bubbleShape) {
                Image(
                    asset = imageResource(it),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.preferredSize(160.dp)
                )
            }
        }
    }
}


@Composable
private fun AuthorNameTimestamp(msg: Message) {
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeAllDescendants = true) {}) {
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
            Text(
                text = msg.idUser.toString(),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .alignBy(LastBaseline)
                    .relativePaddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
            )
        }
        Spacer(modifier = Modifier.preferredWidth(8.dp))
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
            Text(
                text = msg.timestamp,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
    }
}

@Composable
fun DayHeader(dayString: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).preferredHeight(16.dp)) {
        DayHeaderLine()
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
            Text(
                text = dayString,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.overline
            )
        }
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    Divider(
        modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
    )
}













