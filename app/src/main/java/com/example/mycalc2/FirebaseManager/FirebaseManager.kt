package com.example.mycalc2.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirebaseManager {

    private val db = FirebaseFirestore.getInstance()

    fun saveCalculation(expression: String, result: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val calcEntry = hashMapOf(
            "expression" to expression,
            "result" to result,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("calculations")
            .add(calcEntry)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun loadCalculationHistory(onSuccess: (List<Pair<String, String>>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("calculations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val history = mutableListOf<Pair<String, String>>()
                for (document in documents) {
                    val expression = document.getString("expression") ?: ""
                    val result = document.getString("result") ?: ""
                    history.add(expression to result)
                }
                onSuccess(history)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun clearHistory(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("calculations")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("calculations").document(document.id).delete()
                }
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }
}
