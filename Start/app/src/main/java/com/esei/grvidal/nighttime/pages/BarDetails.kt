package com.esei.grvidal.nighttime.pages

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.BarViewModel
import com.esei.grvidal.nighttime.network.EventFromBar
import com.esei.grvidal.nighttime.scaffold.BottomNavigationScreens

private const val TAG = "BarDetails"

/**
 * Check of if the BarId is null, this could be by a problem in navHostController
 *
 * @param barId expected Int identification of the bar to show
 * @param navController navigator with the queue of destinies and it will be used to go back
 */
@Composable
fun BarDetails(
    navController: NavHostController,
    barId: Long,
    barVM: BarViewModel,
) {
    //Null Check
    if (barId == -1L) {
        ErrorComposable(errorText = stringResource(id = R.string.errorBarId))
    } else {

        onCommit(barId) {

            barVM.getSelectedBarDetails(barId)
            // Release space from memory when composable is being detached from composition
            onDispose {
                barVM.eraseSelectedBar()
            }
        }

        // Show details of the Bar
        ShowDetails(
            name = barVM.selectedBar.name,
            description = barVM.selectedBar.description,
            address = barVM.selectedBar.address,
            schedule = barVM.selectedBar.schedule,
            totalImages = barVM.totalNPhotos,
            nImages = barVM.nPhotos,
            images = barVM.barSelectedPhotos,
            fetchImages = barVM::fetchPhotos,
            events = barVM.barSelectedEvents,
            onBackPressed = {
                navController.popBackStack(navController.graph.startDestination, false)
                navController.navigate(BottomNavigationScreens.BarNav.route)
            }
        )
    }
}

/**
 * Basic Composable to show if a en error occurred
 *
 * @param errorText String with the error description to show on screen
 */
@Composable
fun ErrorComposable(errorText: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Text(
            text = errorText,
            modifier = Modifier
                .align(Alignment.Center),
            color = MaterialTheme.colors.error
        )
    }
}

/**
 * StateFull composable that manage the main composition of the BarDetails view
 *
 * @param name name of the bar
 * @param description description of the bar
 * @param address address of the bar used to send the url to google maps
 * @param schedule list of daily schedule
 * @param totalImages number of total images
 * @param nImages number of downloaded images
 * @param images array with the [ImageAsset]
 * @param fetchImages function to fetch more images when the row is ending
 * @param events events of the bar
 * @param onBackPressed action to be done when the icon with arrow back is pressed
 */
