package com.example.anandbhavan.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Policy(
    var id: String = "",
    var name: String = "",
    var policyNumber: String = "",
    var category: String = "", // "Healthcare", "Vehicles", "Insurance"
    var startDate: Date? = null,
    var endDate: Date? = null,
    var status: String = "Active", // "Active", "Renewed", "Legacy"
    @ServerTimestamp
    var createdAt: Date? = null
) {
    // Empty constructor for Firestore serialization
    constructor() : this("", "", "", "", null, null, "Active", null)
}
