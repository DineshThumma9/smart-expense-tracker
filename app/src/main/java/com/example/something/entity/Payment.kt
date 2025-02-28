package com.example.something.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bson.codecs.pojo.annotations.BsonId
import java.time.LocalDateTime



@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val sender: String = "",

    val amount: Double = 0.0,

    val date: LocalDateTime = LocalDateTime.now()
)
