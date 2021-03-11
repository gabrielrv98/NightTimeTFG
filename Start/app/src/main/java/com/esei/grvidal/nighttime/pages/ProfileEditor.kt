package com.esei.grvidal.nighttime.pages

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.*
//import androidx.activity.compose.registerForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.ContextAmbient
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
import com.esei.grvidal.nighttime.data.User
import com.esei.grvidal.nighttime.data.LoginViewModel
import com.esei.grvidal.nighttime.data.meUser
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
    login: LoginViewModel
) {

    val (name, setName) = remember { mutableStateOf(TextFieldValue(meUser.name)) }
    val (status, setStatus) = remember { mutableStateOf(TextFieldValue(meUser.status)) }

    var img by remember { mutableStateOf<ImageAsset?>(null) }
    var drawable by remember { mutableStateOf<Drawable?>(null) }

    onCommit(login.uriPhoto) {

        Log.d(TAG, "ProfileEditorPage: new uri = ${login.uriPhoto}")
        login.uriPhoto.let { uri ->

            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_broken_image)
                .resize(500, 500)
                .into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        Log.d(
                            TAG,
                            "fetchUserPics: onPrepareLoad: loading"
                        )
                        drawable = placeHolderDrawable
                        img = null
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        //Handle the exception here
                        Log.d(TAG, "fetchUserPics: onBitmapFailed: error $e")
                        drawable = errorDrawable
                        img = null
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                        //Here we get the loaded image
                        Log.d(
                            TAG,
                            "fetchUserPics: onBitmapLoaded: Image fetched "
                        )
                        if (bitmap != null) {
                            img = bitmap.asImageAsset()
                        }

                    }
                })
        }

        onDispose {
            img = null
            drawable = null
        }
    }

    val context = ContextAmbient.current

    ProfileEditorScreen(
        name = name,
        setName = setName,
        status = status,
        setStatus = setStatus,
        img = img,
        drawable = drawable,
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

@Composable
fun ProfileEditorScreen(
    name: TextFieldValue,
    setName: (TextFieldValue) -> Unit,
    status: TextFieldValue,
    setStatus: (TextFieldValue) -> Unit,
    img: ImageAsset?,
    drawable: Drawable?,
    searchImageButton: () -> Unit,
    goBack: () -> Unit
) {

val scrollState = rememberScrollState()

    ScrollableColumn(
        modifier = Modifier.fillMaxSize(),
        scrollState = scrollState
    ) {

        ProfileHeader(
            scrollState,
            img,
            drawable
        ){ modifier ->

            Icon(
                asset = Icons.Default.Edit,
                modifier = modifier.clip(CircleShape)
                    .clickable(onClick = { searchImageButton() } )
                    .size(25.dp),
                tint = MaterialTheme.colors.primary
            )

        }

        TextChanger(
            name = name,
            set = { text -> setName(text) },
            text = stringResource(id = R.string.display_name)
        )

        TextChanger(
            name = status,
            set = { text -> setStatus(text) },
            canBeEmpty = true,
            text = stringResource(id = R.string.status)
        )
        AcceptDeclineButtons(
            accept = {
                saveData(user = meUser, name = name, status = status)
                goBack()
            },
            decline = { goBack() }
        )

        Text("pene")
        Text("pene")
        Text("pene")
        Text("pene")
        Text("pene")
        Text("pene")
        Text("pene")
        Text("pene")
        Text("pene")
    }


}

@Composable
fun UserPicture(
    modifier: Modifier = Modifier,
    CircleShape: Shape,
    img: ImageAsset?,
    drawable: Drawable?,
    searchImageButton: () -> Unit
) {
    Row(
        modifier = Modifier.padding(20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = modifier
                .clip(shape = CircleShape)
        ) {


            if (img != null) {
                Log.d(TAG, "fetchUserPics: image is visible")
                Image(
                    asset = img,
                    modifier = modifier
                )

            } else {
                Log.d(TAG, "fetchUserPics: image was null")
                Canvas(
                    modifier = modifier
                ) {
                    drawIntoCanvas {
                        drawable?.draw(it.nativeCanvas) ?: Icons.Default.VerifiedUser
                    }
                }
            }
            Button(
                onClick = { searchImageButton() },
                modifier = Modifier.align(Alignment.BottomEnd)
                    .preferredSize(50.dp)
            ) {
                Icon(Icons.Default.Edit)
            }
        }
    }
}

fun pickImageFromGallery(searchImage: () -> Unit) {
    Log.d(TAG, "pickImageFromGallery: starting")
    searchImage()

}


/*
@Composable
fun pickImageFromGallery( ) {
    Log.d(TAG, "pickImageFromGallery: starting")

    val launcher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        result.value = it
    }

    Button(onClick = { launcher.launch() }) {
        Text(text = "Take a picture")
    }

    val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d(com.esei.grvidal.nighttime.TAG, "settingNewURi:  uri = $uri")
            userVM.uriPhoto = uri
        }

}
 */







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
            stringResource(id = R.string.cancelar)
        )

        IconButtonEditProfile(Icons.Outlined.Check, accept, stringResource(id = R.string.aceptar))
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

fun saveData(user: User, name: TextFieldValue, status: TextFieldValue) {

    if (name.text.trim() != "")
        user.name = name.text.trim()
    user.status = status.text.trim()
}

@Composable
fun TextChanger(
    name: TextFieldValue,
    set: (TextFieldValue) -> Unit,
    canBeEmpty: Boolean = false,
    text: String
) {

    Row(
        modifier = Modifier.padding(horizontal = 24.dp)
            .padding(bottom = 6.dp, top = 6.dp)
    ) {
        Column {
            Row {
                Text(text = text)
                if (!canBeEmpty) {
                    Advert(name.text, Modifier.align(Alignment.Bottom))
                }
            }

            TextField(
                modifier = Modifier.padding(horizontal = 6.dp)
                    .padding(top = 4.dp),
                value = name, onValueChange = { set(it) })
        }
    }
}

@Composable
fun Advert(text: String, modifier: Modifier) {
    ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
        Text(
            modifier = modifier.padding(start = 6.dp),
            style = MaterialTheme.typography.subtitle1,
            fontSize = 12.sp,
            text = "*" + stringResource(id = R.string.cant_be_empty),
            color = if (text.isBlank()) MaterialTheme.colors.error else MaterialTheme.colors.onSurface
        )
    }
}