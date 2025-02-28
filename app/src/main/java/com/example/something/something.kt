package com.example.something

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.something.db.local.AppDatabase
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class something : Application() {
    // No need to make it public
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Use a lazy delegate to ensure the database is created only once
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "something-database"
        ).build()
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Initialize the database in a background thread
        applicationScope.launch {
            try {
                // Accessing the database will trigger its creation
                database.paymentDao()
            } catch (e: Exception) {
                Log.e("something", "Error initializing database", e)
            }
        }
    }
}

// Extension function to get the database from the application context
fun Context.getDatabase(): AppDatabase {
    return (applicationContext as something).database
}
