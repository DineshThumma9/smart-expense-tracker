package com.example.something.db.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.something.entity.PaymentWithTags
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


@Dao
interface PaymentWithTagsDao {

    @Transaction
    @Query("SELECT * FROM payments WHERE id = :paymentId")
    fun getPaymentWithTags(paymentId: Long): Flow<PaymentWithTags>


    @Transaction
    @Query("SELECT * FROM payments WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getAllPaymentsInDateRangeWithTags(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<PaymentWithTags>>

    @Transaction
    @Query("""
        SELECT * FROM payments 
        WHERE id IN (
            SELECT id FROM PaymentTagsCrossRef 
            WHERE tagId = :tagId
        ) 
        ORDER BY date DESC
    """)
    fun getAllPaymentsWithTagsByTag(tagId: Long): Flow<List<PaymentWithTags>>
}