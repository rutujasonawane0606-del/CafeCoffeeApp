package com.example.cafecoffeeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cafecoffeeapp.presentation.screens.homescreen.HomeScreen
import com.example.cafecoffeeapp.ui.theme.CafeCoffeeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CafeCoffeeAppTheme {
                HomeScreen()
            }
        }
    }
}
