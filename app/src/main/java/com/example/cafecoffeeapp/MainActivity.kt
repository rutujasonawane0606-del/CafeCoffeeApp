package com.example.cafecoffeeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.cafecoffeeapp.presentation.navigation.AppScreen
import com.example.cafecoffeeapp.presentation.screens.cartscreen.CartScreen
import com.example.cafecoffeeapp.presentation.screens.checkoutscreen.CheckoutScreen
import com.example.cafecoffeeapp.presentation.screens.detailsscreen.ProductDetailScreen
import com.example.cafecoffeeapp.presentation.screens.favoritesscreen.FavoritesScreen
import com.example.cafecoffeeapp.presentation.screens.homescreen.HomeScreen
import com.example.cafecoffeeapp.presentation.screens.orderscreen.OrderHistoryScreen
import com.example.cafecoffeeapp.presentation.screens.ui_components.MyBottomNavBar
import com.example.cafecoffeeapp.presentation.screens.welcomescreen.WelcomeScreen
import com.example.cafecoffeeapp.presentation.viewmodel.CoffeeViewModel
import com.example.cafecoffeeapp.ui.theme.CafeCoffeeAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CoffeeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CafeCoffeeAppTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: CoffeeViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Show transient notification snackbar
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Determine whether to display the persistent bottom navigation bar
    val showBottomBar = currentScreen in listOf(
        AppScreen.Home,
        AppScreen.Cart,
        AppScreen.Favorites,
        AppScreen.OrderHistory
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                MyBottomNavBar(
                    currentScreen = currentScreen,
                    cartCount = cartItems.sumOf { it.quantity },
                    favoritesCount = favorites.size,
                    onTabSelected = { screen ->
                        viewModel.navigateTo(screen)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is AppScreen.Splash -> {
                    WelcomeScreen(onGetStarted = { viewModel.navigateTo(AppScreen.Home) })
                }
                is AppScreen.Home -> {
                    HomeScreen(viewModel = viewModel)
                }
                is AppScreen.Details -> {
                    selectedProduct?.let { product ->
                        ProductDetailScreen(viewModel = viewModel, product = product)
                    } ?: run {
                        HomeScreen(viewModel = viewModel)
                    }
                }
                is AppScreen.Cart -> {
                    CartScreen(viewModel = viewModel)
                }
                is AppScreen.Favorites -> {
                    FavoritesScreen(viewModel = viewModel)
                }
                is AppScreen.Checkout -> {
                    CheckoutScreen(viewModel = viewModel)
                }
                is AppScreen.OrderHistory -> {
                    OrderHistoryScreen(viewModel = viewModel)
                }
            }
        }
    }
}
