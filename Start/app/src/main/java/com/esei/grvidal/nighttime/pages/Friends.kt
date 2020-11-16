package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esei.grvidal.nighttime.NavigationScreens
import com.esei.grvidal.nighttime.data.User
import androidx.navigation.NavHostController
import com.esei.grvidal.nighttime.navigateWithId

/**
 * Composable that manages the View of the list of chats
 *
 * @param navController  controller of hte navigation, its used to go back or navigate to other views
 */
@Composable
fun FriendsPageView(navController: NavHostController) {


    Column{
        val user = User("me")
        Header(text = "Chat" ,
        modifier = Modifier.padding( top = 24.dp))

        LazyColumnFor(items = user.getChats()) {

            ChatEntry(userName = it.userName, lastMessage = it.lastMessage,
                onEntryClick = { navController.navigateWithId( NavigationScreens.ChatConversation.route, it.id )
                } )
        }
    }
}



/**
 * Composable that shows the information about the chat
 */
@Composable
fun ChatEntry(
    userName : String,
    lastMessage : String,
    onEntryClick : () -> Unit = {}
    ){
    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp)
            .clickable(onClick = onEntryClick)
    ){
        Row(
            modifier = Modifier
                .padding(vertical = 6.dp)
        ){
            Surface(shape= RoundedCornerShape(50),
                modifier = Modifier.preferredSize(45.dp).align(Alignment.CenterVertically),
                color = Color.Gray){

            }
            Column(
                modifier = Modifier.padding(start = 6.dp)
            ){
                Text(
                    text = userName,
                    style= MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold
                )
                Text(text = lastMessage, style= MaterialTheme.typography.body2, maxLines = 1 )
            }
        }
        Divider(startIndent = 25.dp)
    }

}

