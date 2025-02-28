package com.example.something.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tags")
data class Tag(
     @PrimaryKey(autoGenerate = true)
     val tagId: Long = 0,
     val tag: String
)