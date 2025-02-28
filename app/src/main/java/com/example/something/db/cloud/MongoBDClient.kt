package com.example.something.db.cloud

import android.annotation.SuppressLint
import android.util.Log
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase


object MongoDBClient {
    @SuppressLint("AuthLeak")
//    private const val  CONNECTION_STRING = "mongodb://localhost:27017"
  private const val CONNECTION_STRING = "mongodb://dineshthumma15:ZWOaKSpu7pcFlFWr@cluster0-shard-00-00.example.mongodb.net:27017,cluster0-shard-00-01.example.mongodb.net:27017,cluster0-shard-00-02.example.mongodb.net:27017/?retryWrites=true&w=majority&appName=duct"

    private const val DATABASE_NAME = "paymentdb"

    val database: MongoDatabase = run {
        val client = MongoClient.create(CONNECTION_STRING)
        Log.d("MongoDBClient", "Connected to MongoDB")
        client.getDatabase(DATABASE_NAME)

    }
}