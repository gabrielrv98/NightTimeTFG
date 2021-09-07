package com.esei.grvidal.nighttime

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.esei.grvidal.nighttime.fakeData.allUsersList
import com.esei.grvidal.nighttime.fakeData.getImageResource
import com.esei.grvidal.nighttime.network.network_DTOs.UserFriendView
import com.esei.grvidal.nighttime.network.network_DTOs.UserSnapImage
import com.esei.grvidal.nighttime.pages.bar_pages.makeLongShort

/**
 * Custom dialog with the formatted shape
 *
 * @param onClose is the action that will be done when the dialog closes, usually, it will set
 * the variable showDialog to false
 * @param content content to show inside
 */
@Composable
fun CustomDialog(
    onClose: () -> Unit,
    dialogBorder: BorderStroke = BorderStroke(3.dp, MaterialTheme.colors.primary),
    dialogShape: Shape = MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {

    Dialog(onDismissRequest = onClose) {
        //Surface with the shape, border and color
        Surface(
            modifier = Modifier
                .padding(24.dp)
                .border(
                    border = dialogBorder,
                    shape = dialogShape
                )
                .padding(1.dp),
            color = MaterialTheme.colors.background,
            shape = MaterialTheme.shapes.medium,
            elevation = 1.dp
        ) {
            Column {

                //searchBar()

                Row(
                    modifier = Modifier.padding(horizontal = 20.dp)
                        .padding(top = 20.dp, bottom = 0.dp)
                ) {
                    Column {
                        content()
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    //Close button
                    Button(
                        modifier = Modifier
                            .padding(bottom = 18.dp, end = 24.dp, top = 12.dp),
                        onClick = onClose
                    ) {
                        Text(text = stringResource(R.string.cerrar))
                    }
                }
            }
        }
    }
}


/**
 * Dialog that shows the friends who are coming out the selected date
 *
 * @param modifier custom modifier
 * @param userList list with the users to show
 */
@Composable
fun UsersSnapListDialog(
    userList: List<UserSnapImage>,
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
                .clickable(onClick = { onItemClick(user.userId) })
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

                // TODO: 06/09/2021 FAKE Data image
                /*
                user.img?.let {
                    Image(asset = it)
                } ?: Icon(asset = Icons.Default.Person)
                */
                allUsersList.find { it.nickname == user.username }?.let { user ->
                    user.picture?.let {
                        Image(asset = imageResource(id = it))
                    } ?: Icon(asset = Icons.Default.Person)
                }

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


/**
 * Dialog that shows the friends who are coming out the selected date
 *
 * @param modifier custom modifier
 * @param friendshipList list with the users to show
 */
@Composable
fun FriendshipRequestListDialog(
    friendshipList: List<UserFriendView>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onAccept: (Long, String) -> Unit,
    onDecline: (Long, String) -> Unit,
    onItemClick: ((Long) -> Unit)? = null
) {

    //List with the users
    LazyColumnFor(
        items = friendshipList,
        modifier = modifier,
        state = listState
    ) { friendship ->

        val modifierUser = if (onItemClick != null)
            Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = { onItemClick(friendship.userId) })
        else Modifier

        //Each user
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .then(modifierUser),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // User Info
            Row {

                //Image
                Surface(
                    modifier = Modifier
                        .preferredSize(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                ) {
                        // TODO: 07/09/2021 FAKE DATA IMAGE REQUEST FRIENDSHIP
                    getImageResource(friendship.userId)?.let {
                        Image(asset = imageResource(id = it))
                    } ?: Icon(asset = Icons.Default.Person)

                }

                //Name
                Text(
                    text = makeLongShort(friendship.userNickname, 8),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                )
            }


            // Accept / Decline buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier
                        .padding(6.dp)
                        .wrapContentSize()
                        .clip(CircleShape)
                        .clickable(onClick = {
                            onAccept(
                                friendship.friendshipId,
                                friendship.userNickname
                            )
                        }),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Icon(asset = Icons.Filled.Add)
                }

                Surface(
                    modifier = Modifier
                        .padding(6.dp)
                        .wrapContentSize()
                        .clip(CircleShape)
                        .clickable(onClick = {
                            onDecline(
                                friendship.friendshipId,
                                friendship.userNickname
                            )
                        }),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Icon(asset = Icons.Filled.Close)
                }
            }


        }
    }

}





























