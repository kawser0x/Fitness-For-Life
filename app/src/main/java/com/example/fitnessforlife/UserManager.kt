package com.example.fitnessforlife

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveExercises(exercises: List<Exercise>) {
        val json = gson.toJson(exercises)
        prefs.edit().putString("exercises", json).apply()
    }

    fun getExercises(): List<Exercise> {
        val json = prefs.getString("exercises", null) ?: return emptyList()
        val type = object : TypeToken<List<Exercise>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveUser(name: String, email: String, age: String, weight: String, password: String, height: String, imageUri: String? = null) {
        prefs.edit().apply {
            putString("name", name)
            putString("email", email)
            putString("age", age)
            putString("weight", weight)
            putString("height", height)
            putString("password", password)
            putString("image_uri", imageUri)
            // Note: We DO NOT set is_logged_in to true here
            apply()
        }
    }

    fun login(email: String, password: String): Boolean {
        val savedEmail = prefs.getString("email", null)
        val savedPassword = prefs.getString("password", null)
        
        return if (savedEmail == email && savedPassword == password) {
            prefs.edit().putBoolean("is_logged_in", true).apply()
            true
        } else {
            false
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun getUserName(): String? = prefs.getString("name", "")
    fun getUserEmail(): String? = prefs.getString("email", "")
    fun getUserAge(): String? = prefs.getString("age", "")
    fun getUserWeight(): String? = prefs.getString("weight", "")
    fun getUserHeight(): String? = prefs.getString("height", "")
    fun getUserImageUri(): String? = prefs.getString("image_uri", null)

    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }

    fun updateProfile(name: String, age: String, weight: String, height: String, imageUri: String?) {
        prefs.edit().apply {
            putString("name", name)
            putString("age", age)
            putString("weight", weight)
            putString("height", height)
            putString("image_uri", imageUri)
            apply()
        }
    }
}
