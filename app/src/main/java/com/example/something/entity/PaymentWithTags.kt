package com.example.something.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation





data class PaymentWithTags(
    @Embedded
    var payment: Payment,

    @Relation(
        parentColumn = "id", // Matches the primary key column in Payment
        entityColumn = "tagId", // Matches the primary key column in Tag
        associateBy = Junction(PaymentTagsCrossRef::class)
    )
    var tags: List<Tag> // Change to var
) {
    constructor() : this(Payment(), emptyList()) // Add a secondary constructor
}