package com.example.opsc_companioncare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_companioncare.databinding.ActivitySignupBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.jvm.java

class Login : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginBtn: AppCompatButton
    private lateinit var signUpRedirect: TextView
    private lateinit var loadingImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize all views
        usernameInput = findViewById(R.id.username)
        passwordInput = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginbtn)
        signUpRedirect = findViewById(R.id.sign_text)
        loadingImage = findViewById(R.id.loadingImage)

        loadingImage.visibility = ImageView.GONE

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loginBtn.setOnClickListener {
            authenticateUser()
        }

        signUpRedirect.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun authenticateUser() {
        val username = usernameInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        loginBtn.isEnabled = false
        showLoadingAnimation()

        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                hideLoadingAnimation()
                loginBtn.isEnabled = true

                if (documents.isEmpty) {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val document = documents.documents[0]
                val storedPassword = document.getString("password")

                if (password == storedPassword) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, Dashboard::class.java))

                    finish()
                } else {
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                hideLoadingAnimation()
                loginBtn.isEnabled = true
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
}