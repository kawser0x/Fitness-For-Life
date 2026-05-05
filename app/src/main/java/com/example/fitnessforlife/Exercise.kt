package com.example.fitnessforlife

data class Exercise(
    val name: String,
    val type: String,
    val muscleGroup: String,
    val sets: Int?,
    val reps: Int?,
    val weight: Double?,
    val restTime: Int?,
    val duration: Int?
)
