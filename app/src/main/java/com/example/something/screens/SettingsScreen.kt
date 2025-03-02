package com.example.something.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.something.navigation.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A11CB)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                item {
                    // Title Row with Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Settings",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFF6A11CB),
                                fontWeight = FontWeight.Bold
                            )

                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings Icon",
                                tint = Color(0xFF6A11CB),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // User Section
                    SettingsSection(
                        title = "User Settings",
                        items = listOf(
                            SettingsItemData(
                                title = "Profile",
                                icon = Icons.Filled.Person,
                                onClick = { /* Navigate to profile editing */ }
                            ),
                            SettingsItemData(
                                title = "Security & Logins",
                                icon = Icons.Filled.Lock,
                                onClick = { /* Navigate to security settings */ }
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Management Section
                    SettingsSection(
                        title = "Payment Management",
                        items = listOf(
                            SettingsItemData(
                                title = "Transaction History",
                                icon = Icons.Filled.History,
                                onClick = { /* Navigate to transaction history */ }
                            ),
                            SettingsItemData(
                                title = "Linked Accounts",
                                icon = Icons.Filled.CreditCard,
                                onClick = { /* Navigate to linked accounts */ }
                            ),
                            SettingsItemData(
                                title = "Spending Insights",
                                icon = Icons.Filled.AttachMoney,
                                onClick = { /* Navigate to spending insights */ }
                            ),
                            SettingsItemData(
                                title = "Wallet & Balance",
                                icon = Icons.Filled.AccountBalanceWallet,
                                onClick = { /* Navigate to wallet section */ }
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // App Preferences Section
                    SettingsSection(
                        title = "App Preferences",
                        items = listOf(
                            SettingsItemData(
                                title = "Notification Settings",
                                icon = Icons.Filled.Notifications,
                                onClick = { /* Navigate to notification settings */ }
                            ),
                            SettingsItemData(
                                title = "App Preferences",
                                icon = Icons.Filled.Settings,
                                onClick = { /* Navigate to app preferences */ }
                            ),
                            SettingsItemData(
                                title = "Feedback",
                                icon = Icons.Filled.Feedback,
                                onClick = { /* Navigate to feedback screen */ }
                            ),
                            SettingsItemData(
                                title = "About App",
                                icon = Icons.Filled.Info,
                                onClick = { /* Navigate to about app screen */ }
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Logout Button
                    Button(
                        onClick = { handleLogout() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            "Logout",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// Data class for settings items
data class SettingsItemData(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

// Settings Section
@Composable
fun SettingsSection(title: String, items: List<SettingsItemData>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A11CB),
                modifier = Modifier.padding(16.dp)
            )

            HorizontalDivider(
                color = Color(0xFF6A11CB).copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            items.forEachIndexed { index, item ->
                SettingsItem(
                    title = item.title,
                    icon = item.icon,
                    onClick = item.onClick
                )

                if (index < items.size - 1) {
                    HorizontalDivider(
                        color = Color(0xFF6A11CB).copy(alpha = 0.1f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

// Settings Item
@Composable
fun SettingsItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2575FC),
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF2575FC).copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}

// Firebase Logout
fun handleLogout() {
    FirebaseAuth.getInstance().signOut()
    // Handle navigation to login screen if necessary
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(navController = rememberNavController())
}