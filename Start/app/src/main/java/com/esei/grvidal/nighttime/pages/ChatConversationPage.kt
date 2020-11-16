package com.esei.grvidal.nighttime.pages

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import androidx.ui.tooling.preview.Preview
import com.esei.grvidal.nighttime.BottomNavigationScreens
import com.esei.grvidal.nighttime.R
import com.esei.grvidal.nighttime.data.FullChat
import com.esei.grvidal.nighttime.data.Message
import com.esei.grvidal.nighttime.data.User
import com.esei.grvidal.nighttime.ui.NightTimeTheme

/**
 * Composable that cheks if [chatId] is null, if its false it will show the conversation
 *
 * @param navController controller of hte navigation, its used to go back or navigate to other views
 * @param chatId Id of the current char
 */
@Composable
fun ChatConversationPage(navController: NavHostController, chatId :Int?){
    val user = User("me")

    //Nullable check
    if (chatId == null) {
        errorComposable(errorText = stringResource(id = R.string.errorChatId))
    } else {
        val fullChat = user.getChatConversation(chatId)
        val onBackClick = {
            navController.popBackStack(navController.graph.startDestination, false)
            navController.navigate(BottomNavigationScreens.Friends.route)
        }
        ShowConversation(fullChat.otherUserName,onBackClick ){
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ){
                Row(
                    modifier = Modifier.weight(2.5f)
                ) {
                    LazyColumnFor(
                        items = fullChat.initialConversation,

                        ) {
                        BubbleChat(it.idUser == user.id, it.messageText)
                    }
                }
                MessageInput(modifier = Modifier){
                    Text("Aqui se escribe")
                }
            }


        }
    }
}

/**
 * Structure of the View with an arrow back [onBackClick], a title [userName] and the content [content]
 *
 * @param userName name of the other user
 * @param onBackClick action to be done when the arrow back is pressed
 * @param content content of the layour
 */
@Composable
fun ShowConversation(
    userName :String,
    onBackClick : () -> Unit,
    content : @Composable () -> Unit = {}
){
    Column{
        Row(){
            IconButton(
                onClick = onBackClick) {
                Icon(asset = Icons.Default.ArrowBack)
            }
            Header(text = userName,
                border = BorderStroke(0.dp, Color.Transparent))
        }

        Divider()
        content()
    }
}

/**
 * Composable of a chat message
 *
 * @param isUser boolean to show the message on the right if the app user sent it
 * @param text Text of the message
 */
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
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                modifier = Modifier.padding(6.dp),
                text = text)
        }
    }
}

@Composable
fun MessageInput(
    modifier :Modifier = Modifier,
    content: @Composable() () -> Unit = {}
){
    Divider()
    Row(modifier = modifier){
       Surface(
           modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
               .padding(horizontal = 12.dp),
           shape = RoundedCornerShape(10.dp),
           border = BorderStroke(1.dp, AmbientContentColor.current.copy(0.2f))
       ){
           Box(
               modifier = Modifier.padding(4.dp)
                   .align(Alignment.CenterVertically)
                   .fillMaxWidth()
           ){
               content()
           }

       }
    }
    Divider()
}

@Preview("Bubble chat")
@Composable
fun BubbleChatPreview(){
    NightTimeTheme {
        BubbleChat(isUser = false, text = "Mensaje de prueba")
    }
}

@Preview("Message input")
@Composable
fun MessageInputPreview(){
    NightTimeTheme {
        MessageInput(){
            Text("Mensaje de prueba")
        }
    }
}

@Preview("Chat input")
@Composable
fun ChatPreviw(){
    val previewChat = FullChat(0, "Nuria Sotelo Domarco", listOf(
        Message(0,"hey que tal?","8:04 PM"),
        Message(1,"Bien, llegando a casa y tu?","8:04 PM"),
        Message(0,"Acabando de trabjar","8:04 PM"),
        Message(0,"Te apetece hacer algo hoy?","8:04 PM"),
        Message(1,"Dicen que hoy abre el Lokal","8:04 PM")
    ))


    ShowConversation(previewChat.otherUserName, { }){
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ){
            Row(
                modifier = Modifier.weight(1.8f)
            ){
                LazyColumnFor(
                    items = previewChat.initialConversation,

                    ) {
                    BubbleChat(it.idUser == 0 , it.messageText)
                }
            }

            MessageInput(modifier = Modifier.weight(0.2f)){
                Text("Aqui se escribe")
            }
        }


    }
}









