package com.esei.grvidal.nighttime.pages.profile_pages

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.PhotoState
import com.esei.grvidal.nighttime.data.UserViewModel
import com.esei.grvidal.nighttime.pages.login_pages.TextWithInput
import com.esei.grvidal.nighttime.scaffold.BottomNavigationScreens
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

private const val TAG = "ProfileEditor"

//Permission code
const val PERMISSION_CODE = 1001

@Composable
fun ProfileEditorPage(
    navController: NavHostController,
    searchImage: () -> Unit,
    setLoginCredentials: (String) -> Unit,
    user: UserViewModel
) {


    Log.d(TAG, "ProfileEditorPage: starting userPic ${user.userPicture.toString()}")

    onCommit(user.user.id) {
        user.fetchEditData()
    }


    onCommit(user.uriPhotoPicasso) {

        Log.d(TAG, "ProfileEditorPage: onCommit actual uri = ${user.uriPhotoPicasso.toString()}")
        user.uriPhotoPicasso?.let { uri ->

            Log.d(TAG, "ProfileEditorPage: onCommit fetching from  $uri")

            Picasso.get()
                .load(uri)
                .resize(500, 500)
                .centerCrop()
                .into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        Log.d(
                            TAG,
                            "fetchUserPics: onPrepareLoad: loading"
                        )
                        user.photoState = PhotoState.LOADING
                        user.userPicture = null
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        //Handle the exception here
                        Log.d(TAG, "fetchUserPics: onBitmapFailed: error $e")
                        user.photoState = PhotoState.ERROR
                        user.userPicture = null
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                        //Here we get the loaded image
                        Log.d(
                            TAG,
                            "fetchUserPics: onBitmapLoaded: Image fetched "
                        )
                        bitmap?.let { img ->
                            user.userPicture = img.asImageAsset()
                            user.photoState = PhotoState.DONE
                        }

                    }
                })
        }

        onDispose {
            user.lock = false
        }
    }

    val context = ContextAmbient.current

    ProfileEditorScreen(
        name = user.name,
        setName = { text -> user.name = text },
        state = user.state,
        setState = { text -> user.state = text },
        password = user.password,
        setPassword = { text -> user.password = text },
        email = user.email,
        setEmail = { text -> user.email = text },
        img = user.userPicture,
        photoState = user.photoState,
        saveData = {
            user.saveData(
                setLoginCredentials,
                user.uriPhotoPicasso?.let { getPathFromURI(context, it) }
            )
        },
        searchImageButton = {

            if (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PermissionChecker.PERMISSION_DENIED
            ) {
                Log.d(TAG, "ProfileEditorPage: Permission READ_EXTERNAL_STORAGE was denied ")

                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(context as Activity, permissions, PERMISSION_CODE)


            } else {

                Log.d(TAG, "ProfileEditorPage: Permission READ_EXTERNAL_STORAGE was granted ")
                //permission already granted
                pickImageFromGallery(searchImage)
            }

        },
        goBack = {
            navController.popBackStack(navController.graph.startDestination, false)
            navController.navigate(BottomNavigationScreens.ProfileNav.route)
        }
    )
}


@OptIn(ExperimentalFocus::class)
@Composable
fun ProfileEditorScreen(
    name: TextFieldValue,
    setName: (TextFieldValue) -> Unit,
    state: TextFieldValue,
    setState: (TextFieldValue) -> Unit,
    password: TextFieldValue,
    setPassword: (TextFieldValue) -> Unit,
    email: TextFieldValue,
    setEmail: (TextFieldValue) -> Unit,
    img: ImageAsset?,
    photoState: PhotoState,
    saveData: () -> Unit,
    searchImageButton: () -> Unit,
    goBack: () -> Unit
) {

    val scrollState = rememberScrollState()

    val focusRequester = remember { FocusRequester() }

    ScrollableColumn(
        modifier = Modifier.fillMaxSize(),
        scrollState = scrollState
    ) {

        ProfileHeader(
            scrollState = scrollState,
            asset = img,
            photoState = photoState,
        ) { modifier ->

            val difference = scrollState.value / 40
            val offset = 12 - difference
            Log.d(
                TAG,
                "ProfileEditorScreen: scroll state = ${scrollState.value} difference = $difference offset = $offset"
            )
            val offsetDp = with(DensityAmbient.current) {
                if (offset >= 0) offset.toDp()
                else 0.dp
            }
            val myModifier = modifier
                .padding(bottom = offsetDp)

            Surface(
                modifier = myModifier
                    .padding(end = 6.dp)
                    .size(45.dp)
                    .clip(CircleShape)
                    .clickable(onClick = { searchImageButton() })
            ) {
                Icon(
                    modifier = Modifier,//.padding(3.dp),
                    asset = Icons.Default.Edit,
                    tint = MaterialTheme.colors.primary
                )
            }


        }
        Spacer(Modifier.preferredHeight(25.dp))

        TextChanger(
            title = stringResource(id = R.string.display_name),
            focusRequester = focusRequester,
            canContainSpace = true,
            value = name,
            setValue = setName
        )

        TextChanger(
            title = stringResource(id = R.string.state),
            focusRequester = focusRequester,
            canBeEmpty = true,
            canContainSpace = true,
            maxLetters = 50,
            value = state,
            setValue = setState
        )

        TextChanger(
            title = stringResource(id = R.string.email),
            focusRequester = focusRequester,
            value = email,
            setValue = setEmail
        )

        TextWithInputPassword(
            focusRequester = focusRequester,
            password = password,
            setPassword = setPassword
        )


        AcceptDeclineButtons(
            accept = {
                // save password in storage
                saveData()
                goBack()
            },
            decline = { goBack() }
        )
    }


}


