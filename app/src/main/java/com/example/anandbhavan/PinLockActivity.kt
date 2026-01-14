package com.example.anandbhavan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.Toast

class PinLockActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_lock)

        val pinInput = findViewById<TextInputEditText>(R.id.pinInput)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        if (EncryptedPrefs.getPIN(this) == null) {
            EncryptedPrefs.savePIN(this, "1229")
        }

        btnSubmit.setOnClickListener {
            val savedPin = EncryptedPrefs.getPIN(this)
            if (pinInput.text.toString() == savedPin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
