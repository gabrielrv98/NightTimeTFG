package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Create
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.NavigationScreens
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.animations.AnimatingFabContent
import com.esei.grvidal.nighttime.data.*
import com.esei.grvidal.nighttime.navigateWithId

@Composable
fun ProfilePageView(navController: NavHostController, userId: Int?, user: UserViewModel) {

    Column{
        ProfileProperty(
            "token: ",
            if (user.loggedUser.token.isNotEmpty())
                user.loggedUser.token

            else
                "Vacio"
        )

        Button(modifier = Modifier,onClick = {
            user.logOff()
        }){
            Text("Log off")
        }
    }


    //Nullable check
    if (userId == null) {
        ErrorComposable(errorText = stringResource(id = R.string.errorProfileId))
    } else {
        //Datos del usuario
        val userData =
            if (userId == meUser.id) meUser else userPreview // user = UserDao.getUserbyId(userId)

        //ProfilePage(user.toProfileScreenState())
        //viewModel.component1().userData.observeAsState().value.let { userData: ProfileScreenState? ->

        if (userData.id == -1) {
            ErrorComposable(errorText = stringResource(id = R.string.errorProfileId))
        } else {
            val onFavButtonClick = if (userData.toProfileScreenState().isMe()) {
                {
                    navController.navigate(NavigationScreens.ProfileEditor.route)
                }
            } else {
                {
                    navController.navigateWithId(
                        NavigationScreens.ChatConversation.route,
                        userData.id
                    )// or ...route , userId)
                }
            }
            ProfilePage(
                user = userData.toProfileScreenState(),
                onClick = onFavButtonClick
            )
            //ProfilePage(userData)
        }
        //}


    }
}


@Composable
fun ProfilePage(user: ProfileScreenState, onClick: () -> Unit) {

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
                            scrollState,
                            user
                        )
                        UserInfoFields(user, maxHeight)
                    }
                }
                ProfileFab(
                    extended = scrollState.value == 0f,
                    userIsMe = user.isMe(),
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
private fun UserInfoFields(userData: ProfileScreenState, containerHeight: Dp) {
    Column {
        Spacer(modifier = Modifier.preferredHeight(8.dp))

        nickName(
            userData = userData,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 4.dp)
        )

        ProfileProperty(stringResource(R.string.display_name), userData.name)

        ProfileProperty(stringResource(R.string.status), userData.status)

        userData.nextDate?.let {
            ProfileProperty(stringResource(R.string.nextDate), it.toStringFormatted())
        }


        // Add a spacer that always shows part (320.dp) of the fields list regardless of the device,
        // in order to always leave some content at the top.
        Spacer(Modifier.preferredHeight((containerHeight - 320.dp).coerceAtLeast(0.dp)))
    }
}

@Composable
private fun nickName(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
        Text(
            text = userData.nickname,
            modifier = modifier,
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
private fun ProfileHeader(
    scrollState: ScrollState,
    data: ProfileScreenState
) {
    val offset = (scrollState.value / 2)
    val offsetDp = with(DensityAmbient.current) { offset.toDp() }

    data.photo?.let {
        val asset = imageResource(id = it)
        val ratioAsset = asset.width / asset.height.toFloat()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Allow for landscape and portrait ratios
                .preferredHeightIn(max = 320.dp)
                .aspectRatio(ratioAsset)
                .background(Color.LightGray)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = offsetDp),
                asset = asset,
                contentScale = ContentScale.Crop
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
fun ConvPreview480MeDefault() {
    ProfilePage(userPreview.toProfileScreenState()) {}
}

@Preview
@Composable
fun ProfileFabPreview() {
    ProfileFab(extended = true, userIsMe = false)
}
