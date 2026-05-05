package com.example.fitnessforlife

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import androidx.viewpager2.widget.ViewPager2
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.widget.ImageView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.text.TextWatcher
import android.text.Editable

class MainActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var appBarLayout: View
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var authContainer: View
    private lateinit var fragmentContainer: View
    
    private var selectedImageUri: Uri? = null
    private var signupProfilePreview: ImageView? = null
    private var editProfilePreview: ImageView? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            signupProfilePreview?.setImageURI(it)
            editProfilePreview?.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userManager = UserManager(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        topAppBar = findViewById(R.id.topAppBar)
        appBarLayout = findViewById(R.id.appBarLayout)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        authContainer = findViewById(R.id.authContainer)
        fragmentContainer = findViewById(R.id.fragmentContainer)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            appBarLayout.setPadding(0, systemBars.top, 0, 0)
            bottomNavigation.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        setupNavigation()

        if (userManager.isLoggedIn()) {
            showDashboard()
        } else {
            showLogin()
        }
    }

    private fun showLogin() {
        authContainer.visibility = View.VISIBLE
        appBarLayout.visibility = View.GONE
        bottomNavigation.visibility = View.GONE
        fragmentContainer.visibility = View.GONE

        val loginView = layoutInflater.inflate(R.layout.fragment_login, null)
        (authContainer as android.view.ViewGroup).removeAllViews()
        (authContainer as android.view.ViewGroup).addView(loginView)

        loginView.findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email = loginView.findViewById<TextInputEditText>(R.id.emailEditText).text.toString()
            val password = loginView.findViewById<TextInputEditText>(R.id.passwordEditText).text.toString()
            
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (userManager.login(email, password)) {
                    showDashboard()
                } else {
                    Toast.makeText(this, "Invalid credentials. Please Sign Up.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        loginView.findViewById<View>(R.id.signUpText).setOnClickListener {
            showSignUp()
        }

        // Apply spanning to Sign Up text
        val signUpText = loginView.findViewById<TextView>(R.id.signUpText)
        val fullText = "Don't have an account? Sign Up"
        val spannable = SpannableString(fullText)
        val signUpStart = fullText.indexOf("Sign Up")
        if (signUpStart != -1) {
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
                signUpStart, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                signUpStart, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        signUpText.text = spannable
    }

    private fun showSignUp() {
        val signUpView = layoutInflater.inflate(R.layout.fragment_signup, null)
        (authContainer as android.view.ViewGroup).removeAllViews()
        (authContainer as android.view.ViewGroup).addView(signUpView)
        
        signupProfilePreview = signUpView.findViewById(R.id.signupProfileImage)
        
        signUpView.findViewById<Button>(R.id.selectImageButton).setOnClickListener {
            pickImage.launch("image/*")
        }

        signUpView.findViewById<Button>(R.id.createAccountButton).setOnClickListener {
            val name = signUpView.findViewById<TextInputEditText>(R.id.nameEditText).text.toString()
            val email = signUpView.findViewById<TextInputEditText>(R.id.emailSignupEditText).text.toString()
            val age = signUpView.findViewById<TextInputEditText>(R.id.ageEditText).text.toString()
            val weight = signUpView.findViewById<TextInputEditText>(R.id.weightEditText).text.toString()
            val height = signUpView.findViewById<TextInputEditText>(R.id.heightEditText).text.toString()
            val password = signUpView.findViewById<TextInputEditText>(R.id.passwordSignupEditText).text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && height.isNotEmpty()) {
                userManager.saveUser(name, email, age, weight, password, height, selectedImageUri?.toString())
                Toast.makeText(this, "Account Created! Please Login.", Toast.LENGTH_SHORT).show()
                showLogin()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        signUpView.findViewById<View>(R.id.backToLogin).setOnClickListener {
            showLogin()
        }
    }

    private fun showDashboard() {
        authContainer.visibility = View.GONE
        appBarLayout.visibility = View.VISIBLE
        bottomNavigation.visibility = View.VISIBLE
        fragmentContainer.visibility = View.VISIBLE
        
        // Keep the app name consistent in the Toolbar
        topAppBar.title = getString(R.string.app_name)
        
        // Load Profile Image into Navbar
        val toolbarProfileImage = findViewById<ImageView>(R.id.toolbarProfileImage)
        setImageSafely(toolbarProfileImage, userManager.getUserImageUri())
        
        bottomNavigation.selectedItemId = R.id.nav_home
        loadFragment(R.id.nav_home)
    }

    private fun setImageSafely(imageView: ImageView?, uriString: String?) {
        if (uriString == null || imageView == null) return
        try {
            val uri = Uri.parse(uriString)
            imageView.setImageURI(uri)
        } catch (e: Exception) {
            imageView.setImageResource(R.mipmap.ic_launcher)
        }
    }

    private fun setupNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            loadFragment(item.itemId)
            true
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_about -> {
                    Toast.makeText(this, "Fitness For Life a app that provide tips for maintain healty life", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_faq -> {
                    Toast.makeText(this, "FAQ section coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_ai_suggestion -> {
                    Toast.makeText(this, "AI Feature coming soon.", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(itemId: Int) {
        // Ensure the title stays as the app name regardless of the fragment loaded
        topAppBar.title = getString(R.string.app_name)

        val view = when (itemId) {
            R.id.nav_home -> {
                val homeView = layoutInflater.inflate(R.layout.fragment_home, null)
                val viewPager = homeView.findViewById<ViewPager2>(R.id.heroViewPager)
                
                val heroItems = listOf(
                    HeroItem("Work Hard Today \n ......    " , R.drawable.man_workout),
                    HeroItem("Be Fit Together \n .....", R.drawable.man_and_woman_working_out_at_hoome),
                    HeroItem("Workout In Proper way \n ....", R.drawable.train),
                    HeroItem("Smiling Together Fit Also \n ...", R.drawable.both),
                    HeroItem("Squats and Lunge \n ..", R.drawable.abul),
                    HeroItem("More For Better You \n .", R.drawable.babul),
                    HeroItem("Enjoy the Journey", R.drawable.kabul)
                )
                
                viewPager.adapter = HeroAdapter(heroItems)

                // Make the welcome text colorful
                val welcomeTextView = homeView.findViewById<TextView>(R.id.homeDescription)
                val fullText = "Welcome to Fitness For Life! Explore our exercises and start your journey today."
                val spannable = SpannableString(fullText)

                // "Welcome" - Purple
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
                    0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // "Fitness" - Pink
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.secondary)),
                    11, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // "For" - Blue
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.accent_blue)),
                    19, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // "Life" - Green
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.accent_green)),
                    23, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                welcomeTextView.text = spannable

                // Action Card Click Listeners
                homeView.findViewById<View>(R.id.cardWorkout).setOnClickListener {
                    showWorkoutPlan()
                }
                homeView.findViewById<View>(R.id.cardDiet).setOnClickListener {
                    Toast.makeText(this, "Diet Plan coming soon", Toast.LENGTH_SHORT).show()
                }
                homeView.findViewById<View>(R.id.cardAiSuggestion).setOnClickListener {
                    Toast.makeText(this, "AI Suggestion coming soon", Toast.LENGTH_SHORT).show()
                }
                homeView.findViewById<View>(R.id.cardProgress).setOnClickListener {
                    Toast.makeText(this, "Progress Track coming soon", Toast.LENGTH_SHORT).show()
                }

                homeView
            }
            R.id.nav_plan -> {
                val planView = layoutInflater.inflate(R.layout.fragment_plans, null)
                
                planView.findViewById<View>(R.id.videoCard).setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/i91zPveRhEw?si=cok3nbfv5wqXPLxu"))
                    startActivity(intent)
                }

                planView.findViewById<Button>(R.id.btnDailyPlan).setOnClickListener {
                    Toast.makeText(this, "Daily Plan Details", Toast.LENGTH_SHORT).show()
                }
                planView.findViewById<Button>(R.id.btnWeeklyPlan).setOnClickListener {
                    Toast.makeText(this, "Weekly Plan Details", Toast.LENGTH_SHORT).show()
                }
                planView.findViewById<Button>(R.id.btnMonthlyPlan).setOnClickListener {
                    Toast.makeText(this, "Monthly Plan Details", Toast.LENGTH_SHORT).show()
                }
                planView.findViewById<Button>(R.id.btnQuarterlyPlan).setOnClickListener {
                    Toast.makeText(this, "Quarterly Plan Details", Toast.LENGTH_SHORT).show()
                }
                
                planView
            }
            R.id.nav_profile -> {
                val profileView = layoutInflater.inflate(R.layout.fragment_profile, null)
                profileView.findViewById<TextView>(R.id.profileName).text = userManager.getUserName()
                profileView.findViewById<TextView>(R.id.profileEmail).text = userManager.getUserEmail()
                profileView.findViewById<TextView>(R.id.profileAge).text = userManager.getUserAge()
                
                // Load User Image
                val profileImage = profileView.findViewById<ImageView>(R.id.profileImage)
                setImageSafely(profileImage, userManager.getUserImageUri())

                val weight = userManager.getUserWeight() ?: "0"
                profileView.findViewById<TextView>(R.id.profileWeight).text = "$weight kg"

                val height = userManager.getUserHeight() ?: "0"
                profileView.findViewById<TextView>(R.id.profileHeight).text = "$height cm"

                // Calculate BMI
                val weightVal = weight.toDoubleOrNull() ?: 0.0
                val heightCmVal = height.toDoubleOrNull() ?: 0.0
                val heightMVal = heightCmVal / 100.0
                val bmi = if (heightMVal > 0) weightVal / (heightMVal * heightMVal) else 0.0
                profileView.findViewById<TextView>(R.id.profileBmi).text = String.format("%.1f", bmi)
                
                profileView.findViewById<Button>(R.id.logoutButton).setOnClickListener {
                    userManager.logout()
                    showLogin()
                }

                profileView.findViewById<View>(R.id.btnEditProfile).setOnClickListener {
                    showEditProfileDialog()
                }
                profileView
            }
            else -> null
        }

        view?.let {
            (fragmentContainer as android.view.ViewGroup).removeAllViews()
            (fragmentContainer as android.view.ViewGroup).addView(it)
        }
    }

    private fun showWorkoutPlan() {
        val workoutView = layoutInflater.inflate(R.layout.fragment_workout_plan, null)
        (fragmentContainer as android.view.ViewGroup).removeAllViews()
        (fragmentContainer as android.view.ViewGroup).addView(workoutView)

        val recyclerView = workoutView.findViewById<RecyclerView>(R.id.exerciseRecyclerView)
        val emptyState = workoutView.findViewById<View>(R.id.emptyStateText)
        val addFab = workoutView.findViewById<View>(R.id.addExerciseFab)

        val exercises = userManager.getExercises().toMutableList()
        val adapter = ExerciseAdapter(exercises)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fun updateEmptyState() {
            emptyState.visibility = if (exercises.isEmpty()) View.VISIBLE else View.GONE
        }
        updateEmptyState()

        addFab.setOnClickListener {
            showAddExerciseDialog { newExercise ->
                exercises.add(newExercise)
                userManager.saveExercises(exercises)
                adapter.notifyItemInserted(exercises.size - 1)
                updateEmptyState()
            }
        }
    }

    private fun showAddExerciseDialog(onExerciseAdded: (Exercise) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_exercise, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()

        val typeSpinner = dialogView.findViewById<AutoCompleteTextView>(R.id.editWorkoutType)
        val types = listOf("Strength", "Cardio", "Flexibility")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, types)
        typeSpinner.setAdapter(typeAdapter)

        val nameEdit = dialogView.findViewById<TextInputEditText>(R.id.editExerciseName)
        val muscleEdit = dialogView.findViewById<TextInputEditText>(R.id.editMuscleGroup)
        val setsEdit = dialogView.findViewById<TextInputEditText>(R.id.editSets)
        val repsEdit = dialogView.findViewById<TextInputEditText>(R.id.editReps)
        val weightEdit = dialogView.findViewById<TextInputEditText>(R.id.editWeight)
        val restEdit = dialogView.findViewById<TextInputEditText>(R.id.editRestTime)
        val durationEdit = dialogView.findViewById<TextInputEditText>(R.id.editDuration)
        val durationLayout = dialogView.findViewById<View>(R.id.durationLayout)

        typeSpinner.setOnItemClickListener { _, _, position, _ ->
            if (types[position] == "Cardio") {
                durationLayout.visibility = View.VISIBLE
            } else {
                durationLayout.visibility = View.GONE
            }
        }

        dialogView.findViewById<Button>(R.id.btnAddExercise).setOnClickListener {
            val name = nameEdit.text.toString()
            val type = typeSpinner.text.toString()
            val muscle = muscleEdit.text.toString()
            val sets = setsEdit.text.toString().toIntOrNull()
            val reps = repsEdit.text.toString().toIntOrNull()
            val weight = weightEdit.text.toString().toDoubleOrNull()
            val rest = restEdit.text.toString().toIntOrNull()
            val duration = durationEdit.text.toString().toIntOrNull()

            if (name.isNotEmpty() && type.isNotEmpty()) {
                val exercise = Exercise(name, type, muscle, sets, reps, weight, rest, duration)
                onExerciseAdded(exercise)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill Name and Type", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()

        val nameEdit = dialogView.findViewById<TextInputEditText>(R.id.editName)
        val ageEdit = dialogView.findViewById<TextInputEditText>(R.id.editAge)
        val weightEdit = dialogView.findViewById<TextInputEditText>(R.id.editWeight)
        val heightEdit = dialogView.findViewById<TextInputEditText>(R.id.editHeight)
        val bmiText = dialogView.findViewById<TextView>(R.id.editBmiText)
        editProfilePreview = dialogView.findViewById(R.id.editProfileImage)

        // Pre-fill current data
        nameEdit.setText(userManager.getUserName())
        ageEdit.setText(userManager.getUserAge())
        weightEdit.setText(userManager.getUserWeight())
        heightEdit.setText(userManager.getUserHeight())
        val currentImageUri = userManager.getUserImageUri()
        setImageSafely(editProfilePreview, currentImageUri)
        selectedImageUri = currentImageUri?.let { Uri.parse(it) }

        fun updateBmiLive() {
            val weightVal = weightEdit.text.toString().toDoubleOrNull() ?: 0.0
            val heightCmVal = heightEdit.text.toString().toDoubleOrNull() ?: 0.0
            val heightMVal = heightCmVal / 100.0
            val bmi = if (heightMVal > 0) weightVal / (heightMVal * heightMVal) else 0.0
            bmiText.text = if (bmi > 0) String.format("%.1f", bmi) else "--"
        }

        // Initial BMI calculation
        updateBmiLive()

        val bmiWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateBmiLive()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        weightEdit.addTextChangedListener(bmiWatcher)
        heightEdit.addTextChangedListener(bmiWatcher)

        dialogView.findViewById<Button>(R.id.btnChangePhoto).setOnClickListener {
            pickImage.launch("image/*")
        }

        dialogView.findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            val name = nameEdit.text.toString()
            val age = ageEdit.text.toString()
            val weight = weightEdit.text.toString()
            val height = heightEdit.text.toString()

            if (name.isNotEmpty()) {
                userManager.updateProfile(name, age, weight, height, selectedImageUri?.toString())
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                
                // Refresh Top Bar Profile Image
                val toolbarProfileImage = findViewById<ImageView>(R.id.toolbarProfileImage)
                setImageSafely(toolbarProfileImage, selectedImageUri?.toString())

                loadFragment(R.id.nav_profile) // Refresh profile view
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
