package com.example.fitnessforlife

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExerciseAdapter(private val exercises: List<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.exerciseName)
        val type: TextView = view.findViewById(R.id.exerciseType)
        val muscle: TextView = view.findViewById(R.id.muscleGroup)
        val setsReps: TextView = view.findViewById(R.id.setsReps)
        val weight: TextView = view.findViewById(R.id.weightText)
        val rest: TextView = view.findViewById(R.id.restDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.name.text = exercise.name
        holder.type.text = exercise.type
        holder.muscle.text = "Muscle: ${exercise.muscleGroup}"
        
        if (exercise.type.equals("Cardio", ignoreCase = true)) {
            holder.setsReps.text = "${exercise.duration} mins"
            holder.setsReps.visibility = View.VISIBLE
            holder.weight.visibility = View.GONE
            holder.rest.visibility = if (exercise.restTime != null) View.VISIBLE else View.GONE
            exercise.restTime?.let { holder.rest.text = "${it}s Rest" }
        } else {
            holder.setsReps.text = "${exercise.sets} Sets x ${exercise.reps} Reps"
            holder.weight.text = "${exercise.weight} kg"
            holder.rest.text = "${exercise.restTime}s Rest"
            holder.setsReps.visibility = View.VISIBLE
            holder.weight.visibility = View.VISIBLE
            holder.rest.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = exercises.size
}
