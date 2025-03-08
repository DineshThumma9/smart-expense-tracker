

package com.example.something.db.cloud

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

object FirestoreClient {
    private const val TAG = "FirestoreClient"

    val firestore: FirebaseFirestore by lazy {
        val db = FirebaseFirestore.getInstance()

        // Optional: Configure Firestore settings
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)  // Enable offline persistence
            .build()
        db.firestoreSettings = settings

        Log.d(TAG, "Connected to Firestore")
        db
    }
}