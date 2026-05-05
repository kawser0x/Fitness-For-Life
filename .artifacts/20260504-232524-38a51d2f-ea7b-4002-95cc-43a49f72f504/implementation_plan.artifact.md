# Workout Plan Implementation

Added a comprehensive Workout Plan feature with a modern UI, allowing users to track exercises with specific details like sets, reps, weight, muscle group, etc.

## Proposed Changes

### Core Data Model

#### [NEW] [Exercise.kt](file:///C:/Users/HP/AndroidStudioProjects/FitnessForLife2/app/src/main/java/com/example/fitnessforlife/Exercise.kt)
- Data class to store exercise details: name, type, muscle group, sets, reps, weight, rest time, and duration.

### User Data Management

#### [UserManager.kt](file:///C:/Users/HP/AndroidStudioProjects/FitnessForLife2/app/src/main/java/com/example/fitnessforlife/UserManager.kt)
- Added `Gson` for JSON serialization.
- Added `saveExercises` and `getExercises` to persist workout plans in `SharedPreferences`.

### UI Components

#### [NEW] [fragment_workout_plan.xml](file:///C:/Users/HP/AndroidStudioProjects/FitnessForLife2/app/src/main/res/layout/fragment_workout_plan.xml)
- Main screen for workout plan with a `RecyclerView` and `FloatingActionButton`.

#### [NEW] [item_exercise.xml](file:///C:/Users/HP/AndroidStudioProjects/FitnessForLife2/app/src/main/res/layout/item_exercise.xml)
- Layout for individual exercise items in the list.

#### [NEW] [dialog_add_exercise.xml](file:///C:/Users/HP/AndroidStudioProjects/FitnessForLife2/app/src/main/res/layout/dialog_add_exercise.xml)
- Modern dialog for adding new exercises with all requested fields.

#### [NEW] [ExerciseAdapter.kt](file:///C:/Users/HP/AndroidStudioProjects/FitnessForLife2/app/src/main/java/com/example/fitnessforlife/ExerciseAdapter.kt)
- Adapter for the exercise list, handling different display logic for Cardio vs Strength exercises.

### Activity Integration

#### [MainActivity.kt](file:///C:/Users/HP/AndroidStudioProjects/FitnessForLife2/app/src/main/java/com/example/fitnessforlife/MainActivity.kt)
- Updated `loadFragment` to handle "Workout Plan" card clicks.
- Implemented `showWorkoutPlan()` and `showAddExerciseDialog()`.

## Verification Plan

### Automated Tests
- `app:assembleDebug`: Verified that the project builds correctly with new dependencies and files.

### Manual Verification
- Deploy the app.
- Login/Signup.
- Tap "Workout Plan" card on Home.
- Verify the empty state is shown.
- Tap "+" FAB to add an exercise.
- Fill in details (e.g., Bench Press, Strength, Chest, 3 sets, 10 reps, 60kg, 60s rest).
- Verify the exercise appears in the list with correct details.
- Add a Cardio exercise (e.g., Running, Cardio, Legs, 30 mins duration).
- Verify the card layout adapts for Cardio.
- Restart app and verify the workout plan is persisted.
