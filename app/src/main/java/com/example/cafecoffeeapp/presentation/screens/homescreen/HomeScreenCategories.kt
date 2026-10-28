package com.example.cafecoffeeapp.presentation.screens.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenCategories(
    categories: List<String> = listOf(
        "All Coffees",
        "Macchiato",
        "Latte",
        "Americano",
        "Cappuccino",
        "Snacks",
        "Desserts"
    ),
    selectedCategory: String = "All Coffees",
    onCategorySelected: (String) -> Unit = {}
) {
    LazyRow(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { category ->
            CategoryChip(
                text = category,
                isSelected = category.equals(selectedCategory, ignoreCase = true),
                onSelected = {
                    onCategorySelected(category)
                }
            )
        }
    }
}
