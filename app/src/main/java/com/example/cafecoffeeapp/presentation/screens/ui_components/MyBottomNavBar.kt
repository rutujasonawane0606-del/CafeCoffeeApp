package com.example.cafecoffeeapp.presentation.screens.ui_components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cafecoffeeapp.R
import com.example.cafecoffeeapp.presentation.navigation.AppScreen
import com.example.cafecoffeeapp.ui.theme.LightBrown

data class BottomBarItem(
    val title: String,
    val icon: Int,
    val screen: AppScreen,
    val badgeCount: Int = 0
)

@Composable
fun MyBottomNavBar(
    currentScreen: AppScreen = AppScreen.Home,
    cartCount: Int = 0,
    favoritesCount: Int = 0,
    onTabSelected: (AppScreen) -> Unit = {}
) {
    val navItems = listOf(
        BottomBarItem("Home", R.drawable.regular_outline_home, AppScreen.Home),
        BottomBarItem("Cart", R.drawable.regular_outline_bag, AppScreen.Cart, badgeCount = cartCount),
        BottomBarItem("Favourites", R.drawable.regular_outline_heart, AppScreen.Favorites, badgeCount = favoritesCount),
        BottomBarItem("Orders", R.drawable.outline_account_circle_24, AppScreen.OrderHistory)
    )

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            windowInsets = NavigationBarDefaults.windowInsets,
            modifier = Modifier.fillMaxWidth()
        ) {
            navItems.forEach { item ->
                val isSelected = (currentScreen == item.screen)
                NavigationBarItem(
                    icon = {
                        if (item.badgeCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = LightBrown,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = "${item.badgeCount}",
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(item.icon),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 12.sp
                        )
                    },
                    selected = isSelected,
                    onClick = { onTabSelected(item.screen) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LightBrown,
                        selectedTextColor = LightBrown,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = LightBrown.copy(alpha = 0.12f)
                    )
                )
            }
        }
    }
}
