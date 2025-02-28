package com.example.something.db.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.example.something.entity.PaymentTagsCrossRef


@Dao
interface PaymentTagsCrossRefDao {
    @Insert
    suspend fun insert(crossRef: PaymentTagsCrossRef)

    @Delete
    suspend fun delete(crossRef: PaymentTagsCrossRef)
}