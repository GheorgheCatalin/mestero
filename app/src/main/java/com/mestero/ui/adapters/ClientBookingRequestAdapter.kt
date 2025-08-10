package com.mestero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mestero.data.models.BookingRequestModel
import com.mestero.data.models.RequestStatus
import com.mestero.databinding.ItemBookingRequestClientBinding
import com.mestero.utils.FormatUtils

class ClientBookingRequestAdapter(
    private val onReviewClick: (BookingRequestModel) -> Unit = {},
    private val onContactClick: (String, String) -> Unit = { _, _ -> }, // email, phone
    private val onHideClick: (BookingRequestModel) -> Unit = {}
) : ListAdapter<BookingRequestModel, ClientBookingRequestAdapter.ClientBookingViewHolder>(
    ClientBookingDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientBookingViewHolder {
        val binding = ItemBookingRequestClientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ClientBookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientBookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClientBookingViewHolder(
        private val binding: ItemBookingRequestClientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: BookingRequestModel) {
            binding.apply {
                serviceTitle.text = booking.listingTitle
                providerNameTextView.text =
                    booking.providerName.ifBlank { "Provider" } //TODO change
                statusTextView.text = booking.statusDisplayText

                // Set status background color based on booking status
                statusTextView.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context, booking.statusColor
                    )
                )

                // TODO - Price information (if available from listing)
                // might need to extend BookingRequestModel to include listing price
                // Hide for now until price is available
                priceLayout.isVisible = false

                // Client notes
                if (booking.notes.isNotBlank()) {
                    notesLayout.isVisible = true
                    notesTextView.text = booking.notes
                } else {
                    notesLayout.isVisible = false
                }

                // Provider response
                if (booking.providerNotes.isNotBlank()) {
                    providerResponseLayout.isVisible = true
                    providerResponseTextView.text = booking.providerNotes
                } else {
                    providerResponseLayout.isVisible = false
                }

                // Contact information - shown if accepted
                displayContanctInfo(booking)

                // Dates
                createdAtTextView.text =
                    "Requested on ${FormatUtils.formatBookingDate(booking.createdAt)}"

                if (booking.completedAt != null && booking.status == RequestStatus.COMPLETED) {
                    completedAtTextView.isVisible = true
                    completedAtTextView.text =
                        "Completed on ${FormatUtils.formatBookingDate(booking.completedAt)}"
                } else {
                    completedAtTextView.isVisible = false
                }

                // Status-specific actions
                setupStatusActions(booking)
            }
        }

        private fun displayContanctInfo(booking: BookingRequestModel) {
            binding.apply {
                if (booking.status == RequestStatus.ACCEPTED &&
                    (booking.providerEmail.isNotBlank() || booking.providerPhone.isNotBlank())
                ) {

                    contactLayout.isVisible = true

                    if (booking.providerEmail.isNotBlank()) {
                        emailTextView.isVisible = true
                        emailTextView.text = booking.providerEmail
                        emailTextView.setOnClickListener {
                            onContactClick(booking.providerEmail, "email") // TODO enum for type
                        }
                    } else {
                        emailTextView.isVisible = false
                    }

                    if (booking.providerPhone.isNotBlank()) {
                        phoneTextView.isVisible = true
                        phoneTextView.text = booking.providerPhone
                        phoneTextView.setOnClickListener {
                            onContactClick(booking.providerPhone, "phone")
                        }
                    } else {
                        phoneTextView.isVisible = false
                    }

                } else {
                    contactLayout.isVisible = false
                }
            }
        }

        private fun setupStatusActions(booking: BookingRequestModel) {
            binding.apply {
                when (booking.status) {
                    RequestStatus.PENDING -> {
                        statusActionLayout.isVisible = true
                        statusActionText.isVisible = true
                        reviewButton.isVisible = false
                        hideButton.isVisible = false

                        statusActionText.text = "Waiting for provider response..."
                    }

                    RequestStatus.ACCEPTED -> {
                        statusActionLayout.isVisible = true
                        statusActionText.isVisible = true
                        reviewButton.isVisible = false
                        hideButton.isVisible = false

                        statusActionText.text =
                            "Request accepted! You can now contact the provider."  // TODO change text
                    }

                    RequestStatus.REJECTED -> {
                        statusActionLayout.isVisible = true
                        statusActionText.isVisible = true
                        reviewButton.isVisible = false
                        hideButton.isVisible = true

                        statusActionText.text = "Request was declined by the provider."
                        hideButton.setOnClickListener { onHideClick(booking) }
                    }

                    RequestStatus.COMPLETED -> {
                        statusActionLayout.isVisible = true
                        statusActionText.isVisible = false
                        reviewButton.isVisible = true
                        hideButton.isVisible = true

                        reviewButton.setOnClickListener { onReviewClick(booking) }
                        hideButton.setOnClickListener { onHideClick(booking) }
                    }
                }
            }
        }


    }
}

class ClientBookingDiffCallback : DiffUtil.ItemCallback<BookingRequestModel>() {
    override fun areItemsTheSame(
        oldItem: BookingRequestModel, newItem: BookingRequestModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: BookingRequestModel, newItem: BookingRequestModel
    ): Boolean {
        return oldItem == newItem
    }
} 