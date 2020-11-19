package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.MoreVert
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
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.BottomNavigationScreens
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.chatutil.ConversationContent
import com.esei.grvidal.nighttime.components.AnimatingFabContent
import com.esei.grvidal.nighttime.data.*

@Composable
fun ProfilePageView(navController: NavController, userId : Int?){



    //Nullable check
    if (userId == null) {
        errorComposable(errorText = stringResource(id = R.string.errorProfileId))
    } else {
        val user =  if(userId == meUser.id)  meUser else userPreview // user = UserDao.getUserbyId(userId)

        ProfilePage(user.toProfileScreenState())

    }
}

@Composable
fun ProfilePage( user : ProfileScreenState ){

    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        WithConstraints {
            Box(modifier = Modifier.weight(1f)) {
                Surface {
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
                    userIsMe = user.id == meUser.id,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
private fun UserInfoFields(userData: ProfileScreenState, containerHeight: Dp) {
    Column {
        Spacer(modifier = Modifier.preferredHeight(8.dp))

        NameAndPosition(userData)

        ProfileProperty(stringResource(R.string.display_name), userData.name)

        ProfileProperty(stringResource(R.string.status), userData.status)

        // Add a spacer that always shows part (320.dp) of the fields list regardless of the device,
        // in order to always leave some content at the top.
        Spacer(Modifier.preferredHeight((containerHeight - 320.dp).coerceAtLeast(0.dp)))
    }
}


@Composable
private fun NameAndPosition(
    userData: ProfileScreenState
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Name(
            userData,
            modifier = Modifier//.baselineHeight(32.dp)//todo eliminado esto
        )
    }
}


@Composable
private fun Name(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
        Text(
            text = userData.name,
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
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Divider()
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
            Text(
                text = label,
                modifier = Modifier,//.baselineHeight(24.dp),//Todo eliminado esto
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
                modifier = Modifier,//.baselineHeight(24.dp),//Todo eliminado esto
                style = style
            )
        }
    }
}

@Composable
fun ProfileFab(extended: Boolean, userIsMe: Boolean, modifier: Modifier = Modifier) {
    key(userIsMe) { // Prevent multiple invocations to execute during composition
        FloatingActionButton(
            onClick = { /* TODO */ },
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
    ProfilePage(userPreview.toProfileScreenState())
}

@Preview
@Composable
fun ProfileFabPreview() {
        ProfileFab(extended = true, userIsMe = false)
}
