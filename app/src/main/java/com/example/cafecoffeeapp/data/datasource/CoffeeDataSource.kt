package com.example.cafecoffeeapp.data.datasource

import com.example.cafecoffeeapp.R
import com.example.cafecoffeeapp.domain.model.Product

object CoffeeDataSource {

    val defaultCategories = listOf(
        "All Coffees",
        "Macchiato",
        "Latte",
        "Americano",
        "Cappuccino",
        "Snacks",
        "Desserts"
    )

    val defaultProducts = listOf(
        Product(
            id = 1,
            name = "Caffe Macchiato",
            category = "Macchiato",
            description = "Bold espresso with a dot of foamed milk",
            detailedDescription = "A rich, full-bodied espresso marked with a dollop of steamed milk and airy foam. Crafted with freshly roasted 100% Arabica beans, offering an intense flavor with subtle caramel undertones.",
            price = 380.0,
            imageRes = R.drawable.coffee_5,
            rating = 4.9,
            reviewsCount = 312
        ),
        Product(
            id = 2,
            name = "Caramel Latte",
            category = "Latte",
            description = "Smooth espresso with creamy milk & caramel",
            detailedDescription = "Velvety steamed milk poured over a shot of dark roasted espresso, finished with artisanal vanilla syrup and a drizzle of buttery caramel. Perfectly balanced sweetness for any time of day.",
            price = 450.0,
            imageRes = R.drawable.coffee_2,
            rating = 4.8,
            reviewsCount = 425
        ),
        Product(
            id = 3,
            name = "Classic Cappuccino",
            category = "Cappuccino",
            description = "With chocolate dusting & dense foam",
            detailedDescription = "Dark, rich espresso lies in wait under a smoothed and stretched layer of thick milk foam. Topped with Belgian cocoa dust for an authentic European cafe experience.",
            price = 420.0,
            imageRes = R.drawable.coffee_3,
            rating = 4.7,
            reviewsCount = 280
        ),
        Product(
            id = 4,
            name = "Caffe Americano",
            category = "Americano",
            description = "Espresso shots topped with hot water",
            detailedDescription = "Espresso shots topped with hot water create a light layer of crema with nuanced depth and strength. Simple, crisp, and awakening.",
            price = 340.0,
            imageRes = R.drawable.coffee_1,
            rating = 4.6,
            reviewsCount = 190
        ),
        Product(
            id = 5,
            name = "Mocha Frappé",
            category = "Latte",
            description = "Chilled mocha with whipped cream",
            detailedDescription = "Decadent Dutch chocolate sauce blended with bold espresso, whole milk, and ice. Garnished with fluffy whipped cream and chocolate curls.",
            price = 470.0,
            imageRes = R.drawable.coffee_4,
            rating = 4.9,
            reviewsCount = 510
        ),
        Product(
            id = 6,
            name = "Flat White",
            category = "Macchiato",
            description = "Velvety microfoam over double ristretto",
            detailedDescription = "Expertly steamed micro-foamed whole milk gently poured through a double ristretto shot. A strong, creamy, and velvety cup with intense coffee character.",
            price = 440.0,
            imageRes = R.drawable.coffee_6,
            rating = 4.8,
            reviewsCount = 215
        ),
        Product(
            id = 7,
            name = "Butter Croissant",
            category = "Snacks",
            description = "Flaky, golden French pastry",
            detailedDescription = "Freshly baked artisan croissant with crispy golden layers, rich European butter flavor, and a fluffy, melt-in-your-mouth interior. The quintessential coffee companion.",
            price = 220.0,
            imageRes = R.drawable.coffee,
            rating = 4.7,
            reviewsCount = 140
        ),
        Product(
            id = 8,
            name = "Tiramisu Delight",
            category = "Desserts",
            description = "Classic Italian coffee dessert",
            detailedDescription = "Layers of espresso-soaked Savoiardi ladyfingers and whipped mascarpone cream, dusted generously with premium Valrhona dark cocoa powder.",
            price = 320.0,
            imageRes = R.drawable.coffee_4,
            rating = 5.0,
            reviewsCount = 380
        )
    )
}
