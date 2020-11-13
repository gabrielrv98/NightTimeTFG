package com.esei.grvidal.nighttime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Custom dialog with the formated shape
 *
 * @param onClose is the action that will be done when the dialog closes, usually, it will set
 * the variable showDialog to false
 * @param content content to show inside
 */
@Composable
fun CustomDialog(
    onClose : () -> Unit,
    dialogBorder :BorderStroke =  BorderStroke(3.dp, MaterialTheme.colors.primary),
    dialogShape : Shape = MaterialTheme.shapes.medium,
    content :@Composable () -> Unit
){

    Dialog(onDismissRequest = onClose) {
        //Surface with the shape, border and color
        Surface(
            modifier = Modifier
                .padding(24.dp)
                .border(
                    border = dialogBorder,
                    shape = dialogShape
                )
                .padding(1.dp),
            color = MaterialTheme.colors.background,
            shape = MaterialTheme.shapes.medium,
            elevation = 1.dp
        ) {
            Column{

                //searchBar()

                 Row(
                     modifier = Modifier.padding(horizontal = 20.dp)
                         .padding(top = 20.dp, bottom = 0.dp)
                 ) {
                     content()
                 }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ){
                    //Close button
                    Button(
                        modifier = Modifier
                            .padding(bottom = 18.dp, end = 24.dp, top = 12.dp) ,
                        onClick = onClose
                    ) {
                        Text(text = stringResource(R.string.cerrar))
                    }
                }
            }
        }
    }


}

//TODO ADD SEARCH BAR
/*
@Composable
fun searchBar(){
    val text = remember { mutableStateOf(TextFieldValue()) }
    OutlinedTextField(
        value = text.value,
        onValueChange = {
            text.value = it
        },
        label = { Text("Label") }
    )

}


 */






























