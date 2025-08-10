package com.mestero.ui.dashboard.listingDetail

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.firebase.Timestamp
import com.mestero.data.models.ListingModel
import com.mestero.databinding.DialogBookingRequestBinding

class BookingRequestDialog(
    context: Context,
    private val listing: ListingModel,
    private val onRequestSent: (notes: String) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogBookingRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = DialogBookingRequestBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        
        setupDialog()
        setupClickListeners()
    }

    private fun setupDialog() {
        binding.serviceTitle.text = "Service: ${listing.title}"
        
        // Set dialog properties
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupClickListeners() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.sendRequestButton.setOnClickListener {
            onRequestSent(
                binding.notesEditText.text.toString().trim()
            )
            dismiss()
        }
    }


    //todo delete
//    private fun sendRequest() {
//        val notes = binding.notesEditText.text.toString().trim()
//        onRequestSent(notes)
//        dismiss()
//    }
} 