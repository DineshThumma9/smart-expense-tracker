/*
package com.example.something.db.cloud

import android.util.Log
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

object MongoDBClient {
    private const val TAG = "MongoDBClient"
    private const val MONGODB_URI = "mongodb://dineshthumma15:rLZBc6kYij2wwCBA@duct.lsgqe.mongodb.net/paymentdb?retryWrites=false&w=majority"
    private const val DATABASE_NAME = "paymentdb"

    private lateinit var client: MongoClient
    lateinit var database: MongoDatabase

    init {
        try {
            Log.d(TAG, "Initializing MongoDB connection")

            val settings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(MONGODB_URI))
                .applyToSslSettings { it.enabled(true) }
                .applyToSocketSettings {
                    it.connectTimeout(60, TimeUnit.SECONDS) // Increased timeout
                    it.readTimeout(60, TimeUnit.SECONDS)
                }
                .applyToClusterSettings {
                    it.serverSelectionTimeout(60, TimeUnit.SECONDS) // Increased server selection timeout
                }
                .build()

            client = MongoClient.create(settings)
            database = client.getDatabase(DATABASE_NAME)
            Log.d(TAG, "MongoDB connection established")
        } catch (e: Exception) {
            Log.e(TAG, "MongoDB connection failed", e)
        }
    }
    */
/** Test MongoDB connection *//*

    suspend fun testConnection() {
        withContext(Dispatchers.IO) {
            try {
                database.listCollectionNames().collect {
                    Log.d(TAG, "Connected to MongoDB: Collection found - $it")
                }
            } catch (e: Exception) {
                Log.e(TAG, "MongoDB Test Connection Failed", e)
            }
        }
    }
}
*/
