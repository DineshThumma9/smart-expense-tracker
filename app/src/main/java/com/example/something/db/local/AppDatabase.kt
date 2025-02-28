package com.example.something.db.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.something.entity.Payment
import com.example.something.entity.Tag // Ensure this is your custom Tag entity
import com.example.something.entity.PaymentTagsCrossRef
import com.example.something.util.Converters

@Database(
    entities = [Payment::class, Tag::class, PaymentTagsCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun paymentDao(): PaymentDao
    abstract fun tagDao(): TagDao
    abstract fun paymentTagsCrossRefDao(): PaymentTagsCrossRefDao
    abstract fun paymentWithTagsDao(): PaymentWithTagsDao
}