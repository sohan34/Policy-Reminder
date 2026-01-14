package com.example.anandbhavan.data

import com.example.anandbhavan.model.Policy
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object PolicyRepository {
    private val db = FirebaseFirestore.getInstance()
    private val policiesCollection = db.collection("policies")

    suspend fun addPolicy(policy: Policy) {
        val documentRef = policiesCollection.document()
        policy.id = documentRef.id
        documentRef.set(policy).await()
    }

    suspend fun updatePolicy(policy: Policy) {
        if (policy.id.isNotEmpty()) {
            policiesCollection.document(policy.id).set(policy).await()
        }
    }

    suspend fun deletePolicy(policyId: String) {
        policiesCollection.document(policyId).delete().await()
    }

    suspend fun getPoliciesByCategory(category: String): List<Policy> {
        return try {
            val snapshot = policiesCollection
                .whereEqualTo("category", category)
                .get()
                .await()

            snapshot.toObjects(Policy::class.java)
                .sortedBy { it.endDate?.time ?: 0L }   // ✅ Local sorting for Date
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllPolicies(): List<Policy> {
        return try {
            val snapshot = policiesCollection
                .get()
                .await()

            snapshot.toObjects(Policy::class.java)
                .sortedBy { it.endDate?.time ?: 0L }   // ✅ Local sorting for Date
        } catch (e: Exception) {
            emptyList()
        }
    }
}
