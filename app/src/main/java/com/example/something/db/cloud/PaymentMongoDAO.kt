/*
package com.example.something.db.cloud

import android.util.Log
import com.example.something.entity.PaymentMongo
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.bson.Document
import java.util.Date

class PaymentMongoDao {
    private val paymentCollection: MongoCollection<PaymentMongo> =
        MongoDBClient.database?.getCollection("payments") ?: throw Exception("Payments collection not found")

    suspend fun insertOne(payment: PaymentMongo) {
        paymentCollection.insertOne(payment)
    }

    suspend fun getAllPayments(): Flow<List<PaymentMongo>> {
        return flowOf(paymentCollection.find().toList())
    }

    suspend fun getPaymentByName(name: String): Flow<List<PaymentMongo>> {
        return flowOf(paymentCollection.find(Document("sender", name)).toList())
    }

    suspend fun getPaymentAbove(amount: Int): Flow<List<PaymentMongo>> {
        val query = Document("amount", Document("\$gte", amount))
        return flowOf(paymentCollection.find(query).toList())
    }

    suspend fun getPaymentReceived(): Flow<List<PaymentMongo>> {
        return flowOf(paymentCollection.find(Document("type", "received")).toList())
    }

    suspend fun getPaymentTransferred(): Flow<List<PaymentMongo>> {
        return flowOf(paymentCollection.find(Document("type", "transferred")).toList())
    }

    suspend fun getPaymentByTags(tags: List<String>): Flow<List<PaymentMongo>> {
        val query = Document("tags", Document("\$all", tags))
        return flowOf(paymentCollection.find(query).toList())
    }

    suspend fun getCustomPayments(
        name: String? = null,
        dateRange: Pair<Date, Date>? = null,
        amountRange: Pair<Int, Int>? = null,
        tags: List<String>? = null,
        type: String? = null // "received" or "transferred"
    ): Flow<List<PaymentMongo>> {
        val query = Document()

        name?.let { query.append("sender", it) }
        dateRange?.let { query.append("date", Document("\$gte", it.first).append("\$lte", it.second)) }
        amountRange?.let { query.append("amount", Document("\$gte", it.first).append("\$lte", it.second)) }
        tags?.let { query.append("tags", Document("\$all", it)) }
        type?.let { query.append("type", it) }

        return flowOf(paymentCollection.find(query).toList())
    }

    suspend fun totalSumOfPaymentsByCustomQuery(
        name: String? = null,
        dateRange: Pair<Date, Date>? = null,
        amountRange: Pair<Int, Int>? = null,
        tags: List<String>? = null,
        type: String? = null
    ): Int {
        val matchQuery = Document()
        name?.let { matchQuery.append("sender", it) }
        dateRange?.let { matchQuery.append("date", Document("\$gte", it.first).append("\$lte", it.second)) }
        amountRange?.let { matchQuery.append("amount", Document("\$gte", it.first).append("\$lte", it.second)) }
        tags?.let { matchQuery.append("tags", Document("\$all", it)) }
        type?.let { matchQuery.append("type", it) }

        val pipeline = listOf(
            Document("\$match", matchQuery),
            Document("\$group", Document("_id", null).append("total", Document("\$sum", "\$amount")))
        )
        val result = paymentCollection.aggregate<Document>(pipeline).toList()
        return result.firstOrNull()?.getInteger("total") ?: 0
    }

    suspend fun paymentsInDateRange(from: Date, to: Date): Flow<List<PaymentMongo>> {
        val query = Document("date", Document("\$gte", from).append("\$lte", to))
        return flowOf(paymentCollection.find(query).toList())
    }

    suspend fun paymentsDateAbove(from: Date): Flow<List<PaymentMongo>> {
        val query = Document("date", Document("\$gte", from))
        Log.d("PaymentMongoDao", "Query: $query")
        Log.d("PaymentMongoDao","Fetching payments from MongoDB")
        return flowOf(paymentCollection.find(query).toList())
    }

    suspend fun paymentsByAUser(user: String): Flow<List<PaymentMongo>> {
        Log.d("PaymentMongoDao", "Query: $user")
        Log.d("PaymentMongoDao","Fetching payments from MongoDB")
        return flowOf(paymentCollection.find(Document("sender", user)).toList())
    }

    suspend fun paymentsByAmount(min: Int, max: Int): Flow<List<PaymentMongo>> {
        val query = Document("amount", Document("\$gte", min).append("\$lte", max))
        Log.d("PaymentMongoDao", "Query: $query")
        Log.d("PaymentMongoDao","Fetching payments from MongoDB")
        return flowOf(paymentCollection.find(query).toList())
    }

    suspend fun paymentsByType(type: String): Flow<List<PaymentMongo>> {
        Log.d("PaymentMongoDao", "Query: $type")
        Log.d("PaymentMongoDao","Fetching payments from MongoDB")
        return flowOf(paymentCollection.find(Document("type", type)).toList())
    }

    suspend fun paymentsByTag(tags: List<String>): Flow<List<PaymentMongo>> {
        val query = Document("tags", Document("\$all", tags))
        return flowOf(paymentCollection.find(query).toList())
    }

    suspend fun totalSumOfPayments(): Int {
        val pipeline = listOf(
            Document(
                "\$group", Document("_id", null)
                    .append("total", Document("\$sum", "\$amount"))
            )
        )
        val result = paymentCollection.aggregate<Document>(pipeline).toList()
        return result.firstOrNull()?.getInteger("total") ?: 0
    }

    suspend fun totalPayments(): Long {
        return paymentCollection.countDocuments()
    }

    suspend fun totalSumOfPaymentsByUser(user: String): Int {
        val pipeline = listOf(
            Document("\$match", Document("sender", user)),
            Document(
                "\$group", Document("_id", null)
                    .append("total", Document("\$sum", "\$amount"))
            )
        )
        val result = paymentCollection.aggregate<Document>(pipeline).toList()
        return result.firstOrNull()?.getInteger("total") ?: 0
    }

    suspend fun totalSumOfPaymentsInDateRange(from: Date, to: Date): Int {
        val pipeline = listOf(
            Document("\$match", Document("date", Document("\$gte", from).append("\$lte", to))),
            Document(
                "\$group", Document("_id", null)
                    .append("total", Document("\$sum", "\$amount"))
            )
        )
        val result = paymentCollection.aggregate<Document>(pipeline).toList()
        return result.firstOrNull()?.getInteger("total") ?: 0
    }

    suspend fun totalSumOfPaymentsByTags(tags: List<String>): Int {
        val pipeline = listOf(
            Document("\$match", Document("tags", Document("\$all", tags))),
            Document(
                "\$group", Document("_id", null)
                    .append("total", Document("\$sum", "\$amount"))
            )
        )
        val result = paymentCollection.aggregate<Document>(pipeline).toList()
        return result.firstOrNull()?.getInteger("total") ?: 0
    }

    suspend fun totalSumOfPaymentsByTransactionType(type: String): Int {
        val pipeline = listOf(
            Document("\$match", Document("type", type)),
            Document(
                "\$group", Document("_id", null)
                    .append("total", Document("\$sum", "\$amount"))
            )
        )
        val result = paymentCollection.aggregate<Document>(pipeline).toList()
        return result.firstOrNull()?.getInteger("total") ?: 0
    }

    suspend fun totalSumOfPaymentsByCustomQuery(query: Document): Int {
        val pipeline = listOf(
            Document("\$match", query),
            Document(
                "\$group", Document("_id", null)
                    .append("total", Document("\$sum", "\$amount"))
            )
        )
        val result = paymentCollection.aggregate<Document>(pipeline).toList()
        return result.firstOrNull()?.getInteger("total") ?: 0
    }

    suspend  fun insert(mongoPayment: PaymentMongo) {
        Log.d("PaymentMongoDao", "Inserting payment to MongoDB: $mongoPayment")
        paymentCollection.insertOne(mongoPayment)
        Log.d("PaymentMongoDao", "Payment inserted to MongoDB")
    }
}*/
