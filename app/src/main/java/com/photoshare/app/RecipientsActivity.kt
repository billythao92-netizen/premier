package com.photoshare.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.photoshare.app.data.Channel
import com.photoshare.app.data.Recipient
import com.photoshare.app.data.RecipientChannel
import com.photoshare.app.databinding.ActivityRecipientsBinding

class RecipientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipientsBinding
    private lateinit var adapter: RecipientAdapter
    private val selectedPairs = mutableSetOf<RecipientChannel>()

    companion object {
        const val EXTRA_PHOTO_URI = "extra_photo_uri"
        const val EXTRA_SELECTED_PAIRS = "extra_selected_pairs"
        const val EXTRA_COUNTDOWN = "extra_countdown"

        val RECIPIENTS = listOf(
            Recipient(
                name = "Maman",
                phone = "+15551234567",
                email = "maman@example.com"
            ),
            Recipient(
                name = "Papa",
                phone = "+15559876543",
                email = "papa@example.com"
            ),
            Recipient(
                name = "Grand-Mère",
                phone = "+15555550001",
                email = "grandmere@example.com"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUri = intent.getStringExtra(EXTRA_PHOTO_URI)
        if (photoUri == null) {
            Toast.makeText(this, getString(R.string.error_photo), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        adapter = RecipientAdapter(RECIPIENTS) { recipient, channel, isSelected ->
            val pair = RecipientChannel(recipient, channel)
            if (isSelected) {
                selectedPairs.add(pair)
            } else {
                selectedPairs.remove(pair)
            }
        }

        binding.rvRecipients.layoutManager = LinearLayoutManager(this)
        binding.rvRecipients.adapter = adapter

        binding.btnSend.setOnClickListener {
            if (selectedPairs.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_no_selection), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE)
            val countdown = prefs.getInt(SettingsActivity.KEY_COUNTDOWN, SettingsActivity.DEFAULT_COUNTDOWN)

            val pairsList = ArrayList(selectedPairs)
            val intent = Intent(this, CountdownActivity::class.java).apply {
                putExtra(EXTRA_PHOTO_URI, photoUri)
                putExtra(EXTRA_SELECTED_PAIRS, pairsList)
                putExtra(EXTRA_COUNTDOWN, countdown)
            }
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
