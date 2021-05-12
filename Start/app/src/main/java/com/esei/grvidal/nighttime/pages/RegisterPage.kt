package com.esei.grvidal.nighttime.pages

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.ErrorHolder
import com.esei.grvidal.nighttime.data.LoginViewModel
import com.esei.grvidal.nighttime.data.PhotoState
import com.esei.grvidal.nighttime.data.UserViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


private const val TAG = "RegisterPage"

@Composable
fun RegisterPage(
    loginVM: LoginViewModel,
    userVM: UserViewModel,
    searchImageButton: () -> Unit
) {

    onCommit(userVM.uriPhotoPicasso) {

        Log.d(TAG, "RegisterPage: onCommit actual uri = ${userVM.uriPhotoPicasso.toString()}")
        userVM.uriPhotoPicasso?.let { uri ->

            Log.d(TAG, "RegisterPage: onCommit fetching from  $uri")

            Picasso.get()
                .load(uri)
                .resize(500, 500)
                .centerCrop()
                .into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        Log.d(
                            TAG,
                            "RegisterPage fetchUserPics: onPrepareLoad: loading"
                        )
                        userVM.photoState = PhotoState.LOADING
                        userVM.userPicture = null
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        //Handle the exception here
                        Log.d(TAG, "RegisterPage fetchUserPics: onBitmapFailed: error $e")
                        userVM.photoState = PhotoState.ERROR
                        userVM.userPicture = null
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                        //Here we get the loaded image
                        Log.d(
                            TAG,
                            "RegisterPage fetchUserPics: onBitmapLoaded: Image fetched "
                        )
                        bitmap?.let { img ->
                            userVM.userPicture = img.asImageAsset()
                            userVM.photoState = PhotoState.DONE
                        }

                    }
                })
        }
    }


    val context = ContextAmbient.current

    RegisterScreen(
        username = userVM.username,
        setUsername = { txt -> userVM.username = txt },
        name = userVM.name,
        setName = { txt -> userVM.name = txt },
        state = userVM.state,
        setState = { txt -> userVM.state = txt },
        password = userVM.password,
        setPassword = { txt -> userVM.password = txt },
        email = userVM.email,
        setEmail = { txt -> userVM.email = txt },
        img = userVM.userPicture,
        photoState = userVM.photoState,
        errorText = userVM.errorText,
        searchImageButton = {
            if (PermissionChecker.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
                PermissionChecker.PERMISSION_DENIED
            ) {
                Log.d(TAG, "RegisterPage: Permission READ_EXTERNAL_STORAGE was denied ")

                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                ActivityCompat.requestPermissions(context as Activity, permissions, PERMISSION_CODE)


            } else {

                Log.d(TAG, "RegisterPage: Permission READ_EXTERNAL_STORAGE was granted ")
                //permission already granted
                pickImageFromGallery(searchImageButton)
            }
        }
    ) {
        // Adding user to server
        userVM.newUser(
            userVM.uriPhotoPicasso?.let {
                getPathFromURI(context, it)
            },
            loginVM::doLoginRefreshed
        )

    }

}


@OptIn(ExperimentalFocus::class)
@Composable
fun RegisterScreen(
    username: TextFieldValue,
    setUsername: (TextFieldValue) -> Unit,
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
    errorText: ErrorHolder,
    searchImageButton: () -> Unit,
    register: () -> Unit
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
                "RegisterScreen: scroll state = ${scrollState.value} difference = $difference offset = $offset"
            )
            val offsetDp = with(DensityAmbient.current) {
                if (offset >= 0) offset.toDp()
                else 0.dp
            }


            Surface(
                modifier = modifier
                    .padding(bottom = offsetDp)
                    .padding(end = 6.dp)
                    .size(45.dp)
                    .clip(CircleShape)
                    .clickable(onClick = { searchImageButton() })
            ) {
                Icon(
                    modifier = Modifier,
                    asset = Icons.Default.Edit,
                    tint = MaterialTheme.colors.primary
                )
            }


        }
        Spacer(Modifier.preferredHeight(25.dp))

        if (errorText.errorString != null || errorText.resourceInt != null) {
            Text(
                text = errorText.resourceInt?.let{
                    stringResource(id = it)
                } ?:
                errorText.errorString ?:
                stringResource(id = R.string.unexpected_error),

                color = Color.Red,
                style = MaterialTheme.typography.body1
            )
        }

        TextChanger(
            title = stringResource(id = R.string.username),
            focusRequester = remember { FocusRequester() },
            canContainSpace = false,
            value = username,
            setValue = setUsername
        )

        TextChanger(
            title = stringResource(id = R.string.display_name),
            focusRequester = remember { FocusRequester() },
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

        Button(
            onClick = { register() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.confirm))
        }
    }
}