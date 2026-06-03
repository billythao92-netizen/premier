package com.photoshare.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.photoshare.app.data.Channel
import com.photoshare.app.data.RecipientChannel
import com.photoshare.app.databinding.ActivityCountdownBinding

class CountdownActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountdownBinding
    private var countDownTimer: CountDownTimer? = null
    private var photoUriString: String? = null
    private var selectedPairs: ArrayList<RecipientChannel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountdownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photoUriString = intent.getStringExtra(RecipientsActivity.EXTRA_PHOTO_URI)
        @Suppress("UNCHECKED_CAST", "DEPRECATION")
        selectedPairs = intent.getSerializableExtra(RecipientsActivity.EXTRA_SELECTED_PAIRS) as? ArrayList<RecipientChannel>
        val countdownSeconds = intent.getIntExtra(RecipientsActivity.EXTRA_COUNTDOWN, SettingsActivity.DEFAULT_COUNTDOWN)

        if (photoUriString == null || selectedPairs.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.error_photo), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Show photo as background
        val photoUri = Uri.parse(photoUriString)
        try {
            binding.ivPhoto.setImageURI(photoUri)
        } catch (e: Exception) {
            // If image can't be loaded, use solid background
        }

        // Start countdown
        startCountdown(countdownSeconds)

        binding.btnCancel.setOnClickListener {
            countDownTimer?.cancel()
            finish()
        }
    }

    private fun startCountdown(seconds: Int) {
        binding.tvCountdown.text = seconds.toString()

        countDownTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt() + 1
                binding.tvCountdown.text = secondsLeft.toString()
            }

            override fun onFinish() {
                binding.tvCountdown.text = "0"
                sendToAllRecipients()
                finish()
            }
        }.start()
    }

    private fun sendToAllRecipients() {
        val pairs = selectedPairs ?: return
        val photoUri = Uri.parse(photoUriString)

        for (pair in pairs) {
            try {
                when (pair.channel) {
                    Channel.SMS -> sendViaSms(pair.recipient.phone, photoUri)
                    Channel.EMAIL -> sendViaEmail(pair.recipient.email, photoUri)
                    Channel.WHATSAPP -> sendViaWhatsApp(pair.recipient.phone, photoUri)
                    Channel.MESSENGER -> sendViaMessenger(photoUri)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.error_send, pair.recipient.name, pair.channel.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendViaSms(phone: String, photoUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, photoUri)
            putExtra("address", phone)
            setPackage("com.android.mms")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Grant URI permissions to all apps that can handle this
        grantUriPermissionToAllResolvers(intent, photoUri)

        // Fallback to generic SMS/MMS chooser if default MMS app not available
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            val fallback = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phone")
                putExtra("sms_body", getString(R.string.sms_body))
            }
            if (fallback.resolveActivity(packageManager) != null) {
                startActivity(fallback)
            }
        }
    }

    private fun sendViaEmail(email: String, photoUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            putExtra(Intent.EXTRA_STREAM, photoUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        grantUriPermissionToAllResolvers(intent, photoUri)
        val chooser = Intent.createChooser(intent, getString(R.string.choose_email_app))
        if (chooser.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        }
    }

    private fun sendViaWhatsApp(phone: String, photoUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, photoUri)
            putExtra(Intent.EXTRA_TEXT, getString(R.string.whatsapp_body))
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        grantUriPermissionToAllResolvers(intent, photoUri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, getString(R.string.error_whatsapp_not_installed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendViaMessenger(photoUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, photoUri)
            setPackage("com.facebook.orca")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        grantUriPermissionToAllResolvers(intent, photoUri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, getString(R.string.error_messenger_not_installed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun grantUriPermissionToAllResolvers(intent: Intent, uri: Uri) {
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
