package com.example.opsc_companioncare

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class Vet_visits : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var droplist: Spinner
    private lateinit var dateOfVisitLayout: TextInputLayout
    private lateinit var vetNameLayout: TextInputLayout
    private lateinit var vaccinationNameLayout: TextInputLayout
    private lateinit var reasonForVisitLayout: TextInputLayout
    private lateinit var diagnosisLayout: TextInputLayout
    private lateinit var treatmentLayout: TextInputLayout
    private lateinit var followUpAppointmentLayout: TextInputLayout

    private lateinit var dateOfVisit: TextInputEditText
    private lateinit var vetName: TextInputEditText
    private lateinit var vaccinationName: TextInputEditText
    private lateinit var reasonForVisit: TextInputEditText
    private lateinit var diagnosis: TextInputEditText
    private lateinit var treatment: TextInputEditText
    private lateinit var followUpAppointment: TextInputEditText

    private lateinit var savebtn: Button
    private lateinit var profilebtn: ImageButton
    private lateinit var homebtn: ImageButton
    private lateinit var settingsbtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vet_visits)

        // Initialize views
        initializeViews()

        // Set up the Spinner programmatically
        setupPetSpinner()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        savebtn.setOnClickListener {
            saveVetVisit()
        }

        // Set up bottom navigation
        setupBottomNavigation()
    }

    private fun initializeViews() {
        droplist = findViewById(R.id.spinner_category)

        // Initialize TextInputLayouts
        dateOfVisitLayout = findViewById(R.id.DateOfVisit)
        vetNameLayout = findViewById(R.id.VetName)
        vaccinationNameLayout = findViewById(R.id.VaccinationName)
        reasonForVisitLayout = findViewById(R.id.ReasonForVisit)
        diagnosisLayout = findViewById(R.id.VeterinarianName) // Note: Using VeterinarianName for Diagnosis based on your XML
        treatmentLayout = findViewById(R.id.Treatment)
        followUpAppointmentLayout = findViewById(R.id.FollowUpAppointment)

        // Initialize TextInputEditText from the layouts
        dateOfVisit = dateOfVisitLayout.editText as TextInputEditText
        vetName = vetNameLayout.editText as TextInputEditText
        vaccinationName = vaccinationNameLayout.editText as TextInputEditText
        reasonForVisit = reasonForVisitLayout.editText as TextInputEditText
        diagnosis = diagnosisLayout.editText as TextInputEditText
        treatment = treatmentLayout.editText as TextInputEditText
        followUpAppointment = followUpAppointmentLayout.editText as TextInputEditText

        savebtn = findViewById(R.id.savebtn)
        profilebtn = findViewById(R.id.profilebtn)
        homebtn = findViewById(R.id.home)
        settingsbtn = findViewById(R.id.settings)
    }

    private fun setupPetSpinner() {
        // Create pet list programmatically
        val pets = arrayOf("Dog", "Cat", "Rabbit", "Fish")

        // Create ArrayAdapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pets)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        droplist.adapter = adapter
    }

    private fun setupBottomNavigation() {
        profilebtn.setOnClickListener {
            startActivity(Intent(this, profile::class.java))
            finish()
        }

        homebtn.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }

        settingsbtn.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
            finish()
        }
    }

    private fun saveVetVisit() {
        // Get selected pet from Spinner
        val selectedPet = droplist.selectedItem.toString().trim()
        val visitDate = dateOfVisit.text.toString().trim()
        val vetNameText = vetName.text.toString().trim()
        val vaccinationNameText = vaccinationName.text.toString().trim()
        val reasonForVisitText = reasonForVisit.text.toString().trim()
        val diagnosisText = diagnosis.text.toString().trim()
        val treatmentText = treatment.text.toString().trim()
        val followUpDate = followUpAppointment.text.toString().trim()

        // Validate required fields (adjust required fields as needed)
        if (selectedPet.isEmpty() || visitDate.isEmpty() || vetNameText.isEmpty() ||
            reasonForVisitText.isEmpty() || diagnosisText.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val vetVisitDetails = hashMapOf(
            "pet" to selectedPet,
            "visitDate" to visitDate,
            "vetName" to vetNameText,
            "vaccinationName" to vaccinationNameText,
            "reasonForVisit" to reasonForVisitText,
            "diagnosis" to diagnosisText,
            "treatment" to treatmentText,
            "followUpDate" to followUpDate,
            "timestamp" to System.currentTimeMillis() // Add timestamp for sorting
        )

        savebtn.isEnabled = false

        db.collection("vet_visits")
            .add(vetVisitDetails)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Vet visit recorded successfully!", Toast.LENGTH_SHORT).show()
                clearForm()
                startActivity(Intent(this, Dashboard::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to save vet visit: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                savebtn.isEnabled = true
            }
    }

    private fun clearForm() {
        // Reset spinner to first item
        droplist.setSelection(0)
        dateOfVisit.text?.clear()
        vetName.text?.clear()
        vaccinationName.text?.clear()
        reasonForVisit.text?.clear()
        diagnosis.text?.clear()
        treatment.text?.clear()
        followUpAppointment.text?.clear()
    }
}