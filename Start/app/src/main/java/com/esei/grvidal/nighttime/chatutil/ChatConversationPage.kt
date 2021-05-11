package com.esei.grvidal.nighttime.chatutil

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.esei.grvidal.nighttime.scaffold.BottomNavigationScreens
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.navigateWithId
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.data.*
import com.esei.grvidal.nighttime.network.MessageListened
import com.esei.grvidal.nighttime.network.network_DTOs.MessageView
import com.esei.grvidal.nighttime.pages.ErrorComposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.EmptyCoroutineContext

private const val TAG = "ChatConversationPage"

/**
 * Composable that checks if [chatVM] has friendshipId as -1, if it's false it will show the conversation
 *
 * @param navController controller of hte navigation, its used to go back or navigate to other views
 * @param [chatVM] Data of the current chat
 */
@Composable
fun ChatConversationPage(
    navController: NavHostController,
    chatVM: ChatViewModel,
    flow: SharedFlow<MessageListened>,
    friendshipId: Long
) {

    //Nullable check
    if (friendshipId == -1L) {
        ErrorComposable(errorText = stringResource(id = R.string.errorChatId))
    } else {

//        LaunchedEffect(key1 = chatVM.friendshipId ){
//            chatVM.setFlow(this, flow)
//        }


        DisposableEffect(chatVM.friendshipId) {
            val coroutineScope = CoroutineScope(context = EmptyCoroutineContext)

            chatVM.getSelectedChat(friendshipId)
            chatVM.setFlow(coroutineScope, flow)
            onDispose {
                coroutineScope.cancel()
                chatVM.image = null
            }
        }


        ConversationContent(
            userId = chatVM.getId(),
            userNickname = chatVM.userNickname,
            messages = chatVM.messages,
            navigateToProfile = {
                navController.navigateWithId(
                    BottomNavigationScreens.ProfileNav.route,
                    chatVM.otherUserId
                )
            },
            onBackIconPressed = {
                navController.popBackStack(navController.graph.startDestination, false)
                navController.navigate(BottomNavigationScreens.FriendsNav.route)
            },
            addMessage = chatVM::addMessage,
            image = chatVM.image
        )


    }
}

/**
 * Entry point for a conversation screen.
 *
 * @param navigateToProfile User action when navigation to a profile is requested
 * @param modifier [Modifier] to apply to this layout node
 * @param onBackIconPressed Sends an event up when the user clicks on the menu
 */
@Composable
fun ConversationContent(
    userId: Long,
    userNickname: String,
    messages: List<MessageView>,
    navigateToProfile: () -> Unit,
    addMessage: (String) -> Unit,
    onBackIconPressed: () -> Unit,
    modifier: Modifier = Modifier,
    image: ImageAsset? = null,
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier,
        color = MaterialTheme.colors.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(Modifier.fillMaxSize()) {

                Messages(
                    messages = messages,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                    userId = userId
                )

                UserInput(
                    onMessageSent = { content ->
                        addMessage(content)
                    },
                    scrollState
                )
            }

            // Channel name bar floats above the messages
            ChatNameBar(
                channelName = userNickname,
                navigateToProfile = navigateToProfile,
                onBackIconPressed = onBackIconPressed,
                image = image
            )
        }

    }
}


