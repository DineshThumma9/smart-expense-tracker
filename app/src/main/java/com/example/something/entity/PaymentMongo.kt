package com.example.something.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant

data class PaymentMongo(
    @BsonId
    val id: ObjectId? = null, // MongoDB _id (ObjectId)

    val sender: String = "",

    val amount: Double = 0.0,

    val date: Instant = Instant.now(),

    val tags: List<String> = emptyList() // Store tag names directly
)
