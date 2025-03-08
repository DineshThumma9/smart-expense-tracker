package com.example.something

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.something.db.local.AppDatabase
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class something : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

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

        // Initialize Room database in a background thread
        applicationScope.launch(Dispatchers.IO) {
            try {
                database.paymentDao()
                // MongoDB initialization removed since we use Firestore for cloud operations.
            } catch (e: Exception) {
                Log.e("something", "Error initializing databases", e)
            }
        }
    }
}

fun Context.getDatabase(): AppDatabase {
    return (applicationContext as something).database
}
