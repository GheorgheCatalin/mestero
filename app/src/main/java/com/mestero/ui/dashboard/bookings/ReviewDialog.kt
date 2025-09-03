package com.mestero.ui.dashboard.bookings

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.RatingBar
import androidx.core.view.isVisible
import com.mestero.R
import com.mestero.data.models.BookingRequestModel
import com.mestero.data.models.ReviewModel
import com.mestero.databinding.DialogReviewBinding

class ReviewDialog(
    context: Context,
    private val booking: BookingRequestModel,
    private val onReviewSubmitted: (ReviewModel) -> Unit
) : Dialog(context) {
    private lateinit var binding: DialogReviewBinding

    private var serviceRating: Int = 0
    private var providerRating: Int = 0
    private var serviceComment: String = ""
    private var providerComment: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogReviewBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        setupDialog()
        setupViews()
        setupListeners()
        updateSubmitButton()
    }

    private fun setupDialog() {
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.95).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Set service info
        binding.serviceInfoTextView.text = context.getString(R.string.service_label, booking.listingTitle)
        binding.providerNameTextView.text =
            context.getString(R.string.provider_professionalism_question, booking.providerName)
    }

    private fun setupViews() {
        // Default state of button = disabled
        binding.submitButton.isEnabled = false
    }

    private fun setupListeners() {
        // listener for service rating
        binding.serviceRatingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                serviceRating = rating.toInt()
                updateSubmitButton()
            }

        // listener for provider rating
        binding.providerRatingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                providerRating = rating.toInt()
                updateSubmitButton()
            }

        // listener for service comm
        binding.serviceCommentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                serviceComment = s?.toString()?.trim() ?: ""
                updateSubmitButton()
            }
        })

        // listener for provider comm
        binding.providerCommentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                providerComment = s?.toString()?.trim() ?: ""
                updateSubmitButton()
            }
        })

        // listeners for bttns
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.submitButton.setOnClickListener {
            submitReview()
        }
    }

    private fun updateSubmitButton() {
        // Only ratings are required (comments are optional)
        val hasAnyRating = serviceRating > 0 || providerRating > 0

        // Update submit button state
        binding.submitButton.isEnabled = hasAnyRating

        // Update validation message
        if (hasAnyRating) {
            val hasInvalidComment = (serviceComment.isNotEmpty() && serviceComment.length < ReviewModel.MIN_COMMENT_LENGTH) ||
                    (providerComment.isNotEmpty() && providerComment.length < ReviewModel.MIN_COMMENT_LENGTH)
            
            if (hasInvalidComment) {
                binding.validationMessageTextView.isVisible = true
                binding.validationMessageTextView.text = getValidationMessage()
            } else {
                binding.validationMessageTextView.isVisible = false
            }
        } else {
            // Show validation message if user has started but not completed a review
            val hasPartialInput = serviceComment.isNotEmpty() || providerComment.isNotEmpty()

            if (hasPartialInput) {
                binding.validationMessageTextView.isVisible = true
                binding.validationMessageTextView.text = context.getString(R.string.rating_required_message)
            } else {
                binding.validationMessageTextView.isVisible = false
            }
        }
    }

    private fun getValidationMessage(): String {
        val messages = mutableListOf<String>()

        // Check if service comment exists but doesn't match comment size
        if (serviceComment.isNotEmpty() && serviceComment.length < ReviewModel.MIN_COMMENT_LENGTH) {
            messages.add(
                "Service review needs " +
                        "${ReviewModel.MIN_COMMENT_LENGTH - serviceComment.length}" +
                        " more characters"
            )
        }

        // Check if provider comment exists but doesn't match comment size
        if (providerComment.isNotEmpty() && providerComment.length < ReviewModel.MIN_COMMENT_LENGTH) {
            messages.add("Provider review needs ${ReviewModel.MIN_COMMENT_LENGTH - providerComment.length} more characters")
        }

        return if (messages.isEmpty()) {
            "Please provide at least one rating to submit a review"
        } else {
            messages.joinToString(". ")
        }
    }

    private fun submitReview() {
        val review = ReviewModel(
            bookingId = booking.id,
            listingId = booking.listingId,
            listingTitle = booking.listingTitle,
            providerId = booking.providerId,
            providerName = booking.providerName,
            clientId = booking.clientId,
            clientName = booking.clientName,
            serviceRating = if (serviceRating > 0) serviceRating else null,
            serviceComment = serviceComment.trim(),
            providerRating = if (providerRating > 0) providerRating else null,
            providerComment = providerComment.trim(),
            isAnonymous = binding.anonymousCheckBox.isChecked
        )

        // validate
        val validationErrors = review.validate()
        if (validationErrors.isNotEmpty()) {
            binding.validationMessageTextView.isVisible = true
            binding.validationMessageTextView.text = validationErrors.joinToString(". ")
            return
        }

        // submit
        onReviewSubmitted(review)
        dismiss()
    }
} 