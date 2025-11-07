package com.example.opsc_companioncare
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore


class Signup : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var firstNameInput: TextInputEditText
    private lateinit var usernameInput: TextInputEditText
    private lateinit var createPasswordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var agreeCheckBox: CheckBox
    private lateinit var signUpBtn: Button
    private lateinit var cancelImgBtn: ImageButton
    private lateinit var loadingImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        // Initialize views
        firstNameInput = findViewById(R.id.firstname)
        usernameInput = findViewById(R.id.username)
        createPasswordInput = findViewById(R.id.createpassword)
        confirmPasswordInput = findViewById(R.id.confirmpassword)
        emailInput = findViewById(R.id.email)
        agreeCheckBox = findViewById(R.id.agreeCheckBox)
        signUpBtn = findViewById(R.id.loginbtn)
        cancelImgBtn = findViewById(R.id.cancelimg)
        loadingImage = findViewById(R.id.loadingImage)

        loadingImage.visibility = ImageView.GONE

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        signUpBtn.setOnClickListener {
            saveUserToFirestore()
        }

        cancelImgBtn.setOnClickListener {
            Redirect()
        }
    }

    private fun Redirect(){
        try {
            startActivity(Intent(this, Login::class.java))
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserToFirestore() {
        val firstName = firstNameInput.text.toString().trim()
        val username = usernameInput.text.toString().trim()
        val password = createPasswordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()
        val email = emailInput.text.toString().trim()

        // Validation
        if (firstName.isEmpty()) {
            firstNameInput.error = "First name is required"
            return
        }

        if (username.isEmpty()) {
            usernameInput.error = "Username is required"
            return
        }

        if (password.isEmpty() && password.length > 8) {
            createPasswordInput.error = "Password is required"
            return
        }
        else {
            createPasswordInput.error = "Password is too short!"
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.error = "Please confirm your password"
            return
        }

        if (password != confirmPassword) {
            confirmPasswordInput.error = "Passwords do not match"
            return
        }

        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Please enter a valid email address"
            return
        }

        if (!agreeCheckBox.isChecked) {
            Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if username already exists
        signUpBtn.isEnabled = false
        showLoadingAnimation()

        db.collection("users").document(username).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Username already exists
                    hideLoadingAnimation()
                    signUpBtn.isEnabled = true
                    usernameInput.error = "Username already exists. Please choose a different one."
                    return@addOnSuccessListener
                }

                // Username is available, create user
                val user = hashMapOf(
                    "firstName" to firstName,
                    "username" to username,
                    "password" to password,
                    "email" to email,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )

                // Save to Firestore using username as document ID
                db.collection("users").document(username)
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "User registered successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Create initial user data structure
                        initializeUserData(username)

                        clearForm()
                        startActivity(Intent(this, Dashboard::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        hideLoadingAnimation()
                        signUpBtn.isEnabled = true
                        Toast.makeText(
                            this,
                            "Error registering user: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                hideLoadingAnimation()
                signUpBtn.isEnabled = true
                Toast.makeText(this, "Error checking username: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initializeUserData(username: String) {
        // Initialize user-specific collections with empty data
        val userSettings = hashMapOf(
            "theme" to "light",
            "notifications" to true,
            "language" to "en"
        )

        val userProfile = hashMapOf(
            "displayName" to firstNameInput.text.toString().trim(),
            "bio" to "",
            "avatarUrl" to ""
        )

        // Create user settings
        db.collection("userSettings").document(username)
            .set(userSettings)

        // Create user profile
        db.collection("userProfiles").document(username)
            .set(userProfile)

        // Initialize empty collections for user data
        val emptyTasks = hashMapOf<String, Any>()
        db.collection("userTasks").document(username)
            .set(emptyTasks)

        val emptyNotes = hashMapOf<String, Any>()
        db.collection("userNotes").document(username)
            .set(emptyNotes)
    }

    private fun showLoadingAnimation() {
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation)
        loadingImage.visibility = ImageView.VISIBLE
        loadingImage.startAnimation(rotateAnimation)
    }

    private fun hideLoadingAnimation() {
        loadingImage.clearAnimation()
        loadingImage.visibility = ImageView.GONE
    }

    private fun clearForm() {
        firstNameInput.text?.clear()
        usernameInput.text?.clear()
        createPasswordInput.text?.clear()
        confirmPasswordInput.text?.clear()
        emailInput.text?.clear()
        agreeCheckBox.isChecked = false
    }
}