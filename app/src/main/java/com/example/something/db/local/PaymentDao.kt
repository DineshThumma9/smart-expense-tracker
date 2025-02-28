package com.example.something.db.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.something.entity.Payment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import java.time.LocalDateTime


@Dao
interface PaymentDao {


    @Upsert
    suspend fun upsert(payment : Payment) : Long

    @Delete
    suspend fun delete(payment: Payment)

    @Query("SELECT  * FROM payments ORDER BY date DESC")
    fun getAllPayments() : Flow<List<Payment>>

    @Query("""
        SELECT * FROM payments 
        WHERE id IN (
            SELECT id FROM PaymentTagsCrossRef 
            WHERE tagId = :tagId
        ) 
        ORDER BY date DESC
    """)
    fun getAllPaymentsWithTags(tagId : Long) : Flow<List<Payment>>


    @Query("SELECT * FROM payments WHERE date  BETWEEN :startDate AND :endDate")
    fun getAllPaymentInDateRange(startDate:LocalDateTime , endDate:LocalDateTime) : Flow<List<Payment>>
}