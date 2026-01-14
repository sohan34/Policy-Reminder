package com.example.anandbhavan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.anandbhavan.util.NotificationHelper
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // Register permission launcher for Android 13+
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Notifications enabled!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications denied!", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1️⃣ Request notification permission (Android 13+)
        checkNotificationPermission()

        // 2️⃣ Create notification channel
        NotificationHelper.createNotificationChannel(this)

        // 3️⃣ Schedule daily reminders
        scheduleReminders()

        // 4️⃣ Setup category cards
        findViewById<android.view.View>(R.id.cardHealthcare).setOnClickListener {
            openCategory("Healthcare")
        }

        findViewById<android.view.View>(R.id.cardVehicles).setOnClickListener {
            openCategory("Vehicles")
        }

        findViewById<android.view.View>(R.id.cardInsurance).setOnClickListener {
            openCategory("Insurance")
        }
    }

    // Request notification permission at runtime
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Already granted
                    Toast.makeText(this, "Notifications already enabled", Toast.LENGTH_SHORT).show()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show explanation then request
                    Toast.makeText(this, "Please enable notifications to get reminders", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Directly request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    // Navigate to selected policy category
    private fun openCategory(category: String) {
        val intent = Intent(this, PolicyListActivity::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }

    // Schedule daily reminders using WorkManager
    private fun scheduleReminders() {
        val workRequest = PeriodicWorkRequestBuilder<com.example.anandbhavan.worker.ReminderWorker>(
            1, TimeUnit.DAYS
        )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyReminderCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
