package com.example.opsc_companioncare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class account_deletion : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var btnDeleteAccount: Button
    private lateinit var btnCancel: Button
    private lateinit var tvWarning: TextView
    private lateinit var btnProfile: ImageButton
    private lateinit var btnHome: ImageButton
    private lateinit var btnHistory: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_deletion)

        // Get username from intent
        val username = intent.getStringExtra("USERNAME") ?: ""

        // Initialize views
        btnDeleteAccount = findViewById(R.id.deletebtn)
        btnCancel = findViewById(R.id.cancelbtn)
        tvWarning = findViewById(R.id.tvWarning)
        btnProfile = findViewById(R.id.profilebtn)
        btnHome = findViewById(R.id.home)
        btnHistory = findViewById(R.id.history)

        // Update warning text with username
        tvWarning.text = "You are about to delete account: $username\nThis action cannot be undone!"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up click listeners
        btnDeleteAccount.setOnClickListener {
            showDeleteConfirmationDialog(username)
        }

        btnCancel.setOnClickListener {
            finish()
        }

        // Bottom navigation listeners
        btnProfile.setOnClickListener {
            // Navigate to profile activity
            val intent = Intent(this, profile::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
            finish()
        }

        btnHome.setOnClickListener {
            // Navigate to home activity
            val intent = Intent(this, Dashboard::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
            finish()
        }

        btnHistory.setOnClickListener {
            // Navigate to history/settings activity
            val intent = Intent(this, Settings::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
            finish()
        }
    }

    private fun showDeleteConfirmationDialog(username: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Account Deletion")
            .setMessage("Are you sure you want to delete your account '$username'? This action is permanent and cannot be undone!")
            .setPositiveButton("Delete") { dialog, which ->
                deleteUserAccount(username)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteUserAccount(username: String) {
        if (username.isEmpty()) {
            Toast.makeText(this, "Error: User not identified", Toast.LENGTH_SHORT).show()
            return
        }

        btnDeleteAccount.isEnabled = false

        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "User account not found", Toast.LENGTH_SHORT).show()
                    btnDeleteAccount.isEnabled = true
                    return@addOnSuccessListener
                }

                // Delete all documents that match the username
                val batch = db.batch()
                for (document in documents) {
                    batch.delete(document.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                        // Redirect to login screen and clear back stack
                        val intent = Intent(this, Login::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        btnDeleteAccount.isEnabled = true
                        Toast.makeText(this, "Failed to delete account: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                btnDeleteAccount.isEnabled = true
                Toast.makeText(this, "Error finding user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}