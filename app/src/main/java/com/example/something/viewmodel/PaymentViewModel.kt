package com.example.something.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.something.db.cloud.PaymentFirestoreDao
import com.example.something.db.local.PaymentDao
import com.example.something.db.local.PaymentTagsCrossRefDao
import com.example.something.db.local.PaymentWithTagsDao
import com.example.something.db.local.TagDao
import com.example.something.entity.Payment
import com.example.something.entity.PaymentMongo
import com.example.something.entity.PaymentTagsCrossRef
import com.example.something.entity.PaymentWithTags
import com.example.something.entity.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class PaymentViewModel(
    private val paymentDao: PaymentDao,
    private val tagDao: TagDao,
    private val paymentWithTagsDao: PaymentWithTagsDao,
    private val crossRefDao: PaymentTagsCrossRefDao,
    private val paymentFirestoreDao: PaymentFirestoreDao  // Updated to use Firestore DAO
) : ViewModel() {

    // Private mutable state for current payment balance
    private val _currPayment = mutableStateOf(0.0)
    // Public read-only observable state
    val currPayment: State<Double> get() = _currPayment

    // Function to set the current payment balance
    fun setCurrPayment(amount: Double) {
        _currPayment.value = amount
    }

    // Function to save a payment and update the current payment balance
    suspend fun savePaymentWithTags(sender: String, amount: Double, tags: List<String>) {
        val payment = Payment(sender = sender, amount = amount)
        withContext(Dispatchers.IO) {
            // Insert into Room and get the generated payment ID
            val paymentId = paymentDao.upsert(payment)

            // Update the current payment balance
            _currPayment.value -= amount

            // Create a PaymentMongo object to save in Firestore
            val firestorePayment = PaymentMongo(
                sender = sender,
                amount = amount.toDouble(),
                tags = tags
            )

            Log.d("PaymentViewModel", "Saving payment to Firestore: $firestorePayment")
            paymentFirestoreDao.insert(firestorePayment)
            Log.d("PaymentViewModel", "Payment saved to Firestore")

            // Handle tags in Room
            tags.forEach { tagName ->
                var tag = tagDao.getTagByName(tagName)
                if (tag == null) {
                    tag = Tag(tag = tagName)
                    val tagId = tagDao.upsert(tag)
                    tag = tag.copy(tagId = tagId)
                }
                crossRefDao.insert(PaymentTagsCrossRef(paymentId, tag.tagId))
            }
        }
    }

    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()

    fun getAllPaymentsInRange(range: String): Flow<List<Payment>> {
        val days = when (range) {
            "Today" -> 1
            "Week" -> 7
            "Month" -> 30
            "Year" -> 365
            else -> throw IllegalArgumentException("Invalid range: $range")
        }
        val end = LocalDateTime.now()
        val start = end.minusDays(days.toLong())
        return paymentDao.getAllPaymentInDateRange(start, end)
    }

    fun getAllPaymentsWithTagsInRange(range: String): Flow<List<PaymentWithTags>> {
        val days = when (range) {
            "Today" -> 1
            "Week" -> 7
            "Month" -> 30
            "Year" -> 365
            else -> throw IllegalArgumentException("Invalid range: $range")
        }
        val end = LocalDateTime.now()
        val start = end.minusDays(days.toLong())
        return paymentWithTagsDao.getAllPaymentsInDateRangeWithTags(start, end)
    }

    fun getAllPaymentsWithSpecifiedTag(tagName: String): Flow<List<Payment>> {
        val tag = tagDao.getTagByName(tagName) ?: return flowOf(emptyList())
        return paymentDao.getAllPaymentsWithTags(tag.tagId)
    }

    fun getAllPayments(): Flow<List<Payment>> = paymentDao.getAllPayments()

    fun getAllPaymentsWithTagSpecifiedTag(tagName: String): Flow<List<PaymentWithTags>> {
        val tag = tagDao.getTagByName(tagName) ?: return flowOf(emptyList())
        return paymentWithTagsDao.getAllPaymentsWithTagsByTag(tag.tagId)
    }

    // Update current payment balance from a string value
    fun updateCurrPayment(newAmount: String) {
        _currPayment.value = newAmount.toDouble()
        // Optionally, save this to your database or persistent storage
    }
}
