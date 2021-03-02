package com.esei.grvidal.nighttime.pages

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.esei.grvidal.nighttime.scaffold.BottomNavigationScreens
import com.esei.grvidal.nighttime.R

/**
 * Check of if the BarId is null, this could be by a problem in navHostController
 *
 * @param barId expected Int identification of the bar to show
 * @param navController navigator with the queue of destinies and it will be used to go back
 */
@Composable
fun BarDetails(barId: Int?, navController: NavHostController) {
    //Null Check
    if (barId == null) {
        ErrorComposable(errorText = stringResource(id = R.string.errorBarId))
    } else {
        //Accesses to the database to get the information of the bar through its ID
        ShowDetails(bar = BarDAO().bares[barId],
            onBackPressed = {
                navController.popBackStack(navController.graph.startDestination, false)
                navController.navigate(BottomNavigationScreens.Bar.route)
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
    Text(
        text = errorText,
        color = MaterialTheme.colors.error
    )
}

/**
 * StateFull composable that manage the main composition of the BarDetails view
 *
 * @param bar BarClass that holds all the information that will be shown
 * @param onBackPressed action to be done when the icon with arrow back is pressed
 */
@Composable
fun ShowDetails(bar: Bar, onBackPressed: () -> Unit = {}) {

    val (showAlert, setShowAlert) = remember { mutableStateOf(false) }
    val (selectedImage, setSelectedImage) = remember { mutableStateOf<VectorAsset?>(null) }

    Column {
        //Button with an icon of an arrow back, if pushed it will show the previous View
        IconButton(onClick = onBackPressed) {
            Icon(asset = Icons.Default.ArrowBack)
        }
        //Header of the page with the title
        Header(
            modifier = Modifier.padding(bottom = 12.dp),
            text = bar.name,
            style = MaterialTheme.typography.h4
        )

        //Column with an horizontal padding
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
        ) {

            // Description of the bar
            DetailView(
                title = stringResource(R.string.descripcion)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    text = bar.description,
                    style = MaterialTheme.typography.body1
                )
            }
            //Schedule of the bar
            DetailView(title = stringResource(R.string.horario), icon = Icons.Outlined.Alarm,
                titleToRight = {
                    Text(text = bar.time)
                }
            ) {
                WeekSchedule(bar.schedule)
            }

            //Context to call google Maps
            val context = ContextAmbient.current
            val moveToMaps = {
                val uri: String = "http://maps.google.co.in/maps?q=" + bar.address
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                context.startActivity(intent)
            }
            //Localization of the bar
            DetailView(title = stringResource(R.string.localizacion), icon = Icons.Outlined.Place,
                titleToRight = {
                    //Button that triggers Google Maps
                    Button(onClick = moveToMaps) {
                        Text(
                            text = stringResource(id = R.string.openMaps),
                            style = MaterialTheme.typography.body2
                        )
                    }

                }
            ) {
                Text(bar.address)
            }

            //Text Multimedia with the Icon
            DetailView(
                title = stringResource(id = R.string.mutlimedia),
                icon = Icons.Outlined.PhotoLibrary
            )

        }//End Column with horizontal padding

        //If multimedia is not null, it will be shown
        bar.multimedia?.let {
            //Lazy column with no horizontal padding
            LazyRowFor(
                items = it,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.Start)
            ) { image ->
                Image(
                    image as VectorAsset,
                    modifier = Modifier.padding(2.dp).preferredSize(120.dp)
                        .background(Color.Gray)
                        .clickable(onClick = {
                            setSelectedImage(image )
                            setShowAlert(true)
                        })
                )
            }
            /*
            MultimediaView(
                photos = it, onImageClick = {
                    setShowAlert(true)
                },
                setSelectedImage = setSelectedImage
            )

             */
        }
        //Alert ready to be called
        BigPicture(showAlert, selectedImage,
            dismissAlert = {
                setShowAlert(false)
                setSelectedImage(null)
            }
        )

        //Events of the bar
        DetailView(
            modifier = Modifier.padding(horizontal = 12.dp),
            title = stringResource(id = R.string.nextEvents),
            icon = Icons.Outlined.LocalDrink
        ) {
            Event(
                title = "date",
                description = "eventData.description"
            )
            /*
            bar.events?.let {

                LazyColumnFor(items = it) { eventData ->
                    Event(
                        barName = eventData.date.toStringFormatted(),
                        eventData.description
                    )
                }
            }

             */
        }


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
    selectedImage: VectorAsset?,
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
            .padding(vertical = 12.dp)
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

@Deprecated("DetailView with a LazyRow is actually used" , ReplaceWith("DetailView"))
@Composable
fun MultimediaView(
    photos: List<Any>,
    setSelectedImage: (VectorAsset) -> Unit,
    onImageClick: () -> Unit
) {

    val photosChunked = photos.chunked(3)//TODO Size the pictures
    LazyColumnFor(
        modifier = Modifier.fillMaxSize(),
        items = photosChunked,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            it.forEach { vector ->
                Image(
                    vector as VectorAsset,
                    modifier = Modifier.padding(2.dp).preferredSize(120.dp)
                        .background(Color.Gray)
                        .clickable(onClick = {
                            setSelectedImage( vector )
                            onImageClick()
                        })
                )
            }

        }


    }
}