@Composable
fun ShowDetails(
    name: String,
    description: String,
    address: String,
    schedule: List<String>,
    totalImages: Int,
    nImages: Int,
    images: List<ImageAsset>,
    fetchImages: () -> Unit,
    events: List<EventFromBar>,
    onBackPressed: () -> Unit = {}
) {

    //ScrollableColumn with an horizontal padding
    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxSize()
    ) {
        WithConstraints {
            // FoundationLayoutBox(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.weight(1f)) {
                // ComposeMaterialSurface(color = MaterialTheme.colors.background) {
                Surface(color = MaterialTheme.colors.background) {
                    ScrollableColumn {


                        //Button with an icon of an arrow back, if pushed it will show the previous View
                        IconButton(onClick = onBackPressed) {
                            Icon(asset = Icons.Default.ArrowBack)
                        }
                        //Header of the page with the title
                        Header(
                            modifier = Modifier.padding(bottom = 12.dp),
                            text = name,
                            style = MaterialTheme.typography.h4
                        )

                        // Description of the bar
                        DetailView(
                            title = stringResource(R.string.descripcion)
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                text = description,
                                style = MaterialTheme.typography.body1
                            )
                        }
                        // Schedule of the bar
                        BarSchedule(schedule)

                        // Address
                        Localization(address)

                        // Multimedia
                        if (totalImages > 0) {
                            MultimediaView(
                                nImages = nImages,
                                images = images,
                                modifier = Modifier,
                                fetchImages = fetchImages
                            )
                        } else {
                            Log.d(TAG, "ShowDetails: no images $totalImages")
                        }


                        //Events of the bar
                        EventsBar(events)

                        // Add a spacer that always shows part (320.dp) of the fields list regardless of the device,
                        // in order to always leave some content at the top.
                        Log.d(TAG, "ShowDetails: maxHeight = $maxHeight")
                        //Spacer(Modifier.preferredHeight((maxHeight - 320.dp).coerceAtLeast(0.dp)))
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsBar(events: List<EventFromBar>) {
    DetailView(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .preferredHeight(300.dp),
        title = stringResource(id = R.string.nextEvents),
        icon = Icons.Outlined.LocalDrink
    ) {
        LazyColumnFor(
            items = events
        ) { event ->
            Event(
                title = event.date.toString(),
                description = event.description
            )
        }
    }
}

@Composable
private fun Localization(
    address: String
) {
    //Context to call google Maps
    val context = ContextAmbient.current

    //Localization of the bar
    DetailView(title = stringResource(R.string.localizacion),
        icon = Icons.Outlined.Place,//todo improve aesthetic
        titleToRight = {
            //Button that triggers Google Maps
            Button(
                onClick = {
                    val uri = "http://maps.google.co.in/maps?q=$address"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    context.startActivity(intent)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.openMaps),
                    style = MaterialTheme.typography.body2
                )
            }

        }
    ) {
        Text(address)
    }
}

@Composable
private fun BarSchedule(schedule: List<String>) {
    DetailView(title = stringResource(R.string.horario), icon = Icons.Outlined.Alarm,
        titleToRight = { WeekScheduleIcon(schedule) }
    ) {


        Column(
            modifier = Modifier.preferredWidth(250.dp)
        ) {
            Divider(thickness = 1.dp, color = AmbientContentColor.current.copy(alpha = 0.15f))
            DailySchedule(
                day = stringResource(id = R.string.lunes),
                schedule = if (schedule[0] != "") {
                    schedule[0]
                } else {
                    stringResource(
                        id = R.string.cerrado
                    )
                }
            )
            DailySchedule(
                day = stringResource(id = R.string.martes),
                schedule = if (schedule[1] != "") schedule[1]
                else stringResource(
                    id = R.string.cerrado
                )
            )
            DailySchedule(
                day = stringResource(id = R.string.miercoles),
                schedule = if (schedule[2] != "") schedule[2]
                else stringResource(
                    id = R.string.cerrado
                )
            )
            DailySchedule(
                day = stringResource(id = R.string.jueves),
                schedule = if (schedule[3] != "") schedule[3]
                else stringResource(
                    id = R.string.cerrado
                )
            )
            DailySchedule(
                day = stringResource(id = R.string.viernes),
                schedule = if (schedule[4] != "") schedule[4]
                else stringResource(
                    id = R.string.cerrado
                )
            )
            DailySchedule(
                day = stringResource(id = R.string.sabado),
                schedule = if (schedule[5] != "") schedule[5]
                else stringResource(
                    id = R.string.cerrado
                )
            )
            DailySchedule(
                day = stringResource(id = R.string.domingo),
                schedule = if (schedule[6] != "") schedule[6]
                else stringResource(
                    id = R.string.cerrado
                )
            )
        }

    }
}

@Composable
fun MultimediaView(
    nImages: Int,
    images: List<ImageAsset>,
    modifier: Modifier,
    fetchImages: () -> Unit
) {

    val (showAlert, setShowAlert) = remember { mutableStateOf(false) }
    val (selectedImage, setSelectedImage) = remember { mutableStateOf<ImageAsset?>(null) }

    //Text Multimedia with the Icon
    DetailView(
        title = stringResource(id = R.string.mutlimedia),
        icon = Icons.Outlined.PhotoLibrary
    )

    val scrollState = rememberLazyListState()


    // If image list is empty load 5 first images
    if (nImages - scrollState.firstVisibleItemIndex <= 4) {
        Log.d(TAG, "ShowDetails: fetching new Images, actual $nImages")
        fetchImages()
    }

    if (images.isEmpty()) {
        Log.d(TAG, "ShowDetails: no images yet")
        Row {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colors.primary
            )
            Spacer(Modifier)
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colors.primary
            )
            Spacer(Modifier)
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colors.primary
            )
        }
    } else {
        Log.d(TAG, "ShowDetails: showing ${images.size} images")
        //Scrollable Row with the multimedia images

        LazyRowFor(
            items = images,
            state = scrollState,
            modifier = modifier.fillMaxWidth()
        ) { img ->

            Image(
                asset = img,
                modifier = Modifier
                    .padding(2.dp)
                    .preferredSize(120.dp)
                    .background(Color.Gray)
                    .clickable(onClick = {
                        setSelectedImage(img)
                        setShowAlert(true)
                    })
            )

        }


    }




    Spacer(modifier = Modifier)


    //Alert ready to be called
    BigPicture(
        showAlert, selectedImage
    ) {
        setShowAlert(false)
        setSelectedImage(null)
    }
}


/**
 * Composable that will check if the variable showAlert is true, if it is, it will show and alert
 * with the selected photo using all the screen
 *
 * @param showAlert Boolean to show the alert
 * @param selectedImage Selected image to be shown
 * @param dismissAlert lambda function to be done when user click outside of the alert or the dismiss button
 */
@Composable
private fun BigPicture(
    showAlert: Boolean,
    selectedImage: ImageAsset?,
    dismissAlert: () -> Unit
) {

    if (showAlert) {
        AlertDialog(
            onDismissRequest = dismissAlert,
            buttons = {
                Button(onClick = dismissAlert) {
                    Text(stringResource(id = R.string.cerrar))
                }
            },
            text =
            {
                if (selectedImage != null) {
                    Image(
                        asset = selectedImage,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(stringResource(id = R.string.error))
                }
            }

        )
    }
}

/**
 * Stateless composable with the shape of a piece of information, with the nullable icon, title, and
 * optional composable to the right and on the bottom
 *
 * @param modifier Modifier with the padding and align
 * @param title String to show in bold
 * @param icon Icon to visually represent the title
 * @param titleToRight optional composable on the right of the title
 * @param content Composable to show under the DetailView Composable as its content
 */
@Composable
fun DetailView(
    modifier: Modifier = Modifier,
    title: String,
    icon: VectorAsset? = null,
    titleToRight: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {

    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(bottom = 6.dp),
            //verticalAlignment = Alignment.CenterVertically
            verticalAlignment = Alignment.Bottom
        ) {
            if (icon != null)
                Icon(icon)

            Spacer(modifier = Modifier.padding(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(6.dp))
            titleToRight()
        }
        Row(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            content()
        }
    }
}
