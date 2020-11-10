package com.esei.grvidal.nighttime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                 Row{
                     content()
                 }

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ){
                    //Close button
                    Button(
                        modifier = Modifier
                            .padding(top = 6.dp) ,
                        onClick = onClose
                    ) {
                        Text(text = stringResource(R.string.cerrar))
                    }
                }
            }
        }
    }


}

@Composable
fun searchBar(){
    //var text = remember{ mutableStateOf("") }
    //TextField(value = text.value , onValueChange ={text.value = it })

}































