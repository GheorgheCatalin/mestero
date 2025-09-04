package com.mestero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mestero.R
import com.mestero.data.models.BookingRequestModel
import com.mestero.data.models.RequestStatus
import com.mestero.databinding.ItemBookingRequestBinding
import com.mestero.utils.FormatUtils

class BookingRequestAdapter(
    private val onAcceptClick: (BookingRequestModel) -> Unit,
    private val onRejectClick: (BookingRequestModel) -> Unit,
    private val onCompleteClick: (BookingRequestModel) -> Unit,
    private val onHideClick: (BookingRequestModel) -> Unit,
    private val onListingClick: (BookingRequestModel) -> Unit = {}
) : ListAdapter<BookingRequestModel, BookingRequestAdapter.BookingViewHolder>(BookingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingRequestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookingViewHolder(
        private val binding: ItemBookingRequestBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: BookingRequestModel) {
            binding.apply {
                // Service info
                serviceTitle.text = booking.listingTitle
                // Make entire card clickable to navigate to listing details
                root.setOnClickListener { onListingClick(booking) }
                clientNameTextView.text = booking.clientName
                statusTextView.text = booking.statusDisplayText

                // Background color based on booking status
                statusTextView.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        booking.statusColor
                    )
                )

                // Client notes
                if (booking.notes.isNotBlank()) {
                    notesLayout.isVisible = true
                    notesTextView.text = booking.notes
                } else {
                    notesLayout.isVisible = false
                }

                // Contact info
                var hasContact = false
                if (booking.clientEmail.isNotBlank()) {
                    emailTextView.isVisible = true
                    val emailText = binding.root.context.getString(R.string.email_label) + booking.clientEmail
                    emailTextView.text = emailText
                    hasContact = true
                } else {
                    emailTextView.isVisible = false
                }
                if (booking.clientPhone.isNotBlank()) {
                    phoneTextView.isVisible = true
                    val phoneText = binding.root.context.getString(R.string.phone_label) + booking.clientPhone
                    phoneTextView.text = phoneText
                    hasContact = true
                } else {
                    phoneTextView.isVisible = false
                }

                contactLayout.isVisible = hasContact

                // Creation date
                val requestedOnText = binding.root.context.getString(R.string.requested_on) +
                        FormatUtils.formatBookingDate(booking.createdAt)
                createdAtTextView.text = requestedOnText

                // Action buttons based on status
                setActionButtonsBasedOnStatus(this, booking)

            }
        }

        private fun setActionButtonsBasedOnStatus(
            binding: ItemBookingRequestBinding,
            booking: BookingRequestModel)
        {
            binding.apply {
                when (booking.status) {
                    RequestStatus.PENDING -> {
                        actionButtonsLayout.isVisible = true
                        completeButton.isVisible = false
                        hideButton.isVisible = false

                        acceptButton.setOnClickListener { onAcceptClick(booking) }
                        rejectButton.setOnClickListener { onRejectClick(booking) }
                    }

                    RequestStatus.ACCEPTED -> {
                        actionButtonsLayout.isVisible = false
                        completeButton.isVisible = true
                        hideButton.isVisible = false

                        completeButton.setOnClickListener { onCompleteClick(booking) }
                    }

                    RequestStatus.REJECTED -> {
                        actionButtonsLayout.isVisible = false
                        completeButton.isVisible = false
                        hideButton.isVisible = true

                        hideButton.setOnClickListener { onHideClick(booking) }
                    }

                    RequestStatus.COMPLETED -> {
                        actionButtonsLayout.isVisible = false
                        completeButton.isVisible = false
                        hideButton.isVisible = true

                        hideButton.setOnClickListener { onHideClick(booking) }
                    }
                }
            }
        }
    }
}

class BookingDiffCallback : DiffUtil.ItemCallback<BookingRequestModel>() {
    override fun areItemsTheSame(
        oldItem: BookingRequestModel,
        newItem: BookingRequestModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: BookingRequestModel,
        newItem: BookingRequestModel
    ): Boolean {
        return oldItem == newItem
    }
} 