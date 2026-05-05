# Fitness For Life

Fitness For Life is a comprehensive Android application designed to help users track their fitness journey, manage workout plans, and maintain a healthy lifestyle.

## 🚀 Features

- **User Authentication**: Secure Login and Sign Up system with profile image support.
- **Dynamic Dashboard**: A vibrant home screen featuring a hero carousel and quick access to fitness tools.
- **Workout Management**: 
  - View existing exercises and workout routines.
  - Add new exercises with details like sets, reps, weight, and rest time.
  - Categorized workouts (Strength, Cardio, Flexibility).
- **Video Tutorials**: Integration with YouTube for professional fitness guidance.
- **Profile Management**: 
  - Live BMI (Body Mass Index) calculation.
  - Track weight, height, and age.
  - Edit profile information and change profile pictures.
- **Personalized Experience**: The app greets users and maintains their session locally.

## 🛠️ Built With

- **Kotlin**: Primary programming language.
- **Material Design 3**: For a modern and intuitive user interface.
- **SharedPreferences**: For local user data and session persistence.
- **Gson**: For efficient data serialization.
- **ViewPager2 & RecyclerView**: For smooth list and carousel experiences.

## 📸 Screen Highlights

- **Home**: Dynamic carousel showing motivational fitness imagery and quick-action cards.
- **Plans**: Categorized fitness plans (Daily, Weekly, Monthly) and educational video content.
- **Profile**: Comprehensive view of user stats and personal information.

## 🏁 Getting Started

### Prerequisites
- Android Studio Ladybug or newer.
- Android SDK 24 (Android 7.0) or higher.

### Installation
1. Clone the repository.
2. Open the project in Android Studio.
3. Sync Project with Gradle Files.
4. Run the app on an emulator or a physical device.

## 🧹 Project Structure

- `MainActivity.kt`: The core controller managing dynamic fragment loading and navigation logic.
- `UserManager.kt`: Handles user data persistence and authentication states.
- `Exercise.kt` & `ExerciseAdapter.kt`: Data models and UI binding for workout tracking.
- `HeroAdapter.kt`: Manages the motivational carousel on the Home screen.

---
*Stay fit, stay healthy!*
