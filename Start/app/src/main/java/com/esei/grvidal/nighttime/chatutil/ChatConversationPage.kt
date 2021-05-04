package com.esei.grvidal.nighttime.chatutil

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.esei.grvidal.nighttime.scaffold.BottomNavigationScreens
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.FullChat
import com.esei.grvidal.nighttime.navigateWithId
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.sp
//import androidx.compose.ui.res.imageResource
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.data.Message
import com.esei.grvidal.nighttime.pages.ErrorComposable
import java.time.LocalDate


/**
 * Composable that checks if [chatId] is null, if its false it will show the conversation
 *
 * @param navController controller of hte navigation, its used to go back or navigate to other views
 * @param chatId Id of the current char
 */
@Composable
fun ChatConversationPage(navController: NavHostController, chatId: Int?) {

    //Nullable check
    if (chatId == null) {
        ErrorComposable(errorText = stringResource(id = R.string.errorChatId))
    } else {

        ConversationContent(
            actualChat = FullChat(0, "other", 1 ,listOf() ),
            navigateToProfile = { userId ->
                navController.navigateWithId(
                    BottomNavigationScreens.ProfileNav.route,
                    userId.toLong()
                )
            },
            onBackIconPressed = {
                navController.popBackStack(navController.graph.startDestination, false)
                navController.navigate(BottomNavigationScreens.FriendsNav.route)
            } ,
            userId = 3
        )


    }
}

/**
 * Entry point for a conversation screen.
 *
 * @param actualChat [FullChat] that contains messages to display
 * @param navigateToProfile User action when navigation to a profile is requested
 * @param modifier [Modifier] to apply to this layout node
 * @param onBackIconPressed Sends an event up when the user clicks on the menu
 */
@Composable
fun ConversationContent(
    userId:Int,
    actualChat: FullChat,
    navigateToProfile: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onBackIconPressed: () -> Unit = { }
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
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                    userId = userId
                )

                UserInput(
                    onMessageSent = { content ->
                        actualChat.addMessage(
                            Message(userId, content.trim(), LocalDate.now().toString())
                        )
                    },
                    scrollState
                )
            }

            // Channel name bar floats above the messages
            ChatNameBar(
                channelName = actualChat.otherUserName,
                onBackIconPressed = onBackIconPressed,
                navigateToProfile = { navigateToProfile(actualChat.otherUserId) },
            )
        }
    }
}


@Composable
fun ChatNameBar(
    modifier: Modifier = Modifier,
    channelName: String,
    image: VectorAsset = Icons.Default.Person,
    onBackIconPressed: () -> Unit = { },
    navigateToProfile: () -> Unit,
) {

    TopAppBar(
        modifier = modifier,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clickable(onClick = navigateToProfile),
                //horizontalArrangement = Arrangement.Center
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Image(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .preferredSize(37.dp)//Previous value 42
                            .border(1.5.dp, MaterialTheme.colors.primary, CircleShape)
                            .border(3.dp, MaterialTheme.colors.surface, CircleShape)
                            .clip(CircleShape)
                            .align(Alignment.Top),
                        asset = image,
                        contentScale = ContentScale.Crop
                    )

                    // Channel name
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = channelName,
                        style = MaterialTheme.typography.h6
                    )
                }

            }
        },
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.95f),
        elevation = 3.dp,
        contentColor = MaterialTheme.colors.onSurface,
        actions = {
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                // Search icon
                Icon(
                    asset = Icons.Outlined.Search,
                    modifier = Modifier
                        .clickable(onClick = {}) // TODO: Search in the conversation.
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .preferredHeight(24.dp),
                    tint = Color.Transparent
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBackIconPressed
            ) {
                Icon(asset = Icons.Default.ArrowBack)
            }
        }
    )
    Divider()
}


@Composable
fun Messages(
    userId: Int,
    messages: List<Message>,
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

                // Hardcode day dividers for simplicity//todo acabar cuando sepa los datos
                if (index == 0) {
                    DayHeader("20 Aug")
                } else if (index == 4) {
                    DayHeader(stringResource(R.string.hoy))
                }

                Message(
                    message = content,
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
    message: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {

    val spaceBetweenAuthors = if (isFirstMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    val chatArrang = if (isUserMe) Arrangement.End else Arrangement.Start
    Row(
        modifier = spaceBetweenAuthors.fillMaxWidth(),
        horizontalArrangement = chatArrang
    ) {

        Column {
            AuthorAndTextMessage(
                isUserMe = isUserMe,
                message = message,
                isLastMessageByAuthor = isLastMessageByAuthor,
            )
        }

    }
}


@Composable
fun AuthorAndTextMessage(
    isUserMe: Boolean,
    message: Message,
    isLastMessageByAuthor: Boolean
) {

    ChatItemBubble(isUserMe, message, isLastMessageByAuthor)

    if (isLastMessageByAuthor) {
        //ChatTimestamp(message)
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
            Text(
                modifier = Modifier.padding( start = if ( isUserMe)  50.dp else 25.dp),
                text = message.timestamp,
                style = MaterialTheme.typography.caption,
                color = Color.Gray,
            )
        }
        // Last bubble before next author
        Spacer(modifier = Modifier.preferredHeight(8.dp))
    } else {
        // Between bubbles
        Spacer(modifier = Modifier.preferredHeight(4.dp))
    }

}


private val ChatBubbleShape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)
private val LastChatBubbleShape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 8.dp)

private val ChatBubbleShapeUser = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp)
private val LastChatBubbleShapeUser = RoundedCornerShape(8.dp, 0.dp, 8.dp, 8.dp)

@Composable
fun ChatItemBubble(
    isUserMe: Boolean,
    message: Message,
    lastMessageByAuthor: Boolean
) {
// chat Padding
    val modifier = if (isUserMe) Modifier.padding(start = 60.dp, end = 24.dp)
    else Modifier.padding(end = 60.dp, start = 24.dp)

    val backgroundBubbleColor = Color(0xFFF5F5F5)

    val bubbleShape = if (isUserMe) {
        if (lastMessageByAuthor) LastChatBubbleShapeUser else ChatBubbleShapeUser
    } else if (lastMessageByAuthor) LastChatBubbleShape else ChatBubbleShape


    Surface(
        modifier = modifier,
        color = backgroundBubbleColor,
        shape = bubbleShape
    ) {
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.body2,
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
/* //todo send photos
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

 */
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


@Preview("Top bar")
@Composable
fun TopBarPreview() {
    ChatNameBar(channelName ="Nuria Sotelo Domarco", navigateToProfile = {})
}











