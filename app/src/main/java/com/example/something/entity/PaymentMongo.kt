package com.example.something.entity

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PaymentMongo(
    val id: String? = null, // Firestore document ID; assign a unique string when creating a new document.
    val sender: String = "",
    val amount: Double = 0.0,
    @ServerTimestamp
    val date: Date? = null, // Automatically set to the server time upon write.
    val tags: List<String> = emptyList() // List of tag names.
)
