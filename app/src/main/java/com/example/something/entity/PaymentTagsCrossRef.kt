package com.example.something.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["id", "tagId"],
    indices = [Index(value = ["tagId"])] // Add an index for tagId
)
data class PaymentTagsCrossRef(
    val id: Long,
    val tagId: Long
)