fun getPathFromURI(context: Context, uri: Uri): String {
    Log.d(TAG, "getPathFromURI: uri = $uri")
    var realPath = String()
    uri.path?.let { path ->

        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (path.contains("/document/image:")) { // files selected from "Documents"
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
        } else { // files selected from all other sources, especially on Samsung devices
            databaseUri = uri
            selection = null
            selectionArgs = null
        }
        try {
            val column = "_data"
            val projection = arrayOf(column)
            val cursor = context.contentResolver.query(
                databaseUri,
                projection,
                selection,
                selectionArgs,
                null
            )
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    realPath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            println(e)
        }
    }
    return realPath
}

fun pickImageFromGallery(searchImage: () -> Unit) {
    Log.d(TAG, "pickImageFromGallery: starting")
    searchImage()
}

@Composable
private fun AcceptDeclineButtons(
    accept: () -> Unit,
    decline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButtonEditProfile(
            Icons.Outlined.Cancel,
            decline,
            stringResource(id = R.string.cancel)
        )

        IconButtonEditProfile(Icons.Outlined.Check, accept, stringResource(id = R.string.accept))
    }
}

@Composable
private fun IconButtonEditProfile(
    icon: VectorAsset,
    accept: () -> Unit,
    text: String = ""
) {
    IconButton(
        modifier = Modifier.padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        onClick = accept
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(asset = icon)
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                Text(
                    text,
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 10.sp

                )
            }

        }

    }
}


@OptIn(ExperimentalFocus::class)
@Composable
fun TextChanger(
    modifier: Modifier = Modifier,
    title: String,
    focusRequester: FocusRequester = remember { FocusRequester() },
    canBeEmpty: Boolean = false,
    canContainSpace: Boolean = false,
    isPassword: Boolean = false,
    maxLetters: Int = 25,
    value: TextFieldValue,
    setValue: (TextFieldValue) -> Unit,
    content: @Composable ((Modifier) -> Unit)? = null
) {

    Column(
        modifier = modifier.padding(horizontal = 24.dp)
            .padding(vertical = 6.dp)
    ) {
        Row {
            Text(text = title)
            if (!canBeEmpty) {
                Advert(value.text.isBlank(), Modifier.align(Alignment.Bottom))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextWithInput(
                modifier = Modifier
                    .weight(0.8F)
                    .focusRequester(focusRequester = focusRequester),
                text = value,
                setText = { newValue -> setValue(newValue) },
                isPassword = isPassword,
                canContainSpace = canContainSpace,
                maxLetters = maxLetters,
                onImePerformed = { softwareController ->
                    softwareController?.hideSoftwareKeyboard()
                }
            ) {
                Text(text = title)
            }

            content?.let { content ->
                Spacer(modifier = Modifier.weight(0.1F))

                Surface(
                    modifier = modifier
                        .weight(0.1F)
                        .clip(CircleShape),
                    elevation = 1.dp,
                    color = MaterialTheme.colors.background
                ) {
                    content(Modifier.align(alignment = Alignment.CenterVertically))
                }

            }
        }
    }

}


@OptIn(ExperimentalFocus::class)
@Composable
fun TextWithInputPassword(
    focusRequester: FocusRequester,
    password: TextFieldValue,
    setPassword: (TextFieldValue) -> Unit
) {

    val interactionState =
        remember { InteractionState() } // remember { MutableInteractionSource() }
    val showPassword =
        interactionState.contains(Interaction.Pressed)// interactionSource.collectIsPressedAsState()

    TextChanger(
        modifier = Modifier,
        title = stringResource(id = R.string.password),
        focusRequester = focusRequester,
        isPassword = !showPassword,
        value = password,
        setValue = setPassword
    ) { modifier ->


        Icon(
            modifier = modifier
                .clip(CircleShape)
                .clickable(
                    onClick = {},
                    interactionState = interactionState
                ),
            asset = if (showPassword) Icons.Default.Visibility
            else Icons.Default.VisibilityOff,
        )
    }
}


@Composable
fun Advert(isBlank: Boolean, modifier: Modifier) {
    ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
        Text(
            modifier = modifier.padding(start = 6.dp),
            style = MaterialTheme.typography.subtitle1,
            fontSize = 12.sp,
            text = "*" + stringResource(id = R.string.cant_be_empty),
            color = if (isBlank) MaterialTheme.colors.error
            else MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        )
    }
}