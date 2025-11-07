package com.example.opsc_companioncare

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.SharedPreferences
import android.widget.RadioButton
import android.widget.RadioGroup
import java.util.Locale
import android.content.res.Configuration
import android.content.res.Resources
import android.media.Image
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class Settings : ComponentActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var Profilebtn: ImageButton
    private lateinit var homebtn: ImageButton
    private lateinit var accdelte: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        Profilebtn = findViewById(R.id.profilebtn)
        homebtn = findViewById(R.id.home)
        accdelte = findViewById(R.id.deleteaccbtn)
        // set your layout containing the radio buttons


        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        radioGroup = findViewById(R.id.radioGroupLanguages)

        // Set previously saved language selection on load
        val savedLanguage = sharedPreferences.getString("selected_language", "en")
        setLocale(savedLanguage ?: "en")
        checkSelectedRadio(savedLanguage)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            val languageCode = when (selectedRadioButton.id) {
                R.id.btnEnglish -> "en"
                R.id.btnAfrikaans -> "af"
                R.id.btnIsiZulu -> "zu"
                else -> "en"
            }
            // Save language preference
            sharedPreferences.edit().putString("selected_language", languageCode).apply()

            // Change app language
            setLocale(languageCode)

            // Reload activity to apply changes (you can also update UI dynamically if preferred)
            recreate()
        }
        accdelte.setOnClickListener {
            redirectdeleteaccount()
        }
        homebtn.setOnClickListener {
            redirecthome()
        }

        Profilebtn.setOnClickListener {
            redirectprofile()
        }
    }
    private fun redirectdeleteaccount(){
        try {
            startActivity(Intent(this, account_deletion::class.java))
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

private fun redirectprofile(){
    try {
        startActivity(Intent(this, profile::class.java))
        finish() // Optional: close current activity
    } catch (e: Exception) {
        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
    }
}
private fun redirecthome(){
    try {
        startActivity(Intent(this, Dashboard::class.java))
        finish() // Optional: close current activity
    } catch (e: Exception) {
        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
    }
}
    private fun checkSelectedRadio(languageCode: String?) {
        when (languageCode) {
            "en" -> radioGroup.check(R.id.btnEnglish)
            "af" -> radioGroup.check(R.id.btnAfrikaans)
            "zu" -> radioGroup.check(R.id.btnIsiZulu)
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}