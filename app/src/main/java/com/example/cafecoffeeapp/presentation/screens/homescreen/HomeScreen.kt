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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cafecoffeeapp.R
import com.example.cafecoffeeapp.presentation.viewmodel.CoffeeViewModel

@Composable
fun HomeScreen(
    viewModel: CoffeeViewModel
) {
    val location = "Bibwewadi, Pune"
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val promoBanner by viewModel.promoBanner.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Dark gradient top header background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.32f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E1E1E),
                            Color(0xFF2C2C2C),
                            Color(0xFF383838)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // FIXED HEADER (Does NOT move/scroll)
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Location",
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = location,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Change location",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fixed Search Bar
            MySearchBar(
                query = searchQuery,
                onQueryChange = { query ->
                    viewModel.onSearchQueryChange(query)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // DYNAMIC PROMO BANNER (Synced from Firebase Firestore, permanently pinned!)
            PromoBannerView(
                banner = promoBanner,
                onBannerClick = { banner ->
                    if (banner.promoCode.isNotBlank()) {
                        // User clicked banner
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Fixed Category Chips
            HomeScreenCategories(
                categories = viewModel.categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    viewModel.onCategorySelected(category)
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // SCROLLABLE PRODUCTS (Only products scroll underneath the banner!)
            ProductsGrid(
                products = products,
                modifier = Modifier.weight(1f),
                onProductClick = { product ->
                    viewModel.onProductSelected(product)
                },
                onAddToCart = { product ->
                    viewModel.addToCart(product)
                },
                onToggleFavorite = { product ->
                    viewModel.toggleFavorite(product)
                },
                onResetFilter = {
                    viewModel.onSearchQueryChange("")
                    viewModel.onCategorySelected("All Coffees")
                }
            )
        }
    }
}
