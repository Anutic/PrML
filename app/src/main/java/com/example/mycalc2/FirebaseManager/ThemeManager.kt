package com.example.mycalc2.utils

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val THEME_KEY = "theme_color"
    private const val COLLECTION_NAME = "themes"
    private const val DOCUMENT_ID = "user_theme" // Уникальный документ для хранения темы

    private val db = FirebaseFirestore.getInstance()

    // Сохраняем цвет в SharedPreferences и Firebase
    fun saveThemeColor(context: Context, color: Int) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(THEME_KEY, color).apply()

        saveThemeToFirebase(color)
    }

    // Загружаем сохраненный цвет (по умолчанию фиолетовый)
    fun getSavedThemeColor(context: Context, onColorLoaded: (Int) -> Unit) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val localColor = prefs.getInt(THEME_KEY, android.graphics.Color.parseColor("#FF6200EE"))

        db.collection(COLLECTION_NAME).document(DOCUMENT_ID).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val savedColor = document.getLong("color")?.toInt() ?: localColor
                    onColorLoaded(savedColor)
                } else {
                    onColorLoaded(localColor)
                }
            }
            .addOnFailureListener {
                onColorLoaded(localColor) // В случае ошибки загружаем локальный цвет
            }
    }

    // Сохраняем тему в Firestore
    private fun saveThemeToFirebase(color: Int) {
        val themeData = hashMapOf("color" to color)
        db.collection(COLLECTION_NAME).document(DOCUMENT_ID)
            .set(themeData)
    }

    // Применяем цвет к переданному View (например, rootLayout)
    fun applyTheme(rootView: View, color: Int) {
        rootView.setBackgroundColor(color)
    }
}
