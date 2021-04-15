package com.esei.grvidal.nighttime.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Create
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.scaffold.NavigationScreens
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.animations.AnimatingFabContent
import com.esei.grvidal.nighttime.data.*
import com.esei.grvidal.nighttime.navigateWithId


private const val TAG = "ProfilePage"

@Composable
fun ProfilePageView(navController: NavHostController, userId: Long?, userVM: UserViewModel) {


    //Nullable check
    if (userId == null) {
        ErrorComposable(errorText = stringResource(id = R.string.errorProfileId))

    } else {

        //Get data when userId changes
        onCommit(userId) {

            userVM.fetchData(userId)

            onDispose {
                Log.d(TAG, "ProfilePageView: onDispose erasing data")
                userVM.eraseData()

            }
        }

        ProfilePage(
            name = userVM.user.name,
            nickname = userVM.user.nickname,
            state = userVM.user.state,
            nextDate = userVM.user.nextDate,
            img = userVM.userPicture,
            photoState = userVM.photoState,
            isMe = userId == userVM.getMyId(),
            onClick = if (userId == userVM.getMyId()) {
                {
                    userVM.lock = true
                    navController.navigate(NavigationScreens.ProfileEditor.route)
                }
            } else {
                {
                    navController.navigateWithId(
                        NavigationScreens.ChatConversation.route,
                        userId
                    )
                }
            }
        )


    }
}


@Composable
fun ProfilePage(
    name: String,
    nickname: String,
    state: String?,
    nextDate: NextDate?,
    img: ImageAsset?,
    photoState: PhotoState,
    isMe: Boolean,
    onClick: () -> Unit
) {

    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        WithConstraints {
            Box(modifier = Modifier.weight(1f)) {
                Surface(color = MaterialTheme.colors.background) {
                    ScrollableColumn(
                        modifier = Modifier.fillMaxSize(),
                        scrollState = scrollState
                    ) {
                        ProfileHeader(
                            scrollState = scrollState,
                            asset = img,
                            photoState = photoState
                        )
                        UserInfoFields(
                            name = name,
                            nickname = nickname,
                            state = state,
                            nextDate = nextDate,
                            containerHeight = maxHeight
                        )
                    }
                }
                ProfileFab(
                    extended = scrollState.value == 0f,
                    userIsMe = isMe,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
private fun UserInfoFields(
    name: String,
    nickname: String,
    state: String?,
    nextDate: NextDate?,
    containerHeight: Dp
) {
    Column {
        Spacer(modifier = Modifier.preferredHeight(8.dp))

        nickName(
            nickname = nickname,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 4.dp)
        )

        ProfileProperty(stringResource(R.string.display_name), name)

        ProfileProperty(stringResource(R.string.state), state ?: " ")

        nextDate?.let { nextDate ->
            ProfileProperty(stringResource(R.string.nextDate), nextDate.toString())
        }


        // Add a spacer that always shows part (320.dp) of the fields list regardless of the device,
        // in order to always leave some content at the top.
        Spacer(Modifier.preferredHeight((containerHeight - 320.dp).coerceAtLeast(0.dp)))
    }
}

@Composable
private fun nickName(nickname: String, modifier: Modifier = Modifier) {
    ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
        Text(
            text = nickname,
            modifier = modifier,
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun ProfileHeader(
    scrollState: ScrollState,
    asset: ImageAsset?,
    photoState: PhotoState,
    content: @Composable ((Modifier) -> Unit)? = null
) {

    val offset = (scrollState.value / 2)
    val offsetDp = with(DensityAmbient.current) { offset.toDp() }
    val modifier = Modifier
        .fillMaxSize()
        .padding(top = offsetDp)

    val ratioAsset: Float = if (asset != null) (asset.width / asset.height).toFloat()
    else 1F

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Allow for landscape and portrait ratios
            .preferredHeightIn(max = 320.dp)
            .aspectRatio(ratioAsset)
            .background(Color.LightGray)
    ) {

        if (asset != null) {
            Image(
                modifier = modifier,
                asset = asset,
                contentScale = ContentScale.Crop
            )

        } else {
            val context = ContextAmbient.current
            Box(
                modifier = modifier.fillMaxSize(),
                alignment = Alignment.Center
            ) {
                when (photoState) {
                    PhotoState.LOADING -> {
                        CircularProgressIndicator()
                    }
                    PhotoState.ERROR -> {

                        Icon(asset = Icons.Default.BrokenImage)
                        Toast.makeText(
                            context,
                            stringResource(id = R.string.errorPhoto),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    PhotoState.DONE -> {

                        Icon( asset =  Icons.Default.Person.copy(defaultHeight = 500.dp, defaultWidth = 500.dp))

                    }
                }

            }

        }

        if (content != null) {
            content(
                Modifier
                    .padding(bottom = 5.dp, end = 15.dp)
                    .preferredSize(25.dp)
                    .wrapContentHeight(Alignment.Bottom, true)
                    .align(Alignment.BottomEnd)
                    .then(modifier)
            )
        }
    }

}

@Composable
fun ProfileProperty(label: String, value: String, isLink: Boolean = false) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 2.dp)) {
        Divider()
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
            Text(
                text = label,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.caption
            )
        }
        val style = if (isLink) {
            MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.primary)
        } else {
            MaterialTheme.typography.body1
        }
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
            Text(
                text = value,
                modifier = Modifier,
                style = style
            )
        }
    }
}

@Composable
fun ProfileFab(
    extended: Boolean,
    userIsMe: Boolean,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    key(userIsMe) { // Prevent multiple invocations to execute during composition
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier
                .padding(16.dp)
                .preferredHeight(48.dp)
                .widthIn(min = 48.dp),
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        ) {
            AnimatingFabContent(
                icon = {
                    Icon(
                        asset = if (userIsMe) Icons.Outlined.Create else Icons.Outlined.Chat
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            id = if (userIsMe) R.string.edit_profile else R.string.sed_message
                        ),
                    )
                },
                extended = extended

            )
        }
    }
}

@Preview
@Composable
fun ProfileFabPreview() {
    ProfileFab(extended = true, userIsMe = false)
}
