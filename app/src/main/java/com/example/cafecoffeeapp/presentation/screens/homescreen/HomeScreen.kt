package com.example.cafecoffeeapp.presentation.screens.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cafecoffeeapp.R
import com.example.cafecoffeeapp.domain.model.Product
import com.example.cafecoffeeapp.presentation.screens.ui_components.MyBottomNavBar

@Preview(showBackground = true)
@Composable
fun HomeScreen() {

    val location = " Bibwewadi,Pune"
    Scaffold(
        bottomBar = {
            MyBottomNavBar()
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f / 3f)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF303030),
                            Color(0xFF1F1F1F),
                            Color(0xFF121212)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
        ) {

            //display products

            val products = listOf(
                Product(
                    id = 1,
                    name = "Espresso",
                    description = "Strong and rich",
                    price = 380.0,
                    imageRes = R.drawable.coffee_1
                ),
                Product(
                    id = 2,
                    name = "Latte",
                    description = "Smooth and creamy",
                    price = 450.0,
                    imageRes = R.drawable.coffee_2
                ),
                Product(
                    id = 3,
                    name = "Cappuccino",
                    description = "With chocolate",
                    price = 420.0,
                    imageRes = R.drawable.coffee_3
                ),
                Product(
                    id = 4,
                    name = "Mocha",
                    description = "With cocoa flavor",
                    price = 470.0,
                    imageRes = R.drawable.coffee_4
                ),
                Product(
                    id = 5,
                    name = "Macchiato",
                    description = "Bold and milky",
                    price = 460.0,
                    imageRes = R.drawable.coffee_5
                ),
                Product(
                    id = 6,
                    name = "Flat White",
                    description = "Velvety smooth",
                    price = 440.0,
                    imageRes = R.drawable.coffee_6
                ),
                Product(
                    id = 7,
                    name = "Iced Mocha",
                    description = "Refreshing and rich",
                    price = 470.0,
                    imageRes = R.drawable.coffee_4
                )
            )

            ProductsGrid(products = products) {
                Text(
                    text = "Location",
                    color = Color.Gray,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        location, color = Color.White, fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Change location",
                        tint = Color.White
                    )

                }
                Spacer(modifier = Modifier.height(16.dp))

                MySearchBar()

                Spacer(modifier = Modifier.height(20.dp))

                Image(

                    painter = painterResource(R.drawable.banner_1),
                    contentDescription = "banner"
                )

                Spacer(modifier = Modifier.height(8.dp))

                HomeScreenCategories()

            }
        }
    }
}