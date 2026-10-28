package com.example.cafecoffeeapp.presentation.screens.checkoutscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cafecoffeeapp.R
import com.example.cafecoffeeapp.presentation.navigation.AppScreen
import com.example.cafecoffeeapp.presentation.viewmodel.CoffeeViewModel
import com.example.cafecoffeeapp.ui.theme.Ivorywhite
import com.example.cafecoffeeapp.ui.theme.LightBrown

@Composable
fun CheckoutScreen(
    viewModel: CoffeeViewModel
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsState()

    var deliveryAddress by remember {
        mutableStateOf("Flat 402, Green Acre Heights, Bibwewadi, Pune - 411037")
    }
    var showAddressDialog by remember { mutableStateOf(false) }
    var tempAddress by remember { mutableStateOf(deliveryAddress) }

    var selectedPayment by remember { mutableStateOf("UPI / Mobile Banking") }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val deliveryFee = if (subtotal > 500.0 || cartItems.isEmpty()) 0.0 else 30.0
    val taxes = if (cartItems.isEmpty()) 0.0 else Math.round(subtotal * 0.05 * 100.0) / 100.0
    val total = subtotal + deliveryFee + taxes

    // Success Confirmation Dialog
    if (lastPlacedOrder != null) {
        val order = lastPlacedOrder!!
        AlertDialog(
            onDismissRequest = {
                viewModel.clearLastPlacedOrder()
                viewModel.navigateTo(AppScreen.Home)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(60.dp)
                )
            },
            title = {
                Text(
                    text = "Order Placed Successfully!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Order ID: ${order.orderId}",
                        fontWeight = FontWeight.SemiBold,
                        color = LightBrown,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your fresh brews are being prepared with love!\nEstimated delivery: 20-25 mins",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Total Paid: ₹${order.totalAmount.toInt()} via ${order.paymentMethod}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearLastPlacedOrder()
                        viewModel.navigateTo(AppScreen.OrderHistory)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LightBrown)
                ) {
                    Text("View Order History")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.clearLastPlacedOrder()
                        viewModel.navigateTo(AppScreen.Home)
                    }
                ) {
                    Text("Back to Home", color = Color.DarkGray)
                }
            }
        )
    }

    // Edit Address Dialog
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Edit Delivery Address") },
            text = {
                OutlinedTextField(
                    value = tempAddress,
                    onValueChange = { tempAddress = it },
                    label = { Text("Full Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        deliveryAddress = tempAddress
                        showAddressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LightBrown)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Button(
                    onClick = {
                        if (cartItems.isNotEmpty()) {
                            viewModel.placeOrder(deliveryAddress, selectedPayment)
                        }
                    },
                    enabled = cartItems.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightBrown),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Place Order • ₹${total.toInt()}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Ivorywhite)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.Cart) },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                Text(
                    text = "Checkout",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Box(modifier = Modifier.size(44.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Delivery Address Card
            Text(
                text = "Delivery Address",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(LightBrown.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = LightBrown,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Deliver to Home",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = deliveryAddress,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            tempAddress = deliveryAddress
                            showAddressDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Address",
                            tint = LightBrown,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment Methods Card
            Text(
                text = "Payment Method",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    val paymentOptions = listOf(
                        Triple("UPI / Mobile Banking", R.drawable.mobile_banking, "GPay, PhonePe, Paytm"),
                        Triple("Cash on Delivery", R.drawable.wallet, "Pay cash when delivered"),
                        Triple("Online Wallet / Cards", R.drawable.wallet, "Credit/Debit Cards, NetBanking")
                    )

                    paymentOptions.forEachIndexed { index, (name, iconRes, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPayment = name }
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = name,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = desc,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            RadioButton(
                                selected = (selectedPayment == name),
                                onClick = { selectedPayment = name },
                                colors = RadioButtonDefaults.colors(selectedColor = LightBrown)
                            )
                        }
                        if (index < paymentOptions.size - 1) {
                            HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Order Summary Preview
            Text(
                text = "Order Items (${cartItems.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.name} (${item.size})",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "₹${item.totalPrice.toInt()}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFEFEFEF))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", color = Color.Gray, fontSize = 13.sp)
                        Text("₹${subtotal.toInt()}", fontSize = 13.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Fee", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
                            color = if (deliveryFee == 0.0) Color(0xFF2E7D32) else Color.DarkGray,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("GST (5%)", color = Color.Gray, fontSize = 13.sp)
                        Text("₹${taxes.toInt()}", fontSize = 13.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFEFEFEF))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("₹${total.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = LightBrown)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
