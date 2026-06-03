package com.photoshare.app

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photoshare.app.data.Channel
import com.photoshare.app.data.Recipient
import com.photoshare.app.databinding.ItemRecipientBinding

class RecipientAdapter(
    private val recipients: List<Recipient>,
    private val onChannelToggle: (Recipient, Channel, Boolean) -> Unit
) : RecyclerView.Adapter<RecipientAdapter.RecipientViewHolder>() {

    // Maps recipient name + channel to selected state
    private val selectedStates = mutableMapOf<String, Boolean>()

    private fun stateKey(recipient: Recipient, channel: Channel) = "${recipient.name}_${channel.name}"

    inner class RecipientViewHolder(val binding: ItemRecipientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipient: Recipient) {
            binding.tvName.text = recipient.name
            // Set avatar initial letter
            binding.tvAvatar.text = recipient.name.firstOrNull()?.uppercase() ?: "?"

            // Avatar background color based on name hash
            val avatarColors = listOf(
                0xFF4FC3F7.toInt(), // sky blue
                0xFFFF6B35.toInt(), // orange
                0xFF66BB6A.toInt(), // green
                0xFFAB47BC.toInt(), // purple
                0xFFFF7043.toInt()  // deep orange
            )
            val colorIndex = Math.abs(recipient.name.hashCode()) % avatarColors.size
            val circle = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(avatarColors[colorIndex])
            }
            binding.tvAvatar.background = circle

            // Setup channel buttons
            setupChannelButton(binding.btnSms, recipient, Channel.SMS)
            setupChannelButton(binding.btnEmail, recipient, Channel.EMAIL)
            setupChannelButton(binding.btnWhatsapp, recipient, Channel.WHATSAPP)
            setupChannelButton(binding.btnMessenger, recipient, Channel.MESSENGER)
        }

        private fun setupChannelButton(
            button: android.widget.TextView,
            recipient: Recipient,
            channel: Channel
        ) {
            val key = stateKey(recipient, channel)
            val isSelected = selectedStates[key] ?: false
            updateButtonAppearance(button, channel, isSelected)

            button.setOnClickListener {
                val currentlySelected = selectedStates[key] ?: false
                val newState = !currentlySelected
                selectedStates[key] = newState
                updateButtonAppearance(button, channel, newState)
                onChannelToggle(recipient, channel, newState)
            }
        }

        private fun updateButtonAppearance(
            button: android.widget.TextView,
            channel: Channel,
            isSelected: Boolean
        ) {
            if (isSelected) {
                val selectedColors = mapOf(
                    Channel.SMS to 0xFF1565C0.toInt(),
                    Channel.EMAIL to 0xFFE53935.toInt(),
                    Channel.WHATSAPP to 0xFF2E7D32.toInt(),
                    Channel.MESSENGER to 0xFF1565C0.toInt()
                )
                button.setBackgroundColor(selectedColors[channel] ?: 0xFF1565C0.toInt())
                button.setTextColor(Color.WHITE)
                button.alpha = 1.0f
            } else {
                val unselectedColors = mapOf(
                    Channel.SMS to 0xFF90CAF9.toInt(),
                    Channel.EMAIL to 0xFFEF9A9A.toInt(),
                    Channel.WHATSAPP to 0xFFA5D6A7.toInt(),
                    Channel.MESSENGER to 0xFF90CAF9.toInt()
                )
                button.setBackgroundColor(unselectedColors[channel] ?: 0xFF90CAF9.toInt())
                button.setTextColor(Color.WHITE)
                button.alpha = 0.7f
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipientViewHolder {
        val binding = ItemRecipientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecipientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipientViewHolder, position: Int) {
        holder.bind(recipients[position])
    }

    override fun getItemCount() = recipients.size
}
