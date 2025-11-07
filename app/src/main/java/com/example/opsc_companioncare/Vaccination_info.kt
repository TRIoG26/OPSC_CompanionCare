package com.example.opsc_companioncare

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class Vaccination_info : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var droplist: Spinner
    private lateinit var vaccinename: TextInputEditText
    private lateinit var dategiven: TextInputEditText
    private lateinit var nextdue: TextInputEditText
    private lateinit var vetname: TextInputEditText
    private lateinit var vetername: TextInputEditText
    private lateinit var notes: TextInputEditText
    private lateinit var cancelbtn: Button
    private lateinit var savebtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vaccination_info)

        // Initialize views
        droplist = findViewById(R.id.spinner_category)
        vaccinename = findViewById(R.id.Vaccine_name)
        dategiven = findViewById(R.id.date_given)
        nextdue = findViewById(R.id.Next_due_date)
        vetname = findViewById(R.id.Vet_name)
        vetername = findViewById(R.id.veterinarian_Name)
        notes = findViewById(R.id.Vac_note)
        cancelbtn = findViewById(R.id.Cancelbtn)
        savebtn = findViewById(R.id.savebtn)

        // Set up the Spinner programmatically
        setuppetSpinner()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        savebtn.setOnClickListener {
            formcreation()
        }

        cancelbtn.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }
    }

    private fun setuppetSpinner() {
        // Create language list programmatically
        val pets = arrayOf("Dog", "Cat", "Rabbit","Fish")

        // Create ArrayAdapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pets)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        droplist.adapter = adapter
    }

    private fun formcreation() {
        // Get selected pet from Spinner
        val selectedpet = droplist.selectedItem.toString().trim()
        val vac_name = vaccinename.text.toString().trim()
        val date = dategiven.text.toString().trim()
        val nextdate = nextdue.text.toString().trim()
        val vet_name = vetname.text.toString().trim()
        val veter_name = vetername.text.toString().trim()
        val notess = notes.text.toString().trim()

        if (selectedpet.isEmpty() || vac_name.isEmpty() || date.isEmpty() ||
            nextdate.isEmpty() || veter_name.isEmpty() || vet_name.isEmpty() || notess.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val vaccinedetails = hashMapOf(
            "pet" to selectedpet,
            "VaccineName" to vac_name,
            "dategiven" to date,
            "nextdate" to nextdate,
            "vet_name" to vet_name,
            "vetername" to veter_name,
            "notes" to notess
        )

        savebtn.isEnabled = false

        db.collection("vaccine_details")
            .add(vaccinedetails)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Data successfully created!", Toast.LENGTH_SHORT).show()
                clearForm()
                startActivity(Intent(this, Dashboard::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "File creating failed retry again: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                savebtn.isEnabled = true
            }
    }

    private fun clearForm() {
        // Reset spinner to first item
        droplist.setSelection(0)
        vaccinename.text?.clear()
        dategiven.text?.clear()
        nextdue.text?.clear()
        vetname.text?.clear()
        vetername.text?.clear()
        notes.text?.clear()
    }
}