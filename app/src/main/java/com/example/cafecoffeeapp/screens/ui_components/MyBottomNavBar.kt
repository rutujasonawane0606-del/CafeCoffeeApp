package com.example.cafecoffeeapp.screens.ui_components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coffeeapp.R
import com.example.coffeeapp.ui.theme.LightBrown


@Preview(showBackground = true)
@Composable
fun MyBottomNavBar() {

    val navItem = listOf(
        NavItem("Home", R.drawable.regular_outline_home),
        NavItem("Cart", R.drawable.regular_outline_bag),
        NavItem("Favourites", R.drawable.regular_outline_heart),
        NavItem("Profile", R.drawable.outline_account_circle_24),
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.height(90.dp)
    )
    {
        navItem.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                modifier = Modifier.size(25.dp),
                onClick = { },
                selected = true,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LightBrown,
                    selectedTextColor = LightBrown,
                    unselectedIconColor = Color.DarkGray,
                    unselectedTextColor = Color.DarkGray,
                    indicatorColor =  LightBrown.copy(alpha = 0.03f)
                )
            )
        }

    }

}

data class NavItem(
    val title: String,
    val icon: Int
)