package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.City
import com.esei.grvidal.nighttime.ui.NightTimeTheme
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.esei.grvidal.nighttime.BottomNavigationScreens
import com.esei.grvidal.nighttime.NavigationScreens


class BarDAO{
    val bares : List<Bar> = listOf(
        Bar(0,"Lazaros", "Un pub para gente pija"),
        Bar(1,"Lokal","Un lokal para escuchar rock"),
        Bar(2,"Urbe","Las mejores aspiradoras"),
        Bar(3,"Patio Andaluz","Otro gran local pero con una descripcion algo larga de mas que se acortara"),
        Bar(4,"Mil petalos","Chicas siempre listas para darlo todo")
    )
}

data class Bar(val id: Int, val name: String, val description: String)

/**
 * Show the Bar page, //todo acabar
 */
@Composable
fun BarPageView(cityId : City, navController: NavController) {

    val barList = BarDAO().bares
    //val barList = BarDAO().getBares(cityId.id)//Futuro llamamiento
    Column{
        tryy(cityId = cityId, barList,navController)
        /*
        val navController2 = rememberNavController()
        NavHost(navController2, startDestination = BottomNavigationScreens.Bar.route){
            composable(BottomNavigationScreens.Bar.route) {
                tryy(cityId = cityId, barList,navController2)
            }

            composable(NavigationScreens.BarDetails.route){
                BarDetails()
            }
        }


         */

    }
}

@Composable
fun tryy(cityId : City, barList : List<Bar>, navController: NavController){
    Header(cityId.name)
    LazyColumnFor(
        items = barList,
        modifier = Modifier.fillMaxSize()
            .padding(top = 24.dp)
            .padding(horizontal = 24.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding( vertical = 3.dp)
        ){
            BarChip(it)
            Button(onClick = {navController.navigate(NavigationScreens.BarDetails.route)}) {
                Text("ver")
            }
        }

        Divider(startIndent = 30.dp, modifier = Modifier.padding(vertical = 3.dp))

    }
}
@Composable
fun Header(cityName :String){
    val text = stringResource(id = R.string.baresZona) + " $cityName"
    Divider()
    Row(
        modifier = Modifier
            .padding(6.dp)
    ){
        Text(
            modifier = Modifier
                .padding(start = 6.dp),
            text =  text,
            style = MaterialTheme.typography.h6
        )
    }
    Divider()
}


@Composable fun BarChip(bar : Bar){
    Column(
        modifier = Modifier.padding(8.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text( style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                text = bar.name)
            Spacer(Modifier.width(5.dp))
            daySchedule(day = "L"  )
            daySchedule(day = "M" )
            daySchedule(day = "Mi", enable = true)
            daySchedule(day = "J", enable = true)
            daySchedule(day = "V" )
            daySchedule(day = "S", enable = true)
            daySchedule(day = "D", enable = true)

            Text(
                modifier = Modifier.padding(start = 5.dp),
                color = Color.Gray,
                text= "21:00 - 5:00",
            style = MaterialTheme.typography.body2
            )
        }


        Text(
            modifier = Modifier.padding(start = 12.dp),
            style = MaterialTheme.typography.body2,
            text = bar.description)
    }
}

@Composable
fun daySchedule(
    day :String,
    enable :Boolean = false,
    border :BorderStroke = BorderStroke(1.dp,MaterialTheme.colors.primary),
    shape :Shape = RoundedCornerShape(50)
){

    Box(
        modifier = Modifier
            .padding(horizontal = 1.dp)
            .border(
                border = border,
                shape = shape
            )
            .preferredWidth(15.dp)
            .preferredHeight(15.dp)
            .clip(shape)
            .background( if(enable) Color.Green
                else MaterialTheme.colors.background)

            ,
        alignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(0.dp),
            style = MaterialTheme.typography.overline,
            text = day,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Preview ("BarPage")
@Composable
fun BarPreview(){
    NightTimeTheme {
        //BarPageView(City(0,"Ourense"))
    }
}