package com.example.app02.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.app02.ui.theme.bcl

@Composable
fun ConditionsScreen(navController: NavController){
    Surface(modifier = Modifier
    .fillMaxSize()
    .background(color = bcl)
    .padding(16.dp)) {
        BoldTextComponent("Điều khoản")
        BackHandler {
            navController.popBackStack()
        }
    }

}
@Preview
@Composable
fun ConditionsPreview(){
//    ConditionsScreen(navController = NavController())
}