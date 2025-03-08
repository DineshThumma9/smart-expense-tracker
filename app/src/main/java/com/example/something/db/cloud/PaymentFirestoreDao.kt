package com.example.something.db.cloud

import android.util.Log
import com.example.something.entity.PaymentMongo
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date

class PaymentFirestoreDao {

    private val TAG = "PaymentFirestoreDao"
    private val paymentsCollection = FirestoreClient.firestore.collection("payments")

    // ----------------------------
    // Helper: Log Firestore errors
    // ----------------------------
    private fun logFirestoreError(e: Exception) {
        Log.e(TAG, "Firestore query failed: ${e.localizedMessage}", e)
    }

    // --------------------------------------------------
    // Helper: Convert a Firestore Query into a Flow stream
    // --------------------------------------------------
    private fun queryPayments(query: Query): Flow<List<PaymentMongo>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                logFirestoreError(e)
                return@addSnapshotListener
            }
            val payments = snapshot?.documents?.mapNotNull { it.toObject(PaymentMongo::class.java) } ?: emptyList()
            trySend(payments)
        }
        awaitClose {
            listenerRegistration.remove()
            Log.d(TAG, "Firestore listener removed.")
        }
    }

    // --------------------------------------------------
    // Helper: Build a query with optional filters (excluding tags)
    // --------------------------------------------------
    private fun buildQuery(
        name: String? = null,
        dateRange: Pair<Date, Date>? = null,
        amountRange: Pair<Int, Int>? = null,
        type: String? = null
    ): Query {
        var query: Query = paymentsCollection
        name?.let { query = query.whereEqualTo("sender", it) }
        type?.let { query = query.whereEqualTo("type", it) }
        dateRange?.let {
            query = query.whereGreaterThanOrEqualTo("date", it.first)
                .whereLessThanOrEqualTo("date", it.second)
        }
        amountRange?.let {
            query = query.whereGreaterThanOrEqualTo("amount", it.first)
                .whereLessThanOrEqualTo("amount", it.second)
        }
        return query
    }

    // ----------------------------
    // Write Operation: Insert using Firestore's auto-generated ID.
    // ----------------------------
    suspend fun insert(payment: PaymentMongo) {
        Log.d(TAG, "Inserting payment to Firestore: $payment")
        // Firestore generates the document ID automatically using add()
        val docRef = paymentsCollection.add(payment).await()
        Log.d(TAG, "Payment inserted with auto-generated id: ${docRef.id}")
    }

    // ----------------------------
    // Read Operations (Real-time using Flow)
    // ----------------------------
    fun getAllPayments(): Flow<List<PaymentMongo>> =
        queryPayments(paymentsCollection)

    fun getPaymentByName(name: String): Flow<List<PaymentMongo>> =
        queryPayments(paymentsCollection.whereEqualTo("sender", name))

    fun getPaymentAbove(amount: Int): Flow<List<PaymentMongo>> =
        queryPayments(paymentsCollection.whereGreaterThanOrEqualTo("amount", amount))

    fun getPaymentReceived(): Flow<List<PaymentMongo>> =
        queryPayments(paymentsCollection.whereEqualTo("type", "received"))

    fun getPaymentTransferred(): Flow<List<PaymentMongo>> =
        queryPayments(paymentsCollection.whereEqualTo("type", "transferred"))

    // --------------------------------------------------
    // Query by Tags: For multiple tags, perform individual queries and merge results.
    // --------------------------------------------------
    fun getPaymentsByTags(tags: List<String>): Flow<List<PaymentMongo>> = flow {
        if (tags.isEmpty()) {
            val querySnapshot = paymentsCollection.get().await()
            val payments = querySnapshot.documents.mapNotNull { it.toObject(PaymentMongo::class.java) }
            emit(payments)
        } else {
            val allPayments = mutableSetOf<PaymentMongo>()
            for (tag in tags) {
                val querySnapshot = paymentsCollection.whereArrayContains("tags", tag).get().await()
                val payments = querySnapshot.documents.mapNotNull { it.toObject(PaymentMongo::class.java) }
                allPayments.addAll(payments)
            }
            // Ensure each payment contains all the requested tags
            val filteredPayments = allPayments.filter { it.tags.containsAll(tags) }
            emit(filteredPayments)
        }
    }

    // --------------------------------------------------
    // Custom Query combining several filters
    // --------------------------------------------------
    fun getCustomPayments(
        name: String? = null,
        dateRange: Pair<Date, Date>? = null,
        amountRange: Pair<Int, Int>? = null,
        tags: List<String>? = null,
        type: String? = null
    ): Flow<List<PaymentMongo>> = callbackFlow {
        var query = buildQuery(name, dateRange, amountRange, type)
        // For tags, we'll filter using the first tag and perform in-memory filtering for additional tags.
        val singleTag = tags?.firstOrNull()
        if (singleTag != null) {
            query = query.whereArrayContains("tags", singleTag)
        }
        val listenerRegistration = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                logFirestoreError(e)
                return@addSnapshotListener
            }
            var payments = snapshot?.documents?.mapNotNull { it.toObject(PaymentMongo::class.java) } ?: emptyList()
            if (tags != null && tags.size > 1) {
                payments = payments.filter { it.tags.containsAll(tags) }
            }
            trySend(payments)
        }
        awaitClose {
            listenerRegistration.remove()
            Log.d(TAG, "Custom payments listener removed.")
        }
    }

    fun paymentsInDateRange(from: Date, to: Date): Flow<List<PaymentMongo>> =
        queryPayments(
            paymentsCollection
                .whereGreaterThanOrEqualTo("date", from)
                .whereLessThanOrEqualTo("date", to)
        )

    fun paymentsDateAbove(from: Date): Flow<List<PaymentMongo>> =
        queryPayments(paymentsCollection.whereGreaterThanOrEqualTo("date", from))

    fun paymentsByAUser(user: String): Flow<List<PaymentMongo>> =
        getPaymentByName(user)

    fun paymentsByAmount(min: Int, max: Int): Flow<List<PaymentMongo>> =
        queryPayments(
            paymentsCollection
                .whereGreaterThanOrEqualTo("amount", min)
                .whereLessThanOrEqualTo("amount", max)
        )

    fun paymentsByType(type: String): Flow<List<PaymentMongo>> =
        queryPayments(paymentsCollection.whereEqualTo("type", type))

    // ----------------------------
    // Aggregation Functions (Client-Side)
    // ----------------------------
    suspend fun totalSumOfPayments(): Int {
        val querySnapshot = paymentsCollection.get().await()
        val payments = querySnapshot.documents.mapNotNull { it.toObject(PaymentMongo::class.java) }
        return payments.sumOf { it.amount ?: 0.0 }.toInt()
    }

    suspend fun totalPayments(): Long {
        val snapshot = paymentsCollection.get().await()
        return snapshot.size().toLong()
    }

    suspend fun totalSumOfPaymentsByUser(user: String): Int {
        val querySnapshot = paymentsCollection.whereEqualTo("sender", user).get().await()
        val payments = querySnapshot.documents.mapNotNull { it.toObject(PaymentMongo::class.java) }
        return payments.sumOf { it.amount ?: 0.0 }.toInt()
    }

    suspend fun totalSumOfPaymentsInDateRange(from: Date, to: Date): Int {
        val querySnapshot = paymentsCollection
            .whereGreaterThanOrEqualTo("date", from)
            .whereLessThanOrEqualTo("date", to)
            .get().await()
        val payments = querySnapshot.documents.mapNotNull { it.toObject(PaymentMongo::class.java) }
        return payments.sumOf { it.amount ?: 0.0 }.toInt()
    }

    suspend fun totalSumOfPaymentsByTags(tags: List<String>): Int {
        val singleTag = tags.firstOrNull()
        val query = if (singleTag != null) {
            paymentsCollection.whereArrayContains("tags", singleTag)
        } else {
            paymentsCollection
        }
        val querySnapshot = query.get().await()
        var payments = querySnapshot.documents.mapNotNull { it.toObject(PaymentMongo::class.java) }
        if (tags.size > 1) {
            payments = payments.filter { it.tags.containsAll(tags) }
        }
        return payments.sumOf { it.amount ?: 0.0 }.toInt()
    }

    suspend fun totalSumOfPaymentsByTransactionType(type: String): Int {
        val querySnapshot = paymentsCollection.whereEqualTo("type", type).get().await()
        val payments = querySnapshot.documents.mapNotNull { it.toObject(PaymentMongo::class.java) }
        return payments.sumOf { it.amount ?: 0.0 }.toInt()
    }

    suspend fun totalSumOfPaymentsByCustomQuery(
        name: String? = null,
        dateRange: Pair<Date, Date>? = null,
        amountRange: Pair<Int, Int>? = null,
        tags: List<String>? = null,
        type: String? = null
    ): Int {
        var query = buildQuery(name, dateRange, amountRange, type)
        val singleTag = tags?.firstOrNull()
        if (singleTag != null) {
            query = query.whereArrayContains("tags", singleTag)
        }
        val querySnapshot = query.get().await()
        var payments = querySnapshot.documents.mapNotNull { it.toObject(PaymentMongo::class.java) }
        if (tags != null && tags.size > 1) {
            payments = payments.filter { it.tags.containsAll(tags) }
        }
        return payments.sumOf { it.amount ?: 0.0 }.toInt()
    }
}
