package com.example.anandbhavan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Animation
        val title = findViewById<TextView>(R.id.appTitle)
        val subtitle = findViewById<TextView>(R.id.appSubtitle)
        val footer = findViewById<TextView>(R.id.footer)

        val fadeIn = AlphaAnimation(0.0f, 1.0f)
        fadeIn.duration = 1000
        
        title.startAnimation(fadeIn)
        subtitle.startAnimation(fadeIn)
        footer.startAnimation(fadeIn)

        // Delay and Navigation
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is logged in or go to PinLock (Here we always go to PinLock as per requirements)
            startActivity(Intent(this, PinLockActivity::class.java))
            finish()
        }, 2500) // 2.5 seconds delay
    }
}