@Composable
fun ChatNameBar(
    modifier: Modifier = Modifier,
    channelName: String,
    navigateToProfile: () -> Unit,
    image: ImageAsset? = null ,
    onBackIconPressed: () -> Unit = { },
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
                    UserImage(
                        image = image,
                        modifier = Modifier
                            .align(Alignment.Top)
                            .padding(end = Icons.Default.ArrowBack.defaultWidth)
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
fun UserImage(
    image: ImageAsset?,
    modifier: Modifier = Modifier
){
    image?.let{
        Image(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(37.dp)//Previous value 42
                .border(1.5.dp, MaterialTheme.colors.primary, CircleShape)
                .border(3.dp, MaterialTheme.colors.surface, CircleShape)
                .clip(CircleShape),
            asset = it,
            contentScale = ContentScale.Crop
        )
    } ?: Image(
        modifier = Modifier
            .padding(end = 8.dp)
            .preferredSize(37.dp)//Previous value 42
            .border(1.5.dp, MaterialTheme.colors.primary, CircleShape)
            .border(3.dp, MaterialTheme.colors.surface, CircleShape)
            .clip(CircleShape)
            .then(modifier),
        asset = Icons.Default.Person,
        contentScale = ContentScale.Crop
    )
}


@Composable
fun Messages(
    userId: Long,
    messages: List<MessageView>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {

    val today = LocalDate.now()
    Box(modifier = modifier) {

        ScrollableColumn(
            scrollState = scrollState,
            reverseScrollDirection = true,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.preferredHeight(64.dp))

            // Always show the date of the first message
            if (messages.isNotEmpty())
                DayHeader(
                    dateFormatted(
                        messages[0].date,
                        DateTimeFormatter.ofPattern("dd-MMMM")
                    )
                )

            messages.forEachIndexed { index, content ->
                val prevAuthor = messages.getOrNull(index - 1)?.user
                val nextAuthor = messages.getOrNull(index + 1)?.user
                val isFirstMessageByAuthor = prevAuthor != content.user
                val isLastMessageByAuthor = nextAuthor != content.user

                // If the message has different date than the previous message date is shown
                if (index > 0 &&
                    dateFormatted(messages[index].date) != dateFormatted(messages[index - 1].date)
                ) {

                    // If the message is from the actual date a string is shown
                    if (messages[index].date == today.toString())
                        DayHeader(stringResource(R.string.hoy))
                    else DayHeader(
                        dateFormatted(
                            content.date,
                            DateTimeFormatter.ofPattern("dd-MMMM")
                        )
                    )
                }

                Message(
                    message = content,
                    isUserMe = content.user == userId,
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

/**
 * Parse function that takes as input a string with pattern
 * yyyy-mm-dd or
 * {"year":yyyy,"month":mm,"day":dd}
 *
 * @param time unformatted date as String
 */
fun dateFormatted(
    time: String,
    formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
): String {
    val timeSplit: List<String>
    val date: LocalDate
    Log.d(TAG, "dateFormatted: $time")

    if (Regex("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}").matches(time)) {
        timeSplit = time.split("-")
        date = LocalDate.of(timeSplit[0].toInt(), timeSplit[1].toInt(), timeSplit[2].toInt())

    } else {
        timeSplit = time.split(",")
        date = LocalDate.of(
            timeSplit[0].split(":")[1].toInt(),
            timeSplit[1].split(":")[1].toInt(),
            timeSplit[2].split(":")[1].dropLastWhile { !it.isDigit() }.toInt()
        )

    }

    return date.format(formatter)
}


/**
 * Parse function that takes as input a string with pattern
 * {"hour":hh,"minute":mm,"second":ss,"nanosecond":nn}
 *
 * @param time unformatted time as String
 */
fun timeFormatted(
    time: String
): String {
    Log.d(TAG, "timeFormatted: $time")

    return if (!Regex("[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}").matches(time)) {

        val timeSplit = time.split(",")
        val localTime = LocalTime.of(
            timeSplit[0].split(":")[1].toInt(),
            timeSplit[1].split(":")[1].toInt(),
            timeSplit[2].split(":")[1].toInt()
        )
        localTime.toString()

    } else time
}


@Composable
fun Message(
    message: MessageView,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {

    val spaceBetweenAuthors = if (isFirstMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    val chatArrangement = if (isUserMe) Arrangement.End else Arrangement.Start
    Row(
        modifier = spaceBetweenAuthors.fillMaxWidth(),
        horizontalArrangement = chatArrangement
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
    message: MessageView,
    isLastMessageByAuthor: Boolean
) {

    ChatItemBubble(isUserMe, message, isLastMessageByAuthor)

    if (isLastMessageByAuthor) {
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {

                Text(
                    modifier = Modifier.padding(start = if (isUserMe) 50.dp else 25.dp),
                    text = timeFormatted(message.time),
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
    message: MessageView,
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
    ChatNameBar(
        channelName = "Nuria Soto Marco",
        navigateToProfile = {}
    )
}











