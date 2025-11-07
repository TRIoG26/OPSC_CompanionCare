package com.example.opsc_companioncare

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Dashboard : AppCompatActivity() {
    private lateinit var profilebtn: ImageButton
    private lateinit var vaccinebtn: ImageButton
    private lateinit var hrecordbtn: ImageButton
    private lateinit var visitbtn: ImageButton
    private lateinit var dtracking: ImageButton
    private lateinit var chatbtn: ImageButton
    private lateinit var groombtn: ImageButton
    private lateinit var toybtn: ImageButton
    private lateinit var foodbtn: ImageButton

    private lateinit var settingsbtn: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        vaccinebtn = findViewById(R.id.vaccine)
        hrecordbtn = findViewById(R.id.Hrecord)
        visitbtn = findViewById(R.id.visit)
        dtracking = findViewById(R.id.diet)
        chatbtn = findViewById(R.id.chatbot)
        groombtn = findViewById(R.id.groom)
        toybtn = findViewById(R.id.toys)
        foodbtn = findViewById(R.id.food)
        profilebtn = findViewById(R.id.profilebtn)
        settingsbtn = findViewById(R.id.settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        vaccinebtn.setOnClickListener {
            vaccinenav()
        }
        settingsbtn.setOnClickListener {
            redirectSettings()
        }
        visitbtn.setOnClickListener {
            redirectVisitclinic()
        }
    }
    private fun redirectVisitclinic()
    {
        try {
            startActivity(Intent(this, Vet_visits::class.java))
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun vaccinenav(){
        try {
            startActivity(Intent(this, Vaccination_info::class.java))
            finish() // Optional: close current activity
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun redirectSettings()
    {
        try {
            startActivity(Intent(this, com.example.opsc_companioncare.Settings::class.java))
            finish() // Optional: close current activity
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}