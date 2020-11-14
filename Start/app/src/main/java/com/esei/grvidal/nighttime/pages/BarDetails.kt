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
import com.esei.grvidal.nighttime.BottomNavigationScreens
import com.esei.grvidal.nighttime.R


@Composable
fun BarDetails(barId: Int?, navController: NavHostController) {
    if (barId == null) {
        errorComposable(errorText = stringResource(id = R.string.errorBarId))
    } else {
        ShowDetails(BarDAO().bares[barId], navController)
    }
}

@Composable
fun errorComposable(errorText : String) {
    Text(
        text = errorText,
        color = MaterialTheme.colors.error
    )
}

@Composable
fun ShowDetails(bar: Bar, navController: NavHostController) {

    val (showAlert, setShowAlert) = remember { mutableStateOf(false) }
    val (selectedImage, setSelectedImage) = remember { mutableStateOf<VectorAsset?>(null) }

    Column {
        IconButton(
            onClick = {
                navController.popBackStack(navController.graph.startDestination, false)
                navController.navigate(BottomNavigationScreens.Bar.route)
            }) {
            Icon(asset = Icons.Default.ArrowBack)
        }
        Header(
            text = bar.name,
            style = MaterialTheme.typography.h4
        )

        Column(
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 12.dp)
        ) {

            // Description
            DetailView(
                title = stringResource(R.string.descripcion)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    text = bar.description,
                    style = MaterialTheme.typography.body1
                )
            }
            //Schedule
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
            //Localization
            DetailView(title = stringResource(R.string.localizacion), icon = Icons.Outlined.Place,
                titleToRight = {
                    //Button that trigers Google Maps
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

        }//End Column with padding

        //If multimedia is not null, it will be shown
        bar.multimedia?.let {
            LazyRowFor(
                items = it,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.Start)
            ) {image ->
                    Image(
                        image as VectorAsset,
                        modifier = Modifier.padding(2.dp).preferredSize(120.dp)
                            .background(Color.Gray)
                            .clickable(onClick = {
                                setSelectedImage(image as VectorAsset)
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
        BigPicture(showAlert, setShowAlert, selectedImage, setSelectedImage)

        DetailView(
            modifier = Modifier.padding(horizontal = 12.dp),
            title = "Proximos eventos",
            icon = Icons.Outlined.LocalDrink){
            bar.events?.let{

                LazyColumnFor(items = it) {eventData ->
                    Event(
                        barName = eventData.fecha.toStringFormatted(),
                        eventData.description
                    )
                }
            }
        }




    }

}

@Composable
private fun BigPicture(
    showAlert: Boolean,
    setShowAlert: (Boolean) -> Unit,
    selectedImage: VectorAsset?,
    setSelectedImage: (VectorAsset?) -> Unit
) {
    val dismissAlert = {
        setShowAlert(false)
        setSelectedImage(null)
    }
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


@Composable
fun DetailView(
    modifier :Modifier = Modifier,
    title: String,
    icon: VectorAsset? = null,
    titleToRight: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    //Description Row
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
                            setSelectedImage(vector as VectorAsset)
                            onImageClick()
                        })
                )
            }

        }

    }
}
