package com.example.cafecoffeeapp.screens.homescreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cafecoffeeapp.model.Product
import com.example.cafecoffeeapp.screens.ProductCard
import androidx.compose.foundation.lazy.items
@Composable
fun ProductsGrid(
    products: List<Product>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(products.chunked(2)) { rowItems ->

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                // First product
                ProductCard(
                    product = rowItems[0],
                    modifier = Modifier.weight(1f)
                )

                // Second product
                if (rowItems.size == 2) {

                    ProductCard(
                        product = rowItems[1],   // ✅ second product
                        modifier = Modifier.weight(1f)
                    )

                } else {

                    // If only one product is left
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
//
//
//fun ProductsGrid(products: List<Product>) {
//
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(8.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(products) { product ->
//
//            ProductCard(
//                product = product
//            )
//        }
//    }
//}