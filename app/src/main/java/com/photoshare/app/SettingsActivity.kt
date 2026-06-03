package com.photoshare.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.photoshare.app.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    companion object {
        const val PREFS_NAME = "photoshare_prefs"
        const val KEY_COUNTDOWN = "countdown_seconds"
        const val DEFAULT_COUNTDOWN = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val currentCountdown = prefs.getInt(KEY_COUNTDOWN, DEFAULT_COUNTDOWN)
        binding.etCountdown.setText(currentCountdown.toString())

        binding.btnSaveSettings.setOnClickListener {
            val value = binding.etCountdown.text.toString().toIntOrNull()
            if (value == null || value < 1 || value > 60) {
                Toast.makeText(this, getString(R.string.settings_invalid_countdown), Toast.LENGTH_SHORT).show()
            } else {
                prefs.edit().putInt(KEY_COUNTDOWN, value).apply()
                Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
