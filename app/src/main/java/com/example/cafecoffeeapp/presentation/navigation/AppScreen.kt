package com.example.cafecoffeeapp.presentation.navigation

sealed class AppScreen {
    object Splash : AppScreen()
    object Home : AppScreen()
    object Details : AppScreen()
    object Cart : AppScreen()
    object Favorites : AppScreen()
    object Checkout : AppScreen()
    object OrderHistory : AppScreen()
}
