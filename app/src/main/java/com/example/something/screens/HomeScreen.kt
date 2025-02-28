package com.example.something.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.something.R
import com.example.something.entity.Payment
import com.example.something.navigation.BottomNavigationBar
import com.example.something.viewmodel.PaymentViewModel
import kotlinx.serialization.json.JsonNull.content
import java.util.Calendar
import kotlin.math.abs




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: PaymentViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedFilter by remember { mutableStateOf("Today") }


    val currPayment by viewModel.currPayment

    // Keep your beautiful gradient
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Scaffold(
            modifier = Modifier.background(Color.Transparent),
            containerColor = Color.Transparent,
            topBar = { TopBar() },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screens.Payment.route) },
                    containerColor = Color(0xFF2575FC),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Payment")
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Greeting with time-based message
                    GreetingSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Enhanced card section with visual spending overview
                    EnhancedCardSection(viewModel)

//                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick action buttons
//                    QuickActionSection(navController)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recent activity section with filter tabs
                    RecentActivitySection(viewModel, selectedFilter) {
                        selectedFilter = it
                    }
                }
            }
        )
    }
}

@Composable
fun GreetingSection() {
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when {
        currentHour < 12 -> "Good Morning"
        currentHour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Column {
        Text(
            text = greeting,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = "John Doe", // Replace with actual user name
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun EnhancedCardSection(viewModel: PaymentViewModel) {

    val currPayment by viewModel.currPayment

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Total Balance",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                "₹${currPayment}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Spending overview with visual graph
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Simple spending visualization
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                ) {
                    // Income percentage (60%)
                    drawRect(
                        color = Color(0xFF4CAF50),
                        size = Size(size.width * 0.6f, 16.dp.toPx())
                    )

                    // Expense percentage (40%)
                    drawRect(
                        color = Color(0xFFF44336),
                        topLeft = Offset(size.width * 0.6f, 0f),
                        size = Size(size.width * 0.4f, 16.dp.toPx())
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Legend
                Column(
                    modifier = Modifier.width(100.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Income", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFFF44336), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Expense", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

//@Composable
//fun QuickActionSection(navController: NavController) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceEvenly
//    ) {
//        QuickActionButton(
//            icon = Icons.Default.Add,
//            label = "Add",
//            onClick = { navController.navigate(Screens.Payment.route) }
//        )
//
//        QuickActionButton(
//            icon = Icons.Default.BarChart,
//            label = "Analytics",
//            onClick = { /* Navigate to analytics */ }
//        )
//
//        QuickActionButton(
//            icon = Icons.Default.AccountBalanceWallet,
//            label = "Budget",
//            onClick = { /* Navigate to budget */ }
//        )
//
//        QuickActionButton(
//            icon = Icons.Default.Settings,
//            label = "Settings",
//            onClick = { /* Navigate to settings */ }
//        )
//    }
//}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentActivitySection(
    viewModel: PaymentViewModel,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val payments by viewModel.getAllPaymentsInRange(selectedFilter).collectAsState(initial = emptyList())

    Column {
        Text(
            "Recent Activity",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Filter chips
        ScrollableChipGroup(selectedFilter, onFilterSelected)

        Spacer(modifier = Modifier.height(8.dp))

        // Activity list with enhanced visuals
        if (payments.isEmpty()) {
            EmptyStateMessage()
        } else {
            EnhancedTransactionList(payments)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableChipGroup(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        listOf("Today", "Week", "Month", "Year", "All").forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2575FC),
                    selectedLabelColor = Color.White,
                    containerColor = Color.White.copy(alpha = 0.2f),
                    labelColor = Color.White
                ),
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "No transactions",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "No transactions yet",
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Add your first transaction using the + button",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EnhancedTransactionList(payments: List<Payment>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        items(payments) { payment ->
            EnhancedPaymentItem(payment)
        }
    }
}

@Composable
fun EnhancedPaymentItem(payment: Payment) {
    val isExpense = payment.amount < 0
    val amountColor = if (isExpense) Color.Red else Color(0xFF4CAF50)
    val amountPrefix = if (isExpense) "-₹" else "+₹"
    val displayAmount = abs(payment.amount)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Category icon in a colored circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9))
                ) {
                    Icon(
                        painter = painterResource(
                            if (isExpense) R.drawable.pic
                            else R.drawable.k1
                        ),
                        contentDescription = null,
                        tint = if (isExpense) Color.Red else Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = payment.sender,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    Text(
                        text = "${payment.date.dayOfMonth} ${payment.date.month} ${payment.date.year}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = "$amountPrefix$displayAmount",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC), Color(0xFF2196F3))
    )
    TopAppBar(
        {
            Text("Home", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, actionIconContentColor = Color.White),
        actions = {
            Image(
                painter = painterResource(R.drawable.k1),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
        },
        modifier = Modifier.background(Color.Transparent)
    )
}
