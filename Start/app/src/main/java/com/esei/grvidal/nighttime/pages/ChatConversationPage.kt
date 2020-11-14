package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.esei.grvidal.nighttime.BottomNavigationScreens
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.FullChat
import com.esei.grvidal.nighttime.data.User

@Composable
fun ChatConversationPage(navController: NavHostController, chatId :Int?){
    val user = User("me")


    if (chatId == null) {
        errorComposable(errorText = stringResource(id = R.string.errorChatId))
    } else {
        val fullChat = user.getChatConversation(chatId)
        val onBackClick = {
            navController.popBackStack(navController.graph.startDestination, false)
            navController.navigate(BottomNavigationScreens.Profile.route)
        }
        ShowConversation(fullChat.otherUserName,onBackClick ){

            LazyColumnFor(
                items = fullChat.conversation
            ) {
                BubbleChat(it.idUser == user.id, it.messageText)
            }
        }
    }
}

@Composable
fun BubbleChat(
    isUser : Boolean,
    text : String
){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalArrangement = if(isUser) Arrangement.End
        else Arrangement.Start
    ){
        Surface(
            border = BorderStroke(1.dp, MaterialTheme.colors.primary),
            shape = RoundedCornerShape(25),
        ) {
            Text(
                modifier = Modifier.padding(6.dp),
                text = text)
        }
    }


}

@Composable
fun ShowConversation(
    userName :String,
    onBackCick : () -> Unit,
    content : @Composable () -> Unit = {}
){
    Column{
        Row(){
            IconButton(
                onClick = onBackCick) {
                Icon(asset = Icons.Default.ArrowBack)
            }
            Header(text = userName,
                border = BorderStroke(0.dp, Color.Transparent))
        }

        Divider()
        content()
    }
}











